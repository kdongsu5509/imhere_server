package com.kdongsu5509.imhereuserservice.adapter.`in`.web.user

import com.kdongsu5509.imhereuserservice.adapter.`in`.web.common.APIResponse
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.user.dto.AuthenticationRequest
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.user.dto.AuthenticationResponse
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.user.dto.ReAuthenticationResponse
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
        @Validated @RequestBody authenticationRequest: AuthenticationRequest
    ): APIResponse<AuthenticationResponse?> {
        val jwt: SelfSignedJWT = authenticateWithOidcUseCase.verifyIdTokenAndReturnJwt(
            authenticationRequest.idToken, authenticationRequest.provider
        )

        return APIResponse.success(
            AuthenticationResponse(jwt.accessToken, jwt.refreshToken)
        )
    }

    @PostMapping("/reissue")
    fun handleJwtTokenReissueRequest(
        @Validated @RequestBody reAuthenticationResponse: ReAuthenticationResponse
    ): APIResponse<AuthenticationResponse?> {
        val jwt = reissueJwtUseCase.reissue(reAuthenticationResponse.refreshToken)
        return APIResponse.Companion.success(
            AuthenticationResponse(jwt.accessToken, jwt.refreshToken)
        )
    }
}