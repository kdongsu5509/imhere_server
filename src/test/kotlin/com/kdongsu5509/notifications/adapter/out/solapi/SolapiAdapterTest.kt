package com.kdongsu5509.notifications.adapter.out.solapi


import com.kdongsu5509.notifications.config.ExternalSMSProperties
import com.kdongsu5509.notifications.domain.SMS
import com.kdongsu5509.support.exception.BusinessException
import com.kdongsu5509.support.exception.ExternalSMSErrorCode
import com.solapi.sdk.message.service.DefaultMessageService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyList
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

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

    @Nested
    inner class SendMultipleTest {
        @Test
        @DisplayName("다중 발송 - 성공")
        fun sendMultiple_success() {
            val testMultiSMS = listOf(
                SMS("${TEST_NICKNAME}1", "01011112222", "서울역"),
                SMS("${TEST_NICKNAME}2", "01033334444", "판교역")
            )

            solapiAdapter.sendMultiple(testMultiSMS)

            verify(messageService).send(anyList(), any())
        }

        @Test
        @DisplayName("다중 발송 - 실페 : 빈 요청")
        fun sendMultiple_fail_empty_request() {
            val testMultiSMS = listOf<SMS>()

            assertThrows<BusinessException> {
                solapiAdapter.sendMultiple(testMultiSMS)
            }.also {
                assertThat(it.errorCode).isEqualTo(ExternalSMSErrorCode.NOT_ALLOW_EMPTY)
            }
        }

        /**
         * TODO : 추후 `sendMultiple` 에 대한 누락된 테스트 코드 추가 예정
         * - 우선 2026-03-27 솔라피 발송 코드는 가정에 의해 오류를 처리하고 있음.
         * - 해당 부분에 대한 핸들링 로직의 정합성을 보장할 수 없어 우선적으로는 테스트 커버리지를 약간 포기하는 방식을 선택.
         * - 프로덕션 관련 부분에 대한 로그를 찍도록 작성한 후, 차후 보완하는 방향으로 수정 예정
         * - 연관 클래스
         *  1. SolapiAdapter : 메인 로직
         *  2. ExternalSMSErrorCode : 오류 메시지 보관소
         *  3. SMSService : 구현체 SolapiAdapter의 인터페이스를 사용하는 곳
         */
    }
}