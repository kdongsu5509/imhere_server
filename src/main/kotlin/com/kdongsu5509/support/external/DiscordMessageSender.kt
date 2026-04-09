package com.kdongsu5509.support.external

import com.kdongsu5509.support.logger.ErrorAlertMessage
import com.kdongsu5509.support.logger.MessageSendPort
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class DiscordMessageSender(
    restClientBuilder: RestClient.Builder
) : MessageSendPort {

    private val logger = LoggerFactory.getLogger(DiscordMessageSender::class.java)
    private val restClient = restClientBuilder.build()

    override fun sendMessage(webHookUrl: String, content: String) {
        if (webHookUrl.isEmpty()) {
            logger.warn("Discord webhook URL not configured. Skipping message send.")
            return
        }

        try {
            val message = ErrorAlertMessage(content)
            sendToDiscord(webHookUrl, message)
            logger.info("디스코드 알림 전송 성공: {}", content)
        } catch (e: Exception) {
            logger.error("디스코드 알림 전송 실패", e)
        }
    }

    private fun sendToDiscord(webHookUrl: String, message: ErrorAlertMessage) {
        restClient.post()
            .uri(webHookUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .body(message)
            .retrieve()
            .toBodilessEntity()
    }
}
