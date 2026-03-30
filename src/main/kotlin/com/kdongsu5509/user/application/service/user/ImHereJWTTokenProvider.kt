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

        val redisKey = getTokenRedisKey(imHereJwtTokenElements.userEmail)
        cachePort.save(redisKey, refreshToken, duration)

        return ImHereJwt(accessToken, refreshToken)
    }

    override fun reissueJwtTokenByRefreshToken(refreshToken: String): ImHereJwt {
        if (!jwtTokenUtil.validateToken(refreshToken)) {
            throw BusinessException(AuthErrorCode.IMHERE_INVALID_TOKEN)
        }

        val email = jwtTokenUtil.getUserEmailFromToken(refreshToken)
        val refreshTokenSavedAtRedis = findTokenFromRedisWithUserEmail(email)

        if (refreshTokenSavedAtRedis != refreshToken) throw BusinessException(AuthErrorCode.IMHERE_INVALID_TOKEN)

        return commonReissueLogic(email)
    }

    override fun reissueJwtTokenByUserEmail(email: String): ImHereJwt {
        return commonReissueLogic(email)
    }

    private fun commonReissueLogic(email: String): ImHereJwt {
        val refreshTokenFromRedis = findTokenFromRedisWithUserEmail(email)

        val elements = consistImHereJwtTokenElementsFromRefreshToken(refreshTokenFromRedis)
        return issueJwtToken(elements)
    }

    private fun findTokenFromRedisWithUserEmail(email: String): String {
        val redisKey = getTokenRedisKey(email)

        val refreshTokenFromRedis = cachePort.find(redisKey, String::class.java)
            ?: throw BusinessException(AuthErrorCode.IMHERE_KEY_NOT_FOUND_IN_REDIS)

        return refreshTokenFromRedis
    }

    private fun getTokenRedisKey(userEmail: String): String = "refresh:$userEmail"

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
