package com.kdongsu5509.notifications.adapter.`in`.messageQueue

import com.kdongsu5509.notifications.adapter.`in`.messageQueue.dto.NotificationMessageDto
import com.kdongsu5509.notifications.application.port.`in`.NotificationToUserCasePort
import com.kdongsu5509.notifications.config.NotificationRabbitMQConfig
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class TermNotificationConsumer(
    private val notificationToUserCasePort: NotificationToUserCasePort
) {
    @RabbitListener(queues = [NotificationRabbitMQConfig.SERVICE_QUEUE])
    fun receiveMessage(dto: NotificationMessageDto) {
        notificationToUserCasePort.send(dto.receiverEmail, dto.type.name, dto.message)
    }
}
