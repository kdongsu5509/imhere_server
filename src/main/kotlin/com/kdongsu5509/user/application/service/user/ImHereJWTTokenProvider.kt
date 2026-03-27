package com.kdongsu5509.user.application.service.user

import com.kdongsu5509.support.exception.AuthErrorCode
import com.kdongsu5509.support.exception.BusinessException
import com.kdongsu5509.user.application.dto.ImHereJwt
import com.kdongsu5509.user.application.port.out.user.CachePort
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

@Component
class ImHereJWTTokenProvider(
    private val jwtTokenIssuer: JwtTokenIssuer,
    private val jwtTokenUtil: JwtTokenUtil,
    private val cachePort: CachePort,
) : JwtTokenProvider {
    override fun issueJwtToken(
        imHereJwtTokenElements: ImHereJwtTokenElements
    ): ImHereJwt {
        val accessToken = jwtTokenIssuer.createAccessToken(imHereJwtTokenElements)
        val refreshToken = jwtTokenIssuer.createRefreshToken(imHereJwtTokenElements)

        val expiredDateTime = jwtTokenUtil.getExpirationDateFromToken(refreshToken).atZone(ZoneId.systemDefault())
        val duration: Duration = Duration.between(Instant.now(), expiredDateTime.toInstant())

        val redisKey = getTokenRedisKeyFromImHereJwtTokenElements(imHereJwtTokenElements)
        cachePort.save(redisKey, refreshToken, duration)

        return ImHereJwt(accessToken, refreshToken)
    }

    override fun reissueJwtToken(refreshToken: String): ImHereJwt {
        val imHereJwtTokenElements = consistImHereJwtTokenElementsFromRefreshToken(refreshToken)

        // 토큰 유효성 검사 (만료 시간 포함)
        if (!jwtTokenUtil.validateToken(refreshToken)) {
            throw BusinessException(AuthErrorCode.IMHERE_INVALID_TOKEN)
        }

        val redisKey = getTokenRedisKeyFromImHereJwtTokenElements(imHereJwtTokenElements)
        val refreshTokenFromRedis = cachePort.find(redisKey, String::class.java)

        if (refreshTokenFromRedis != null && refreshTokenFromRedis == refreshToken) {
            return issueJwtToken(imHereJwtTokenElements)
        }

        throw BusinessException(AuthErrorCode.IMHERE_INVALID_TOKEN)
    }

    private fun getTokenRedisKeyFromImHereJwtTokenElements(imHereJwtTokenElements: ImHereJwtTokenElements): String {
        return "refresh:${imHereJwtTokenElements.userEmail}"
    }

    private fun consistImHereJwtTokenElementsFromRefreshToken(refreshToken: String): ImHereJwtTokenElements {
        return ImHereJwtTokenElements(
            uid = jwtTokenUtil.getUIDFromToken(refreshToken),
            userEmail = jwtTokenUtil.getUserEmailFromToken(refreshToken),
            userNickname = jwtTokenUtil.getUserNicknameFromToken(refreshToken),
            role = jwtTokenUtil.getRoleFromToken(refreshToken),
            status = jwtTokenUtil.getStatusFromToken(refreshToken)
        )
    }
}