package com.kdongsu5509.imhereuserservice.adapter.out.auth.oauth

import com.kdongsu5509.imhereuserservice.adapter.out.auth.oauth.dto.OIDCPublicKeyResponse
import com.kdongsu5509.imhereuserservice.application.port.out.user.oauth.OauthClientPort
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class KakaoOauthClient(private val restClientBuilder: RestClient.Builder) : OauthClientPort {
    private val kakaoBaseUrl = "https://kauth.kakao.com"
    private val publicKeyRequestSpecificUrl: String = "/.well-known/jwks.json"

    private val log = LoggerFactory.getLogger(OauthClientPort::class.java)

    @Cacheable(value = ["kakaoOidcKeys"], cacheManager = "oidcCacheManager", key = "'kakaoPublicKeySet'")
    override fun getPublicKeyFromProvider(): OIDCPublicKeyResponse? {
        return fetchPublicKey()
    }

    @CachePut(value = ["kakaoOidcKeys"], cacheManager = "oidcCacheManager", key = "'kakaoPublicKeySet'")
    override fun refreshPublicKeyFromProvider(): OIDCPublicKeyResponse? {
        log.info("카카오 공개키 강제 갱신 수행")
        return fetchPublicKey()
    }

    private fun fetchPublicKey(): OIDCPublicKeyResponse? {
        val webClient = restClientBuilder.baseUrl(kakaoBaseUrl).build()
        return webClient.get()
            .uri(publicKeyRequestSpecificUrl)
            .retrieve()
            .body(OIDCPublicKeyResponse::class.java)
    }
}