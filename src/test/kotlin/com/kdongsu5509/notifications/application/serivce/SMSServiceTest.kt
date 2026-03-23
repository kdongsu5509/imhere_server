package com.kdongsu5509.notifications.application.serivce

import com.kdongsu5509.notifications.application.port.out.ExternalMessagePort
import com.kdongsu5509.notifications.domain.SMS
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.bean.override.mockito.MockitoBean

@ExtendWith(MockitoExtension::class)
class SMSServiceTest {

    companion object {
        const val senderNickname = "라티"
        const val receiverNumber = "01012345678"
        const val location = "판교역"
    }

    @MockitoBean
    private lateinit var externalMessagePort: ExternalMessagePort

    @InjectMocks
    private lateinit var smsService: SMSService


    @Test
    @DisplayName("문자 잘 보낸다")
    fun send_good() {
        //given
        val sms = SMS(
            senderNickname = senderNickname,
            receiverNumber = receiverNumber,
            location = location
        )
        `when`(externalMessagePort.send(sms)).then { }

        //when, then
        assertDoesNotThrow {
            smsService.send(senderNickname, receiverNumber, location)
        }
    }

}

//package com.kdongsu5509.notifications.application.serivce
//
//import com.kdongsu5509.notifications.application.port.`in`.MessageSendUseCasePort
//import com.kdongsu5509.notifications.application.port.out.ExternalMessagePort
//import com.kdongsu5509.notifications.domain.SMS
//import org.springframework.stereotype.Service
//import org.springframework.transaction.annotation.Transactional
//
//@Service
//@Transactional
//class SMSService(private val externalMessagePort: ExternalMessagePort) : MessageSendUseCasePort {
//    override fun send(senderNickname: String, receiverNumber: String, location: String) {
//        //TODO : 문자 발송 데이터 저장 필요
//        val sms = SMS(
//            senderNickname = senderNickname,
//            receiverNumber = receiverNumber,
//            location = location
//        )
//
//        externalMessagePort.send(sms)
//    }
//
//    override fun sendMultiple(
//        senderNickname: String,
//        receiverNumbers: List<String>,
//        location: String
//    ) {
//        val multipleSMS = receiverNumbers.stream()
//            .map { it ->
//                SMS(senderNickname, it, location)
//            }.toList()
//
//        externalMessagePort.sendMultiple(multipleSMS)
//    }
//}