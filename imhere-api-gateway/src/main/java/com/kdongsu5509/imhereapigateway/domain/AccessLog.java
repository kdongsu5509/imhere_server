package com.kdongsu5509.imhereapigateway.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AccessLog {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            "authorization", "cookie", "set-cookie", "x-auth-token", "proxy-authorization"
    );

    private final String traceId;
    private final String method;
    private final String uri;
    private final String queryString;
    private final String requestBody;
    private final String responseBody;
    private final Map<String, String> headers;
    private final String userAgent;
    private final String remoteIp;
    private final int status;
    private final String threadName;
    private final LocalDateTime requestAt;
    private final LocalDateTime responseAt;
    private final long durationMs;

    private AccessLog(Builder builder) {
        this.traceId = builder.traceId;
        this.method = builder.method;
        this.uri = builder.uri;
        this.queryString = builder.queryString;
        this.requestBody = builder.requestBody;
        this.responseBody = builder.responseBody;
        this.headers = builder.headers;
        this.userAgent = builder.userAgent;
        this.remoteIp = builder.remoteIp;
        this.status = builder.status;
        this.threadName = builder.threadName;
        this.requestAt = builder.requestAt;
        this.responseAt = builder.responseAt;
        this.durationMs = builder.durationMs;
    }

    // Getters
    public String getTraceId() { return traceId; }
    public String getMethod() { return method; }
    public String getUri() { return uri; }
    public String getQueryString() { return queryString; }
    public String getRequestBody() { return requestBody; }
    public String getResponseBody() { return responseBody; }
    public Map<String, String> getHeaders() { return headers; }
    public String getUserAgent() { return userAgent; }
    public String getRemoteIp() { return remoteIp; }
    public int getStatus() { return status; }
    public String getThreadName() { return threadName; }
    public LocalDateTime getRequestAt() { return requestAt; }
    public LocalDateTime getResponseAt() { return responseAt; }
    public long getDurationMs() { return durationMs; }

    @Override
    public String toString() {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "Log JSON Parsing Error";
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String traceId;
        private String method;
        private String uri;
        private String queryString;
        private String requestBody = "";
        private String responseBody = "";
        private Map<String, String> headers = new HashMap<>();
        private String userAgent;
        private String remoteIp;
        private int status;
        private String threadName;
        private LocalDateTime requestAt;
        private LocalDateTime responseAt;
        private long durationMs;

        public Builder traceId(String traceId) { this.traceId = traceId; return this; }
        public Builder method(String method) { this.method = method; return this; }
        public Builder uri(String uri) { this.uri = uri; return this; }
        public Builder queryString(String queryString) { this.queryString = queryString; return this; }
        public Builder requestBody(String requestBody) { this.requestBody = requestBody != null ? requestBody : ""; return this; }
        public Builder responseBody(String responseBody) { this.responseBody = responseBody != null ? responseBody : ""; return this; }
        public Builder headers(Map<String, String> headers) { this.headers = headers; return this; }
        public Builder userAgent(String userAgent) { this.userAgent = userAgent; return this; }
        public Builder remoteIp(String remoteIp) { this.remoteIp = remoteIp; return this; }
        public Builder status(int status) { this.status = status; return this; }
        public Builder threadName(String threadName) { this.threadName = threadName; return this; }
        public Builder requestAt(LocalDateTime requestAt) { this.requestAt = requestAt; return this; }
        public Builder responseAt(LocalDateTime responseAt) { this.responseAt = responseAt; return this; }
        public Builder durationMs(long durationMs) { this.durationMs = durationMs; return this; }

        public AccessLog build() {
            return new AccessLog(this);
        }
    }

    public static AccessLog createFromRequest(
            ServerHttpRequest request,
            ServerHttpResponse response,
            String traceId,
            LocalDateTime requestAt,
            LocalDateTime responseAt,
            String requestBody,
            String responseBody
    ) {
        long durationMs = Duration.between(requestAt, responseAt).toMillis();

        return AccessLog.builder()
                .traceId(traceId)
                .requestAt(requestAt)
                .responseAt(responseAt)
                .durationMs(durationMs)
                .threadName(Thread.currentThread().getName())
                .method(request.getMethod().name())
                .uri(request.getURI().getPath())
                .queryString(request.getURI().getQuery())
                .headers(extractHeaders(request.getHeaders()))
                .remoteIp(extractClientIp(request))
                .userAgent(request.getHeaders().getFirst(HttpHeaders.USER_AGENT))
                .status(response.getStatusCode() != null ? response.getStatusCode().value() : 0)
                .requestBody(requestBody)
                .responseBody(responseBody)
                .build();
    }

    private static Map<String, String> extractHeaders(HttpHeaders headers) {
        return headers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            if (SENSITIVE_HEADERS.contains(entry.getKey().toLowerCase())) {
                                return "true";
                            }
                            return String.join(", ", entry.getValue());
                        }
                ));
    }

    private static String extractClientIp(ServerHttpRequest request) {
        String forwarded = request.getHeaders().getFirst("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        if (request.getRemoteAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }
        return "unknown";
    }
}
