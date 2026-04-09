package com.kdongsu5509.support.external

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.http.HttpMethod
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@RestClientTest(DiscordMessageSender::class)
class DiscordMessageSenderTest @Autowired constructor(
    private val discordMessageSender: DiscordMessageSender,
    private val mockDiscordServer: MockRestServiceServer
) {

    @Test
    fun `메시지 전송 시 디스코드 웹훅 API를 성공적으로 호출해야 한다`() {
        // given
        val testMessage = "테스트용 메시지"
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
