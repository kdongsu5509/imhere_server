package com.kdongsu5509.support.logger

import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class BodyMasker(private val jsonMapper: JsonMapper) {

    fun mask(body: String): String {
        val formatted = formatJson(body)
        return maskSensitiveFields(formatted)
    }

    private fun formatJson(body: String): String {
        return try {
            val jsonNode = jsonMapper.readTree(body)
            jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode)
        } catch (e: Exception) {
            body
        }
    }

    private fun maskSensitiveFields(json: String): String {
        return json.replace(
            Regex("\"(password|pw|confirmPassword|secret)\"\\s*:\\s*\"[^\"]+\""),
            "\"$1\": \"*****\""
        )
    }
}
