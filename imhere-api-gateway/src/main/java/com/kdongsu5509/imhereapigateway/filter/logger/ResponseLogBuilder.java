package com.kdongsu5509.imhereapigateway.filter.logger;

import com.kdongsu5509.imhereapigateway.domain.AccessLog;
import org.springframework.stereotype.Component;

@Component
public class ResponseLogBuilder {
    private final BodyMasker bodyMasker;

    public ResponseLogBuilder(BodyMasker bodyMasker) {
        this.bodyMasker = bodyMasker;
    }

    public void build(AccessLog log, StringBuilder sb) {
        appendHeader(sb, log.getStatus() >= 400);
        appendStatusAndDuration(sb, log);
        appendResponseBody(sb, log);
        sb.append("--------------------------------------------------\n");
    }

    private void appendHeader(StringBuilder sb, boolean isError) {
        String header = isError ? "❌ --- [Error Response] --------------------------\n"
                                : "🚀 --- [Response] --------------------------\n";
        sb.append(header);
    }

    private void appendStatusAndDuration(StringBuilder sb, AccessLog log) {
        sb.append("Status:   ").append(log.getStatus()).append("\n");
        sb.append("Duration: ").append(log.getDurationMs()).append("ms\n");
    }

    private void appendResponseBody(StringBuilder sb, AccessLog log) {
        if (!log.getResponseBody().isEmpty()) {
            sb.append("Response: \n").append(bodyMasker.mask(log.getResponseBody())).append("\n");
        }
    }
}
