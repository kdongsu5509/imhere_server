package com.kdongsu5509.support.external

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.ott.OneTimeToken
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler
import org.springframework.stereotype.Component

@Component
class DiscordOttSuccessHandler(
    private val discordMessageSender: DiscordMessageSender,
    @param:Value("\${discord.url.ott}") private val ottAlertChannelWebhookUrl: String
) : OneTimeTokenGenerationSuccessHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        oneTimeToken: OneTimeToken
    ) {
        val message = createDiscordMessageWithOneTimeToken(oneTimeToken)
        discordMessageSender.sendMessage(ottAlertChannelWebhookUrl, message)

        configureSuccessResponse(response)
    }

    private fun createDiscordMessageWithOneTimeToken(oneTimeToken: OneTimeToken): DiscordMessageDto {
        val tokenValue = oneTimeToken.tokenValue

        val message = """
                        **ImHere Admin OTT Login Requested**
                        User: ${oneTimeToken.username}
                        Token: $tokenValue
                        Expires: 10 minutes
                    """.trimIndent()
        return DiscordMessageDto(message)
    }

    private fun configureSuccessResponse(response: HttpServletResponse) {
        response.status = HttpServletResponse.SC_OK
        response.contentType = "text/plain"
        response.characterEncoding = "UTF-8"
        response.writer.write("Token has been sent to Discord. Please check your channel.")
    }
}
