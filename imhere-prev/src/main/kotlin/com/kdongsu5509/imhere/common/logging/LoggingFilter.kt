package com.kdongsu5509.imhere.common.logging

import com.fasterxml.jackson.databind.ObjectMapper
import com.kdongsu5509.imhere.common.alert.port.out.MessageSendPort
import com.kdongsu5509.imhere.common.logging.domain.AccessLog
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

class LoggingFilter(
    private val messageSendPort: MessageSendPort,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (isIgnoredUrl(request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }

        val wrappedRequest = ContentCachingRequestWrapper(request)
        val wrappedResponse = ContentCachingResponseWrapper(response)

        val requestAt = LocalDateTime.now()
        val traceId = UUID.randomUUID().toString()
        MDC.put("traceId", traceId)

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse)
        } finally {
            val responseAt = LocalDateTime.now()
            val durationMs = Duration.between(requestAt, responseAt).toMillis()

            val accessLog = AccessLog.createAccessLogFromReqAndResp(
                wrappedRequest,
                wrappedResponse,
                traceId,
                requestAt,
                responseAt,
                durationMs
            )

            logFormattedAccessLog(accessLog)

            wrappedResponse.copyBodyToResponse()
            MDC.clear()
        }
    }

    private fun isIgnoredUrl(uri: String): Boolean {
        return uri.startsWith("/actuator") || uri.startsWith("/health") || uri.startsWith("/favicon.ico")
    }

    private fun logFormattedAccessLog(accessLog: AccessLog) {
        val loggingContents = StringBuilder()
        loggingContents.append("\n")

        createRequestLog(accessLog, loggingContents)
        createResponseLog(accessLog, loggingContents)

        if (accessLog.status >= 400) {
            messageSendPort.sendMessage("## 🚨 HTTP Error\n\n```json\n$loggingContents\n```")
        }

        log.info(loggingContents.toString())
    }

    private fun createHeader(sb: StringBuilder, isRequest: Boolean, isError: Boolean) {
        val requestHeader = "✈️ --- [Request] ---------------------------\n"
        val errorRequestHeader = "🚨 --- [Error Request] --------------------------\n"

        val responseHeader = "🚀 --- [Response] --------------------------\n"
        val errorResponseHeader = "❌ --- [Error Response] --------------------------\n"

        val header = if (isRequest) {
            if (isError) errorRequestHeader else requestHeader
        } else {
            if (isError) errorResponseHeader else responseHeader
        }
        sb.append(header)
    }

    private fun createRequestLog(accessLog: AccessLog, sb: StringBuilder) {
        createHeader(sb, isRequest = true, isError = accessLog.status >= 400)

        sb.append("ID:       ").append(accessLog.traceId).append("\n")
        sb.append("Method:   ").append(accessLog.method).append("\n")
        sb.append("URI:      ").append(accessLog.uri)

        if (!accessLog.queryString.isNullOrEmpty()) {
            sb.append("?").append(accessLog.queryString)
        }
        sb.append("\n")

        sb.append("From:     ").append(accessLog.remoteIp).append("\n")
        sb.append("User-Agent: ").append(accessLog.userAgent).append("\n")

        sb.append("Headers:  ").append(accessLog.headers).append("\n")

        if (accessLog.requestBody.isNotEmpty()) {
            sb.append("Request:  ").append("\n")
            sb.append(formatAndMaskBody(accessLog.requestBody)).append("\n")
        }
    }

    private fun createResponseLog(accessLog: AccessLog, sb: StringBuilder) {
        createHeader(sb, isRequest = false, isError = accessLog.status >= 400)

        sb.append("Status:   ").append(accessLog.status).append("\n")
        sb.append("Duration: ").append(accessLog.durationMs).append("ms\n")

        if (accessLog.responseBody.isNotEmpty()) {
            sb.append("Response: ").append("\n")
            sb.append(formatAndMaskBody(accessLog.responseBody)).append("\n")
        }
        sb.append("--------------------------------------------------\n")
    }

    private fun formatAndMaskBody(body: String): String {
        return try {
            val jsonNode = objectMapper.readTree(body)
            val prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode)

            prettyJson.replace(Regex("\"(password|pw|confirmPassword|secret)\"\\s*:\\s*\"[^\"]+\"")) { matchResult ->
                "\"${matchResult.groupValues[1]}\": \"*****\""
            }
        } catch (e: Exception) {
            body
        }
    }
}