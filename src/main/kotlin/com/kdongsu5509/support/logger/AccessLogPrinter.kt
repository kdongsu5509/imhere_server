package com.kdongsu5509.support.logger

import com.kdongsu5509.support.external.DiscordMessageDto
import com.kdongsu5509.support.external.DiscordMessageSendPort
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AccessLogPrinter(
    private val discordMessageSendPort: DiscordMessageSendPort,
    private val formatter: AccessLogFormatter
) {

    @Value("\${discord.url.error:}")
    private val errorAlertChannelWebhookUrl: String? = null

    private val log = LoggerFactory.getLogger(AccessLogPrinter::class.java)

    fun print(accessLog: AccessLog, sendAlert: Boolean) {
        val formatted = formatter.format(accessLog)
        log.info(formatted)
        sendAlertIfNeeded(accessLog, formatted, sendAlert)
    }

    private fun sendAlertIfNeeded(accessLog: AccessLog, formatted: String, sendAlert: Boolean) {

        if (sendAlert && accessLog.status >= 400) {
            errorAlertChannelWebhookUrl?.let {
                discordMessageSendPort.sendMessage(
                    it,
                    DiscordMessageDto("## 🚨 HTTP Error\n\n```json\n$formatted\n```")
                )
            }
        }
    }
}
