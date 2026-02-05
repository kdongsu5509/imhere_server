package com.kdongsu5509.imhereuserservice.application.service.user

import com.kdongsu5509.imhereuserservice.application.dto.SelfSignedJWT
import com.kdongsu5509.imhereuserservice.application.port.`in`.user.ReissueJWTUseCase
import org.springframework.stereotype.Component

@Component
class JwtReissueService(private val jwtTokenProvider: JwtTokenProvider) : ReissueJWTUseCase {
    override fun reissue(refreshToken: String): SelfSignedJWT {
        return jwtTokenProvider.reissueJwtToken(refreshToken)
    }
}