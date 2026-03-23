package com.kdongsu5509.notifications.adapter.out.solapi

import com.kdongsu5509.notifications.application.port.out.ExternalMessagePort
import com.kdongsu5509.notifications.config.ExternalSMSProperties
import com.kdongsu5509.notifications.domain.SMS
import com.solapi.sdk.message.model.Message
import com.solapi.sdk.message.service.DefaultMessageService
import org.springframework.stereotype.Component


@Component
class SolapiAdapter(
    private val externalSMSProperties: ExternalSMSProperties,
    private val solapiService: DefaultMessageService,
) : ExternalMessagePort {

    companion object {
        const val MSG_FORMAT = "[도착 알림 서비스, ImHere]\n" +
                "보낸 사람: %s\n" +
                "내용:%s에 도착하였습니다"
    }

    override fun send(sms: SMS) {
        val message = buildExternalSMSMessage(sms)

        solapiService.send(message)
    }

    override fun sendMultiple(multiSMS: List<SMS>) {
        val messages: MutableList<Message> = ArrayList()
        for (sms in multiSMS) {
            val msg = Message(
                from = externalSMSProperties.sender,
                to = sms.receiverNumber,
                text = String.format(
                    MSG_FORMAT, sms.senderNickname, sms.location
                )
            )
            messages.add(msg)
        }

        solapiService.send(messages, null)
    }

    private fun buildExternalSMSMessage(sms: SMS): Message {
        val message = Message(
            from = externalSMSProperties.sender,
            to = sms.receiverNumber,
            text = String.format(
                MSG_FORMAT, sms.senderNickname, sms.location
            )
        )
        return message
    }
}