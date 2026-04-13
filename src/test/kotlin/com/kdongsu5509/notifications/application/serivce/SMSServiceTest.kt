package com.kdongsu5509.notifications.application.serivce

import com.kdongsu5509.notifications.application.port.out.ExternalMessagePort
import com.kdongsu5509.notifications.domain.SMS
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class SMSServiceTest {

    companion object {
        const val SENDER_NICKNAME = "라티"
        const val RECEIVER_NUMBER = "01012345678"
        const val LOCATION = "판교역"
    }

    @Mock
    private lateinit var externalMessagePort: ExternalMessagePort

    @InjectMocks
    private lateinit var smsService: SMSService

    @Test
    @DisplayName("문자 잘 보낸다")
    fun send_good() {
        // given
        val sms = SMS(
            senderNickname = SENDER_NICKNAME,
            receiverNumber = RECEIVER_NUMBER,
            location = LOCATION
        )

        // when, then
        assertDoesNotThrow {
            smsService.send(SENDER_NICKNAME, RECEIVER_NUMBER, LOCATION)
        }
    }

    @Test
    @DisplayName("단일 문자 발송 시 ExternalMessagePort.send 호출 검증")
    fun send_calls_external_port() {
        // when
        smsService.send(SENDER_NICKNAME, RECEIVER_NUMBER, LOCATION)

        // then
        verify(externalMessagePort).send(
            SMS(SENDER_NICKNAME, RECEIVER_NUMBER, LOCATION)
        )
    }

    @Test
    @DisplayName("다중 문자 발송 성공")
    fun sendMultiple_good() {
        // given
        val receiverNumbers = listOf("01011112222", "01033334444")

        // when, then
        assertDoesNotThrow {
            smsService.sendMultiple(SENDER_NICKNAME, receiverNumbers, LOCATION)
        }
    }

    @Test
    @DisplayName("다중 문자 발송 시 ExternalMessagePort.sendMultiple 호출 검증")
    fun sendMultiple_calls_external_port() {
        // given
        val receiverNumbers = listOf("01011112222", "01033334444")
        val expectedSMSList = receiverNumbers.map { SMS(SENDER_NICKNAME, it, LOCATION) }

        // when
        smsService.sendMultiple(SENDER_NICKNAME, receiverNumbers, LOCATION)

        // then
        verify(externalMessagePort).sendMultiple(expectedSMSList)
    }
}
