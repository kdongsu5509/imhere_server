package com.kdongsu5509.imhereapigateway.filter.logger;

import com.kdongsu5509.imhereapigateway.domain.AccessLog;
import org.springframework.stereotype.Component;

@Component
public class AccessLogFormatter {
    private final RequestLogBuilder requestBuilder;
    private final ResponseLogBuilder responseBuilder;

    public AccessLogFormatter(RequestLogBuilder requestBuilder, ResponseLogBuilder responseBuilder) {
        this.requestBuilder = requestBuilder;
        this.responseBuilder = responseBuilder;
    }

    public String format(AccessLog accessLog) {
        StringBuilder sb = new StringBuilder("\n");
        requestBuilder.build(accessLog, sb);
        responseBuilder.build(accessLog, sb);
        return sb.toString();
    }
}
