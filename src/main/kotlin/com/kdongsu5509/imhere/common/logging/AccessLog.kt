package com.kdongsu5509.imhere.common.logging

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

data class AccessLog(
    val traceId: String,
    val method: String,
    val uri: String,
    val queryString: String?,
    val requestBody: String,
    val responseBody: String,
    val headers: Map<String, String>,
    val userAgent: String?,
    val remoteIp: String,
    val status: Int,
    val threadName: String,
    val requestAt: LocalDateTime,
    val responseAt: LocalDateTime,
    val durationMs: Long
) {
    // data class의 기본 toString()을 JSON 포맷으로 오버라이딩
    override fun toString(): String {
        return try {
            // companion object에 있는 objectMapper 재사용 (메모리 절약)
            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
        } catch (e: Exception) {
            "Log JSON Parsing Error"
        }
    }

    companion object {
        // ★ 핵심 변경: 여기에 선언하면 Java의 'private static final'과 동일하게 동작합니다.
        // 클래스 로딩 시점에 딱 한 번만 생성됩니다.
        private val objectMapper = ObjectMapper()

        fun extractClientIp(request: HttpServletRequest): String {
            val forwarded = request.getHeader("X-Forwarded-For")
            if (!forwarded.isNullOrEmpty()) {
                return forwarded.split(",")[0].trim()
            }
            return request.remoteAddr
        }

        fun extractHeaders(request: HttpServletRequest): Map<String, String> {
            val headerMap = mutableMapOf<String, String>()
            val headerNames = request.headerNames
            while (headerNames.hasMoreElements()) {
                val headerName = headerNames.nextElement()
                request.getHeader(headerName)?.let {
                    headerMap[headerName] = it
                }
            }
            return headerMap
        }

        fun createAccessLogFromReqAndResp(
            wrappedRequest: ContentCachingRequestWrapper,
            wrappedResponse: ContentCachingResponseWrapper,
            traceId: String,
            requestAt: LocalDateTime,
            responseAt: LocalDateTime,
            durationMs: Long
        ): AccessLog {
            return AccessLog(
                traceId = traceId,
                requestAt = requestAt,
                responseAt = responseAt,
                durationMs = durationMs,
                threadName = Thread.currentThread().name,
                method = wrappedRequest.method,
                uri = wrappedRequest.requestURI,
                queryString = wrappedRequest.queryString,
                headers = extractHeaders(wrappedRequest),
                remoteIp = extractClientIp(wrappedRequest),
                userAgent = wrappedRequest.getHeader("User-Agent"),
                status = wrappedResponse.status,
                requestBody = getRequestBody(wrappedRequest),
                responseBody = getResponseBody(wrappedResponse)
            )
        }

        private fun getRequestBody(wrappedRequest: ContentCachingRequestWrapper): String {
            val content = wrappedRequest.contentAsByteArray
            return if (content.isNotEmpty()) String(content, StandardCharsets.UTF_8) else ""
        }

        private fun getResponseBody(wrappedResponse: ContentCachingResponseWrapper): String {
            val content = wrappedResponse.contentAsByteArray
            return if (content.isNotEmpty()) String(content, StandardCharsets.UTF_8) else ""
        }
    }
}