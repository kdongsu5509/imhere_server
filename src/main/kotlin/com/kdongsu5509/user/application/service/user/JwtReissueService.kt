package com.kdongsu5509.user.application.service.user

import com.kdongsu5509.user.application.dto.ImHereJwt
import com.kdongsu5509.user.application.port.`in`.user.ReissueJWTUseCase
import org.springframework.stereotype.Component

@Component
class JwtReissueService(private val jwtTokenProvider: JwtTokenProvider) : ReissueJWTUseCase {
    override fun reissueByRefreshToken(refreshToken: String): ImHereJwt {
        return jwtTokenProvider.reissueJwtTokenByRefreshToken(refreshToken)
    }
}