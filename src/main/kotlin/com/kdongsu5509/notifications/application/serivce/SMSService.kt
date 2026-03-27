package com.kdongsu5509.notifications.application.serivce

import com.kdongsu5509.notifications.application.port.`in`.MessageSendUseCasePort
import com.kdongsu5509.notifications.application.port.out.ExternalMessagePort
import com.kdongsu5509.notifications.domain.SMS
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SMSService(private val externalMessagePort: ExternalMessagePort) : MessageSendUseCasePort {
    override fun send(senderNickname: String, receiverNumber: String, location: String) {
        //TODO : 문자 발송 데이터 저장 필요
        val sms = SMS(
            senderNickname = senderNickname,
            receiverNumber = receiverNumber,
            location = location
        )

        externalMessagePort.send(sms)
    }

    override fun sendMultiple(
        senderNickname: String,
        receiverNumbers: List<String>,
        location: String
    ) {
        val multipleSMS = receiverNumbers.stream()
            .map { it ->
                SMS(senderNickname, it, location)
            }.toList()

        externalMessagePort.sendMultiple(multipleSMS)
    }
}