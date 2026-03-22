package com.kdongsu5509.user.application.service.user

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenIssuer(private val jwtProperties: JwtProperties) {

    private val zoneID: ZoneId = ZoneId.systemDefault()
    private val secretKey: SecretKey by lazy {
        val keyBytes = jwtProperties.secret.toByteArray(StandardCharsets.UTF_8)
        Keys.hmacShaKeyFor(keyBytes)
    }

    fun createAccessToken(imHereJwtTokenElements: ImHereJwtTokenElements): String {
        val expiredTime = LocalDateTime.now().plusMinutes(jwtProperties.accessExpirationMinutes)
        return setDetailInformationIntoToken(JwtClaimKeys.ACCESS_TOKEN, imHereJwtTokenElements, expiredTime)

    }

    fun createRefreshToken(imHereJwtTokenElements: ImHereJwtTokenElements): String {
        val expiredTime = LocalDateTime.now().plusDays(jwtProperties.refreshExpirationDays);
        return setDetailInformationIntoToken(JwtClaimKeys.REFRESH_TOKEN, imHereJwtTokenElements, expiredTime)
    }

    private fun setDetailInformationIntoToken(
        category: String, imHereJwtTokenElements: ImHereJwtTokenElements, expiredTime: LocalDateTime,
    ): String {
        return Jwts.builder()
            .setId(UUID.randomUUID().toString())
            .claim(JwtClaimKeys.CLAIM_CATEGORY, category)
            .claim(JwtClaimKeys.CLAIM_USER_ID, imHereJwtTokenElements.uid)
            .claim(JwtClaimKeys.CLAIM_EMAIL, imHereJwtTokenElements.userEmail)
            .claim(JwtClaimKeys.CLAIM_NICKNAME, imHereJwtTokenElements.userNickname)
            .claim(JwtClaimKeys.CLAIM_ROLE, "ROLE_${imHereJwtTokenElements.role}")
            .claim(JwtClaimKeys.CLAIM_STATUS, imHereJwtTokenElements.status)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(expiredTime.atZone(zoneID).toInstant()))
            .signWith(secretKey)
            .compact()
    }
}