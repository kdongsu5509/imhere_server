package com.kdongsu5509.imhereuserservice.adapter.`in`.web.user

import com.kdongsu5509.imhereuserservice.adapter.`in`.web.common.APIResponse
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.user.dto.ImhereJwt
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.user.dto.JwtRefreshToken
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.user.dto.TokenInfo
import com.kdongsu5509.imhereuserservice.application.dto.SelfSignedJWT
import com.kdongsu5509.imhereuserservice.application.port.`in`.user.AuthenticateWithOidcUseCase
import com.kdongsu5509.imhereuserservice.application.port.`in`.user.ReissueJWTUseCase
import lombok.extern.slf4j.Slf4j
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
@RequestMapping("/api/v1/user/auth")
class AuthController(
    val authenticateWithOidcUseCase: AuthenticateWithOidcUseCase,
    val reissueJwtUseCase: ReissueJWTUseCase
) {

    @PostMapping("/login")
    fun handleIdToken(
        @Validated @RequestBody tokenInfo: TokenInfo
    ): APIResponse<ImhereJwt?> {
        val jwt: SelfSignedJWT = authenticateWithOidcUseCase.verifyIdTokenAndReturnJwt(
            tokenInfo.idToken, tokenInfo.provider
        )

        return APIResponse.success(
            ImhereJwt(jwt.accessToken, jwt.refreshToken)
        )
    }

    @PostMapping("/reissue")
    fun handleJwtTokenReissueRequest(
        @Validated @RequestBody jwtRefreshToken: JwtRefreshToken
    ): APIResponse<ImhereJwt?> {
        val jwt = reissueJwtUseCase.reissue(jwtRefreshToken.refreshToken)
        return APIResponse.Companion.success(
            ImhereJwt(jwt.accessToken, jwt.refreshToken)
        )
    }
}