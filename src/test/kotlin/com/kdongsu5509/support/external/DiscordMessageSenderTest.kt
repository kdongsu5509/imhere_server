package com.kdongsu5509.support.external

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.springframework.web.service.invoker.createClient

@RestClientTest(DiscordMessageSender::class)
@Import(DiscordMessageSenderTest.Config::class)
class DiscordMessageSenderTest @Autowired constructor(
    private val discordMessageSender: DiscordMessageSender,
    private val mockDiscordServer: MockRestServiceServer
) {

    @TestConfiguration
    class Config {
        @Bean
        fun discordApiClient(restClientBuilder: RestClient.Builder): DiscordApiClient {
            return HttpServiceProxyFactory.builderFor(
                RestClientAdapter.create(restClientBuilder.build())
            ).build().createClient<DiscordApiClient>()
        }
    }

    @Test
    fun `메시지 전송 시 디스코드 웹훅 API를 성공적으로 호출해야 한다`() {
        // given
        val testMessage = DiscordMessageDto("테스트용 메시지")
        val testWebhookUrl = "https://discord.com/api/webhook/test"

        mockDiscordServer.expect(requestTo(testWebhookUrl))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess())

        // when
        discordMessageSender.sendMessage(testWebhookUrl, testMessage)

        // then
        mockDiscordServer.verify()
    }
}
