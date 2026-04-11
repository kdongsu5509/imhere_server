package com.kdongsu5509.support.external

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.net.URI

@Component
class DiscordMessageSender(
    private val discordApiClient: DiscordApiClient
) : DiscordMessageSendPort {

    private val logger = LoggerFactory.getLogger(DiscordMessageSender::class.java)

    @Async("discordExecutor")
    override fun sendMessage(webHookUrl: String, content: DiscordMessageDto) {
        if (isWebhookUrlEmpty(webHookUrl)) return

        try {
            discordApiClient.sendMessage(
                URI.create(webHookUrl),
                content
            )
            logger.info("디스코드 알림 전송 성공: {}", content)
        } catch (e: Exception) {
            logger.error("디스코드 알림 전송 실패", e)
        }
    }

    private fun isWebhookUrlEmpty(webHookUrl: String): Boolean {
        if (webHookUrl.isEmpty()) {
            logger.warn("Discord webhook URL not configured. Skipping message send.")
            return true
        }
        return false
    }
}
