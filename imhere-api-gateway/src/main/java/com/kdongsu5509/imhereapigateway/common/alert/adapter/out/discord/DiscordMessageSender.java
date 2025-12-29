package com.kdongsu5509.imhereapigateway.common.alert.adapter.out.discord;

import com.kdongsu5509.imhereapigateway.common.alert.dto.ErrorAlertMessage;
import com.kdongsu5509.imhereapigateway.common.alert.port.out.MessageSendPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class DiscordMessageSender implements MessageSendPort {

    private static final Logger log = LoggerFactory.getLogger(DiscordMessageSender.class);
    private static final String DISCORD_BASE_URL = "https://discord.com";

    private final WebClient webClient;

    @Value("${discord.url:}")
    private String specificWebhookUrl;

    public DiscordMessageSender(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(DISCORD_BASE_URL).build();
    }

    @Override
    public void sendMessage(String content) {
        if (specificWebhookUrl == null || specificWebhookUrl.isEmpty()) {
            log.warn("Discord webhook URL not configured. Skipping message send.");
            return;
        }
        
        try {
            ErrorAlertMessage message = new ErrorAlertMessage(content);
            sendToDiscord(message);
            log.info("디스코드 알림 전송 성공: {}", content);
        } catch (Exception e) {
            log.error("디스코드 알림 전송 실패", e);
        }
    }

    private void sendToDiscord(ErrorAlertMessage message) {
        webClient.post()
                .uri(specificWebhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(message)
                .retrieve()
                .toBodilessEntity()
                .block();  // Reactive 환경에서 동기 호출
    }
}
