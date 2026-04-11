package com.kdongsu5509.support.logger

import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class BodyMasker(private val jsonMapper: JsonMapper) {

    companion object {
        private val SENSITIVE_PATTERN =
            Regex(""""(password|pw|confirmPassword|secret)"\s*:\s*"[^"]+"""")
    }

    fun mask(body: String): String {
        val formatted = formatJson(body)
        return maskSensitiveFields(formatted)
    }

    private fun formatJson(body: String): String {
        return try {
            val jsonNode = jsonMapper.readTree(body)
            jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode)
        } catch (_: Exception) {
            body
        }
    }

    private fun maskSensitiveFields(json: String): String =
        json.replace(SENSITIVE_PATTERN, "\"$1\": \"*****\"")
}
