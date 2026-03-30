package com.kdongsu5509.user.adapter.out.auth.oauth

import com.kdongsu5509.support.exception.AuthErrorCode
import com.kdongsu5509.support.exception.BusinessException
import com.kdongsu5509.user.adapter.out.auth.oauth.dto.OIDCPublicKeyResponse
import com.kdongsu5509.user.application.port.out.user.oauth.OauthClientPort
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class KakaoOauthClient(
    restClientBuilder: RestClient.Builder
) : OauthClientPort {

    companion object {
        private val log = LoggerFactory.getLogger(KakaoOauthClient::class.java)
        const val KAKAO_BASE_URL = "https://kauth.kakao.com"
        const val KEY_REQ_URL = "/.well-known/jwks.json"
    }

    private val restClient: RestClient = restClientBuilder
        .baseUrl(KAKAO_BASE_URL)
        .build()

    @Cacheable(value = ["kakaoOidcKeys"], cacheManager = "oidcCacheManager", key = "'kakaoPublicKeySet'")
    override fun getPublicKeyFromProvider(): OIDCPublicKeyResponse? {
        return fetchPublicKey()
    }

    @CachePut(value = ["kakaoOidcKeys"], cacheManager = "oidcCacheManager", key = "'kakaoPublicKeySet'")
    override fun refreshPublicKeyFromProvider(): OIDCPublicKeyResponse? {
        log.info("카카오 공개키 강제 갱신 수행")
        return fetchPublicKey()
    }

    private fun fetchPublicKey(): OIDCPublicKeyResponse {
        val response = restClient.get()
            .uri(KEY_REQ_URL)
            .retrieve()
            .body<OIDCPublicKeyResponse>()
        return response ?: throw BusinessException(AuthErrorCode.KAKAO_OIDC_PUBLIC_KEY_FETCH_FAILED)
    }
}
