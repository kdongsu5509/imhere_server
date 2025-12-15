package com.kdongsu5509.imhere.common.exceptionHandler.adapter.out.discord

import com.kdongsu5509.imhere.common.exceptionHandler.dto.ErrorAlertMessage
import com.kdongsu5509.imhere.common.exceptionHandler.port.out.MessageSendPort
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class DiscordMessageService(
    private val restClientBuilder: RestClient.Builder
) : MessageSendPort {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val discordBaseUrl = "https://discord.com"

    @Value("\${discord.url}")
    lateinit var specificWebhookUrl: String

    override fun sendMessage(content: String) {
        try {
            val message = ErrorAlertMessage(content)
            sendToDiscord(message)
            log.info("디스코드 알림 전송 성공: {}", content)
        } catch (e: Exception) {
            log.error("디스코드 알림 전송 실패", e)
        }
    }

    override fun sendDetailMessage(e: Throwable) {
        sendMessage("Exception occurred: ${e.message}")
    }

    private fun sendToDiscord(message: ErrorAlertMessage) {
        val webClient = restClientBuilder.baseUrl(discordBaseUrl).build()

        webClient.post()
            .uri(specificWebhookUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .body(message)
            .retrieve()
            .toBodilessEntity()
    }
}