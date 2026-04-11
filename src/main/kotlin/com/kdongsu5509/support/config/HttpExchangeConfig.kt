package com.kdongsu5509.support.config

import com.kdongsu5509.support.external.DiscordApiClient
import com.kdongsu5509.user.adapter.out.auth.oauth.KakaoOauthPublicKeyApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.springframework.web.service.invoker.createClient


@Configuration
class HttpExchangeConfig {

    @Bean
    fun discordApiClient(restClientBuilder: RestClient.Builder): DiscordApiClient {
        val httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
            RestClientAdapter.create(
                restClientBuilder.build()
            )
        )

        return httpServiceProxyFactory.build().createClient<DiscordApiClient>()
    }

    @Bean
    fun kakaoPublicKeyApiClient(restClientBuilder: RestClient.Builder): KakaoOauthPublicKeyApiClient {

        val restClient = restClientBuilder
            .baseUrl("https://kauth.kakao.com")
            .build()

        val adapter = RestClientAdapter.create(restClient)

        val httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(adapter)

        return httpServiceProxyFactory.build().createClient<KakaoOauthPublicKeyApiClient>()
    }
}
