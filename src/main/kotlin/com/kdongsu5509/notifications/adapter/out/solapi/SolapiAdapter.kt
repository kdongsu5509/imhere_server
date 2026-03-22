package com.kdongsu5509.notifications.adapter.out.solapi

import com.kdongsu5509.notifications.config.ExternalSMSProperties
import com.kdongsu5509.notifications.domain.SMS
import com.solapi.sdk.SolapiClient
import com.solapi.sdk.message.dto.response.MultipleDetailMessageSentResponse
import com.solapi.sdk.message.model.Message
import com.solapi.sdk.message.service.DefaultMessageService

class SolapiAdapter(private val externalSMSProperties: ExternalSMSProperties) {

    companion object {
        const val MSG_FORMAT = "[도착 알림 서비스, ImHere]\n" +
                "보낸 사람: %s\n" +
                "내용:%s에 도착하였습니다"
    }

    private val externalMessageService: DefaultMessageService = SolapiClient.createInstance(
        externalSMSProperties.apiKey,
        externalSMSProperties.apiSecret,
    )

    fun send(sms: SMS) {
        val message = buildExternalSMSMessage(sms)

        val response: MultipleDetailMessageSentResponse = externalMessageService.send(message)
        println("Group ID: ${response.groupInfo?.groupId}")
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