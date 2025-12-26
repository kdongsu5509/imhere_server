package com.kdongsu5509.imhereapigateway.filter.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdongsu5509.imhereapigateway.domain.AccessLog;
import org.springframework.stereotype.Component;

@Component
public class RequestLogBuilder {
    private final BodyMasker bodyMasker;

    public RequestLogBuilder(BodyMasker bodyMasker) {
        this.bodyMasker = bodyMasker;
    }

    public void build(AccessLog log, StringBuilder sb) {
        appendHeader(sb, log.getStatus() >= 400);
        appendBasicInfo(sb, log);
        appendRequestBody(sb, log);
    }

    private void appendHeader(StringBuilder sb, boolean isError) {
        String header = isError ? "🚨 --- [Error Request] --------------------------\n" 
                                : "✈️ --- [Request] ---------------------------\n";
        sb.append(header);
    }

    private void appendBasicInfo(StringBuilder sb, AccessLog log) {
        sb.append("ID:       ").append(log.getTraceId()).append("\n");
        sb.append("Method:   ").append(log.getMethod()).append("\n");
        appendUri(sb, log);
        sb.append("From:     ").append(log.getRemoteIp()).append("\n");
        sb.append("User-Agent: ").append(log.getUserAgent()).append("\n");
        sb.append("Headers:  ").append(log.getHeaders()).append("\n");
    }

   private void appendUri(StringBuilder sb, AccessLog log) {
        sb.append("URI:      ").append(log.getUri());
        if (log.getQueryString() != null && !log.getQueryString().isEmpty()) {
            sb.append("?").append(log.getQueryString());
        }
        sb.append("\n");
    }

    private void appendRequestBody(StringBuilder sb, AccessLog log) {
        if (!log.getRequestBody().isEmpty()) {
            sb.append("Request:  \n").append(bodyMasker.mask(log.getRequestBody())).append("\n");
        }
    }
}
