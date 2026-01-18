package com.kdongsu5509.imhereuserservice.application.service.auth.jwt

import com.kdongsu5509.imhereuserservice.application.dto.SelfSignedJWT
import com.kdongsu5509.imhereuserservice.application.port.`in`.auth.ReissueJWTUseCase
import org.springframework.stereotype.Component

@Component
class JwtReissueService(private val jwtTokenProvider: JwtTokenProvider) : ReissueJWTUseCase {
    override fun reissue(refreshToken: String): SelfSignedJWT {
        return jwtTokenProvider.reissueJwtToken(refreshToken)
    }
}