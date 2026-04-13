package com.kdongsu5509.notifications.adapter.out.solapi


import com.kdongsu5509.notifications.config.ExternalSMSProperties
import com.kdongsu5509.notifications.domain.SMS
import com.kdongsu5509.support.exception.BusinessException
import com.kdongsu5509.support.exception.ExternalSMSErrorCode
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException
import com.solapi.sdk.message.service.DefaultMessageService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.isNull
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import com.solapi.sdk.message.model.Message as SolapiMessage

/**
 * Kotlin 2.0+ 에서 Java 플랫폼 타입(T!)을 non-null 파라미터로 전달하면
 * 컴파일러가 checkNotNullExpressionValue를 생성해 NPE가 발생합니다.
 * Any?를 제네릭 T로 캐스팅하면 JVM 타입 소거(type erasure)로 런타임 체크가 없어 안전합니다.
 */
@Suppress("UNCHECKED_CAST")
private fun <T> anyMatcher(): T {
    val result: Any? = any<T>()
    return result as T  // unchecked cast via type erasure - no runtime TypeCastException
}

@ExtendWith(MockitoExtension::class)
class SolapiAdapterTest {
    companion object {
        const val TEST_SENDER = "01012345678"
        const val TEST_NICKNAME = "라티"
        const val TEST_REC_NUM = "01098765432"
        const val TEST_LOC = "강남역"
    }

    @Mock
    private lateinit var externalSMSProperties: ExternalSMSProperties

    @Mock
    private lateinit var messageService: DefaultMessageService

    @InjectMocks
    private lateinit var solapiAdapter: SolapiAdapter

    @Test
    fun `단일 메시지 발송 테스트`() {
        `when`(externalSMSProperties.sender).thenReturn(TEST_SENDER)

        val sms = SMS(
            senderNickname = TEST_NICKNAME,
            receiverNumber = TEST_REC_NUM,
            location = TEST_LOC
        )

        assertDoesNotThrow {
            solapiAdapter.send(sms)
        }
    }

    @Test
    @DisplayName("단일 발송 - SolapiMessageNotReceivedException 발생 시 fail 응답 반환")
    fun send_message_not_received_exception_returns_fail() {
        `when`(externalSMSProperties.sender).thenReturn(TEST_SENDER)
        doThrow(SolapiMessageNotReceivedException("발송 미접수 테스트"))
            .`when`(messageService).send(anyMatcher<SolapiMessage>(), isNull())

        val result = solapiAdapter.send(SMS(TEST_NICKNAME, TEST_REC_NUM, TEST_LOC))

        assertThat(result.status).isEqualTo(SolapiResponse.FAIL_STATUS)
        assertThat(result.message).contains("발송 미접수")
    }

    @Test
    @DisplayName("단일 발송 - 알 수 없는 예외 발생 시 fail 응답 반환")
    fun send_unknown_exception_returns_fail() {
        `when`(externalSMSProperties.sender).thenReturn(TEST_SENDER)
        doThrow(RuntimeException("알 수 없는 오류"))
            .`when`(messageService).send(anyMatcher<SolapiMessage>(), isNull())

        val result = solapiAdapter.send(SMS(TEST_NICKNAME, TEST_REC_NUM, TEST_LOC))

        assertThat(result.status).isEqualTo(SolapiResponse.FAIL_STATUS)
    }

    @Nested
    inner class SendMultipleTest {
        @Test
        @DisplayName("다중 발송 - 성공")
        fun sendMultiple_success() {
            `when`(externalSMSProperties.sender).thenReturn(TEST_SENDER)

            val testMultiSMS = listOf(
                SMS("${TEST_NICKNAME}1", "01011112222", "서울역"),
                SMS("${TEST_NICKNAME}2", "01033334444", "판교역")
            )

            solapiAdapter.sendMultiple(testMultiSMS)

            verify(messageService).send(anyList(), isNull())
        }

        @Test
        @DisplayName("다중 발송 - 실패 : 빈 요청")
        fun sendMultiple_fail_empty_request() {
            val testMultiSMS = listOf<SMS>()

            assertThrows<BusinessException> {
                solapiAdapter.sendMultiple(testMultiSMS)
            }.also {
                assertThat(it.errorCode).isEqualTo(ExternalSMSErrorCode.NOT_ALLOW_EMPTY)
            }
        }

        @Test
        @DisplayName("다중 발송 - 예외 발생 시 모든 항목에 fail 응답 반환")
        fun sendMultiple_exception_returns_fail_for_all() {
            `when`(externalSMSProperties.sender).thenReturn(TEST_SENDER)
            doThrow(RuntimeException("네트워크 오류"))
                .`when`(messageService).send(anyList(), isNull())

            val multiSMS = listOf(
                SMS("${TEST_NICKNAME}1", "01011112222", "서울역"),
                SMS("${TEST_NICKNAME}2", "01033334444", "판교역")
            )

            val results = solapiAdapter.sendMultiple(multiSMS)

            assertThat(results).hasSize(2)
            assertThat(results).allMatch { it.status == SolapiResponse.FAIL_STATUS }
        }

        @Test
        @DisplayName("다중 발송 - SolapiMessageNotReceivedException 발생 시 모든 항목에 fail 응답 반환")
        fun sendMultiple_message_not_received_exception_returns_fail_for_all() {
            `when`(externalSMSProperties.sender).thenReturn(TEST_SENDER)
            doThrow(SolapiMessageNotReceivedException("발송 미접수 테스트"))
                .`when`(messageService).send(anyList(), isNull())

            val multiSMS = listOf(
                SMS(TEST_NICKNAME, "01011112222", TEST_LOC),
                SMS(TEST_NICKNAME, "01033334444", TEST_LOC)
            )

            val results = solapiAdapter.sendMultiple(multiSMS)

            assertThat(results).hasSize(2)
            assertThat(results).allMatch { it.status == SolapiResponse.FAIL_STATUS }
        }
    }
}
