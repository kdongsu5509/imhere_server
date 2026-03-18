package com.kdongsu5509.support.logger

import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
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
    companion object {
        val mapper: JsonMapper = JsonMapper.builder()
            .addModule(KotlinModule.Builder().build())
            .build()

        private val SENSITIVE_HEADERS = setOf(
            "authorization", "cookie", "set-cookie", "x-auth-token", "proxy-authorization"
        )

        fun extractHeaders(headerMap: Map<String, List<String>>): Map<String, String> {
            return headerMap.entries.associate { (key, value) ->
                val lowerKey = key.lowercase()
                val maskedValue = if (SENSITIVE_HEADERS.contains(lowerKey)) {
                    "*****"
                } else {
                    value.joinToString(", ")
                }
                key to maskedValue
            }
        }
    }

    override fun toString(): String {
        return try {
            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
        } catch (e: Exception) {
            "Log JSON Parsing Error"
        }
    }
}