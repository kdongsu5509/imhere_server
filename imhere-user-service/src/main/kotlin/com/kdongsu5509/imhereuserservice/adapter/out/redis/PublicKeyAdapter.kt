package com.kdongsu5509.imhereuserservice.adapter.out.redis

import com.kdongsu5509.imhereuserservice.adapter.out.dto.OIDCPublicKey
import com.kdongsu5509.imhereuserservice.adapter.out.dto.OIDCPublicKeyResponse
import com.kdongsu5509.imhereuserservice.adapter.out.jjwt.KakaoOIDCProperties
import com.kdongsu5509.imhereuserservice.application.port.out.user.CachePort
import com.kdongsu5509.imhereuserservice.application.port.out.user.LoadPublicKeyPort
import com.kdongsu5509.imhereuserservice.support.exception.BusinessException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode
import org.springframework.stereotype.Component

@Component
class PublicKeyAdapter(
    private val cachePort: CachePort,
    private val kakaoOIDCProperties: KakaoOIDCProperties
) : LoadPublicKeyPort {
    override fun loadPublicKey(kid: String): OIDCPublicKey {
        val cachedPublicKeys = getCachedPublicKeys()

        val oidcPublicKey: OIDCPublicKey =
            cachedPublicKeys.keys.firstOrNull { it.kid == kid }
                ?: throw BusinessException(ErrorCode.KAKAO_OIDC_PUBLIC_KEY_NOT_FOUND)

        return oidcPublicKey
    }

    private fun getCachedPublicKeys(): OIDCPublicKeyResponse {
        val cachedKeySet = cachePort.find(kakaoOIDCProperties.cacheKey) as? OIDCPublicKeyResponse
            ?: throw BusinessException(ErrorCode.KAKAO_OIDC_PUBLIC_KEY_FETCH_FROM_REDIS_FAILED)
        return cachedKeySet
    }
}