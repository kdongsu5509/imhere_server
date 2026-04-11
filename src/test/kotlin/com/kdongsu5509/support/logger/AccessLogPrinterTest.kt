package com.kdongsu5509.support.logger

import com.kdongsu5509.support.external.DiscordMessageDto
import com.kdongsu5509.support.external.DiscordMessageSendPort
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.test.util.ReflectionTestUtils
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class AccessLogPrinterTest {

    @Mock
    private lateinit var discordMessageSendPort: DiscordMessageSendPort

    @Mock
    private lateinit var formatter: AccessLogFormatter

    private lateinit var accessLogPrinter: AccessLogPrinter

    private val webhookUrl = "https://discord.com/api/webhooks/test-error"

    private fun buildAccessLog(status: Int) = AccessLog(
        traceId = "trace-id",
        method = "GET",
        uri = "/test",
        queryString = null,
        requestBody = "",
        responseBody = "",
        headers = emptyMap(),
        userAgent = null,
        remoteIp = "127.0.0.1",
        status = status,
        threadName = "main",
        requestAt = LocalDateTime.now(),
        responseAt = LocalDateTime.now(),
        durationMs = 10L
    )

    @BeforeEach
    fun setUp() {
        accessLogPrinter = AccessLogPrinter(discordMessageSendPort, formatter)
        `when`(formatter.format(any())).thenReturn("{}")
    }

    @Test
    @DisplayName("sendAlert=true이고 status가 400 이상이면 Discord 알림을 전송한다")
    fun print_sendsAlert_whenSendAlertTrueAndStatusAbove400() {
        ReflectionTestUtils.setField(accessLogPrinter, "errorAlertChannelWebhookUrl", webhookUrl)
        val accessLog = buildAccessLog(500)

        accessLogPrinter.print(accessLog, sendAlert = true)

        verify(discordMessageSendPort).sendMessage(any(), any<DiscordMessageDto>())
    }

    @Test
    @DisplayName("sendAlert=false이면 status와 관계없이 Discord 알림을 전송하지 않는다")
    fun print_doesNotSendAlert_whenSendAlertFalse() {
        ReflectionTestUtils.setField(accessLogPrinter, "errorAlertChannelWebhookUrl", webhookUrl)
        val accessLog = buildAccessLog(500)

        accessLogPrinter.print(accessLog, sendAlert = false)

        verify(discordMessageSendPort, never()).sendMessage(any(), any<DiscordMessageDto>())
    }

    @Test
    @DisplayName("status가 400 미만이면 sendAlert=true여도 Discord 알림을 전송하지 않는다")
    fun print_doesNotSendAlert_whenStatusBelow400() {
        ReflectionTestUtils.setField(accessLogPrinter, "errorAlertChannelWebhookUrl", webhookUrl)
        val accessLog = buildAccessLog(200)

        accessLogPrinter.print(accessLog, sendAlert = true)

        verify(discordMessageSendPort, never()).sendMessage(any(), any<DiscordMessageDto>())
    }

    @Test
    @DisplayName("webhookUrl이 null이면 Discord 알림을 전송하지 않는다")
    fun print_doesNotSendAlert_whenWebhookUrlIsNull() {
        val accessLog = buildAccessLog(500)

        accessLogPrinter.print(accessLog, sendAlert = true)

        verify(discordMessageSendPort, never()).sendMessage(any(), any<DiscordMessageDto>())
    }

    @Test
    @DisplayName("status가 정확히 400이면 Discord 알림을 전송한다")
    fun print_sendsAlert_whenStatusIsExactly400() {
        ReflectionTestUtils.setField(accessLogPrinter, "errorAlertChannelWebhookUrl", webhookUrl)
        val accessLog = buildAccessLog(400)

        accessLogPrinter.print(accessLog, sendAlert = true)

        verify(discordMessageSendPort).sendMessage(any(), any<DiscordMessageDto>())
    }
}
