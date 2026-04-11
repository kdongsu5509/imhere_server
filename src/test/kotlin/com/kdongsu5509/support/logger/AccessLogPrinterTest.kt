package com.kdongsu5509.support.logger

import com.kdongsu5509.support.external.DiscordMessageDto
import com.kdongsu5509.support.external.DiscordMessageSendPort
import org.assertj.core.api.Assertions.assertThat
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
import org.mockito.kotlin.argumentCaptor
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
    @DisplayName("5xx 에러 발생 시 Discord 서버 에러 알림을 전송한다")
    fun print_sendsAlert_whenStatus5xx() {
        ReflectionTestUtils.setField(accessLogPrinter, "errorAlertChannelWebhookUrl", webhookUrl)
        val accessLog = buildAccessLog(500)

        accessLogPrinter.print(accessLog, sendAlert = true)

        val captor = argumentCaptor<DiscordMessageDto>()
        verify(discordMessageSendPort).sendMessage(any(), captor.capture())
        assertThat(captor.firstValue.content).contains("🔥")
        assertThat(captor.firstValue.content).contains("500")
        assertThat(captor.firstValue.content).contains(accessLog.traceId)
    }

    @Test
    @DisplayName("sendAlert=false이면 5xx여도 Discord 알림을 전송하지 않는다")
    fun print_doesNotSendAlert_whenSendAlertFalse() {
        ReflectionTestUtils.setField(accessLogPrinter, "errorAlertChannelWebhookUrl", webhookUrl)
        val accessLog = buildAccessLog(500)

        accessLogPrinter.print(accessLog, sendAlert = false)

        verify(discordMessageSendPort, never()).sendMessage(any(), any<DiscordMessageDto>())
    }

    @Test
    @DisplayName("4xx 클라이언트 에러는 Discord 알림을 전송하지 않는다")
    fun print_doesNotSendAlert_whenStatus4xx() {
        ReflectionTestUtils.setField(accessLogPrinter, "errorAlertChannelWebhookUrl", webhookUrl)

        listOf(400, 401, 403, 404, 422, 429).forEach { status ->
            accessLogPrinter.print(buildAccessLog(status), sendAlert = true)
        }

        verify(discordMessageSendPort, never()).sendMessage(any(), any<DiscordMessageDto>())
    }

    @Test
    @DisplayName("2xx 정상 응답은 Discord 알림을 전송하지 않는다")
    fun print_doesNotSendAlert_whenStatus2xx() {
        ReflectionTestUtils.setField(accessLogPrinter, "errorAlertChannelWebhookUrl", webhookUrl)
        val accessLog = buildAccessLog(200)

        accessLogPrinter.print(accessLog, sendAlert = true)

        verify(discordMessageSendPort, never()).sendMessage(any(), any<DiscordMessageDto>())
    }

    @Test
    @DisplayName("webhookUrl이 null이면 5xx여도 Discord 알림을 전송하지 않는다")
    fun print_doesNotSendAlert_whenWebhookUrlIsNull() {
        val accessLog = buildAccessLog(500)

        accessLogPrinter.print(accessLog, sendAlert = true)

        verify(discordMessageSendPort, never()).sendMessage(any(), any<DiscordMessageDto>())
    }

    @Test
    @DisplayName("status가 정확히 500이면 Discord 알림을 전송한다")
    fun print_sendsAlert_whenStatusIsExactly500() {
        ReflectionTestUtils.setField(accessLogPrinter, "errorAlertChannelWebhookUrl", webhookUrl)
        val accessLog = buildAccessLog(500)

        accessLogPrinter.print(accessLog, sendAlert = true)

        verify(discordMessageSendPort).sendMessage(any(), any<DiscordMessageDto>())
    }
}
