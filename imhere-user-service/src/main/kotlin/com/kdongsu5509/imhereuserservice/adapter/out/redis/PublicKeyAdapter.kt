package com.kdongsu5509.imhereuserservice.adapter.out.redis

import com.kdongsu5509.imhereuserservice.adapter.out.dto.OIDCPublicKey
import com.kdongsu5509.imhereuserservice.adapter.out.dto.OIDCPublicKeyResponse
import com.kdongsu5509.imhereuserservice.adapter.out.jjwt.KakaoOIDCProperties
import com.kdongsu5509.imhereuserservice.application.port.out.CachePort
import com.kdongsu5509.imhereuserservice.application.port.out.LoadPublicKeyPort
import com.kdongsu5509.imhereuserservice.support.exception.domain.auth.KakaoOIDCKeyFetchFailFromRedisException
import com.kdongsu5509.imhereuserservice.support.exception.domain.auth.KakaoOIDCPublicKeyNotFoundException
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
                ?: throw KakaoOIDCPublicKeyNotFoundException()

        return oidcPublicKey
    }

    private fun getCachedPublicKeys(): OIDCPublicKeyResponse {
        val cachedKeySet = cachePort.find(kakaoOIDCProperties.cacheKey) as? OIDCPublicKeyResponse
            ?: throw KakaoOIDCKeyFetchFailFromRedisException()
        return cachedKeySet
    }
}