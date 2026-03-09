package com.kdongsu5509.imhereuserservice.application.service.user

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenUtil(private val jwtProperties: JwtProperties) {

    private val zoneID: ZoneId = ZoneId.systemDefault()
    private val secretKey: SecretKey by lazy {
        val keyBytes = jwtProperties.secret.toByteArray(StandardCharsets.UTF_8)
        Keys.hmacShaKeyFor(keyBytes)
    }

    fun validateToken(token: String): Boolean {
        try {
            parseClaims(token)
            return true
        } catch (e: SecurityException) {
            print("잘못된 JWT 서명입니다: ${e.message}")
        } catch (e: MalformedJwtException) {
            print("잘못된 JWT 서명입니다: ${e.message}")
        } catch (e: ExpiredJwtException) {
            print("만료된 JWT 서명입니다: ${e.message}")
        } catch (e: UnsupportedJwtException) {
            print("지원되지 않는 JWT 서명입니다: ${e.message}")
        } catch (e: IllegalArgumentException) {
            print("JWT 토큰이 잘못되었습니다 ${e.message}")
        }
        return false
    }

    fun getJwtIdFromToken(token: String): String {
        return parseClaims(token).id
    }

    fun getUIDFromToken(token: String): UUID {
        val claims = parseClaims(token)

        // 1. "uid" 값이 아예 없는 경우에 대한 방어 로직
        val uidValue = claims["uid"]?.toString()
            ?: throw IllegalArgumentException("토큰에 UID 정보가 존재하지 않습니다.")

        // 2. 문자열을 UUID로 변환
        return try {
            UUID.fromString(uidValue)
        } catch (e: Exception) {
            throw IllegalArgumentException("유효하지 않은 UUID 형식입니다: $uidValue")
        }
    }

    fun getUsernameFromToken(token: String): String {
        return parseClaims(token)["username"] as String
    }

    fun getRoleFromToken(token: String): String {
        return parseClaims(token)["role"] as String
    }

    fun getExpirationDateFromToken(token: String): LocalDateTime {
        val expiration = parseClaims(token).expiration
        return LocalDateTime.ofInstant(expiration.toInstant(), zoneID)
    }

    private fun parseClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }
}