package com.kdongsu5509.imhereapigateway.filter;

import com.kdongsu5509.imhereapigateway.domain.AccessLog;
import com.kdongsu5509.imhereapigateway.filter.logger.AccessLogPrinter;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {
    private final AccessLogPrinter logPrinter;

    public LoggingFilter(AccessLogPrinter logPrinter) {
        super(Config.class);
        this.logPrinter = logPrinter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (shouldIgnore(exchange.getRequest().getURI().getPath(), config)) {
                return chain.filter(exchange);
            }
            return processWithLogging(exchange, chain, config);
        };
    }

    private Mono<Void> processWithLogging(
            org.springframework.web.server.ServerWebExchange exchange,
            org.springframework.cloud.gateway.filter.GatewayFilterChain chain,
            Config config) {
        String traceId = initializeTrace();
        LocalDateTime requestAt = LocalDateTime.now();
        return captureAndLog(exchange, chain, config, traceId, requestAt);
    }

    private String initializeTrace() {
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        return traceId;
    }

    private Mono<Void> captureAndLog(
            org.springframework.web.server.ServerWebExchange exchange,
            org.springframework.cloud.gateway.filter.GatewayFilterChain chain,
            Config config, String traceId, LocalDateTime requestAt) {
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .defaultIfEmpty(exchange.getResponse().bufferFactory().wrap(new byte[0]))
                .flatMap(requestBody -> 
                    processRequest(exchange, chain, config, traceId, requestAt, requestBody));
    }

    private Mono<Void> processRequest(
            org.springframework.web.server.ServerWebExchange exchange,
            org.springframework.cloud.gateway.filter.GatewayFilterChain chain,
            Config config, String traceId, LocalDateTime requestAt, DataBuffer requestBody) {
        String requestBodyStr = extractBody(requestBody);
        ServerHttpResponseDecorator responseDecorator = 
            createResponseDecorator(exchange, config, traceId, requestAt, requestBodyStr);
        return chain.filter(exchange.mutate().response(responseDecorator).build());
    }

    private String extractBody(DataBuffer buffer) {
        String body = buffer.toString(StandardCharsets.UTF_8);
        DataBufferUtils.release(buffer);
        return body;
    }

    private ServerHttpResponseDecorator createResponseDecorator(
            org.springframework.web.server.ServerWebExchange exchange,
            Config config, String traceId, LocalDateTime requestAt, String requestBodyStr) {
        return new ServerHttpResponseDecorator(exchange.getResponse()) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                return captureResponse(body, exchange.getRequest(), getDelegate(), 
                                     config, traceId, requestAt, requestBodyStr);
            }
        };
    }

    private Mono<Void> captureResponse(Publisher<? extends DataBuffer> body,
                                       ServerHttpRequest request, ServerHttpResponse response,
                                       Config config, String traceId, LocalDateTime requestAt,
                                       String requestBodyStr) {
        return DataBufferUtils.join(Flux.from(body))
                .flatMap(responseBody -> 
                    logAndReturn(request, response, config, traceId, requestAt, 
                               requestBodyStr, responseBody));
    }

    private Mono<Void> logAndReturn(ServerHttpRequest request, ServerHttpResponse response,
                                    Config config, String traceId, LocalDateTime requestAt,
                                    String requestBodyStr, DataBuffer responseBody) {
        String responseBodyStr = extractBody(responseBody);
        AccessLog accessLog = createAccessLog(request, response, traceId, 
                                             requestAt, requestBodyStr, responseBodyStr);
        logPrinter.print(accessLog, config.isSendAlertOnError());
        MDC.clear();
        return writeResponse(response, responseBodyStr);
    }

    private AccessLog createAccessLog(ServerHttpRequest request, ServerHttpResponse response,
                                     String traceId, LocalDateTime requestAt,
                                     String requestBodyStr, String responseBodyStr) {
        return AccessLog.createFromRequest(request, response, traceId, requestAt,
                                          LocalDateTime.now(), requestBodyStr, responseBodyStr);
    }

    private Mono<Void> writeResponse(ServerHttpResponse response, String body) {
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private boolean shouldIgnore(String uri, Config config) {
        if (config.getIgnoredPatterns() != null) {
            return config.getIgnoredPatterns().stream().anyMatch(uri::startsWith);
        }
        return isDefaultIgnoredUrl(uri);
    }

    private boolean isDefaultIgnoredUrl(String uri) {
        return uri.startsWith("/actuator") || uri.startsWith("/health") 
            || uri.startsWith("/favicon.ico");
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("enabled", "sendAlertOnError");
    }

    public static class Config {
        private boolean enabled = true;
        private boolean sendAlertOnError = true;
        private List<String> ignoredPatterns;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public boolean isSendAlertOnError() { return sendAlertOnError; }
        public void setSendAlertOnError(boolean sendAlertOnError) { 
            this.sendAlertOnError = sendAlertOnError; 
        }
        public List<String> getIgnoredPatterns() { return ignoredPatterns; }
        public void setIgnoredPatterns(List<String> ignoredPatterns) { 
            this.ignoredPatterns = ignoredPatterns; 
        }
    }
}
