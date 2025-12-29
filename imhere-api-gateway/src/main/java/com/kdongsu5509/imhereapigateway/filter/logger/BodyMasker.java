package com.kdongsu5509.imhereapigateway.filter.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class BodyMasker {
    private final ObjectMapper objectMapper;

    public BodyMasker(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String mask(String body) {
        String formatted = formatJson(body);
        return maskSensitiveFields(formatted);
    }

    private String formatJson(String body) {
        try {
            var jsonNode = objectMapper.readTree(body);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (Exception e) {
            return body;
        }
    }

    private String maskSensitiveFields(String json) {
        return json.replaceAll(
                "\"(password|pw|confirmPassword|secret)\"\\s*:\\s*\"[^\"]+\"",
                "\"$1\": \"*****\""
        );
    }
}
