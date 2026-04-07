package com.kdongsu5509.user.adapter.out.messageQueue

import com.kdongsu5509.user.adapter.out.messageQueue.dto.NotificationMessageDto
import com.kdongsu5509.user.adapter.out.messageQueue.dto.NotificationType
import com.kdongsu5509.user.application.dto.AlertInformation
import com.kdongsu5509.user.application.port.out.noti.TermAlertPort
import com.kdongsu5509.user.common.config.UserRabbitMQConfig
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class TermAlertExternalMessageQueueAdapter(val rabbitTemplate: RabbitTemplate) : TermAlertPort {
    override fun sendAlert(alertInformation: AlertInformation) {
        rabbitTemplate.convertAndSend(
            UserRabbitMQConfig.EXCHANGE_NAME,
            UserRabbitMQConfig.SERVICE_ROUTING_KEY,
            consistNotificationMessageDto(alertInformation)
        )
    }

    private fun consistNotificationMessageDto(alertInformation: AlertInformation): NotificationMessageDto {
        val externalMessageDto = NotificationMessageDto(
            type = NotificationType.TERMS_UPDATE,
            senderEmail = alertInformation.senderNickname,
            receiverEmail = alertInformation.receiverEmail,
            message = alertInformation.body,
            data = mapOf(
                "targetScreen" to "/friend/list"
            )
        )
        return externalMessageDto
    }
}
