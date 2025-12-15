package com.kdongsu5509.imhere.common.exceptionHandler.adapter.out.discord

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.*
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient

@ExtendWith(MockitoExtension::class)
class DiscordMessageServiceTest {
    private lateinit var messageService: DiscordMessageSender
    private lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun setUp() {
        var builder = RestClient.builder()

        mockServer = MockRestServiceServer.bindTo(builder).build()

        messageService = DiscordMessageSender(builder)

        messageService.specificWebhookUrl = "/api/webhooks/test-url"
    }

    @Test
    @DisplayName("디스코드 메시지가 올바른 URL과 포맷으로 전송되어야 한다")
    fun sendMessageSuccess() {
        // Given
        val errorContent = "테스트 에러 메시지입니다."
        val expectedJson = """{"content":"$errorContent"}"""

        mockServer.expect(requestTo("https://discord.com/api/webhooks/test-url"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(expectedJson))
            .andRespond(withSuccess())

        // When
        messageService.sendMessage(errorContent)

        // Then
        mockServer.verify()
    }
}