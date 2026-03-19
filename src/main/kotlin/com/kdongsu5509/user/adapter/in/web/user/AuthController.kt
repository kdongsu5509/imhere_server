package com.kdongsu5509.user.adapter.`in`.web.user

import com.kdongsu5509.support.common.dto.APIResponse
import com.kdongsu5509.user.adapter.`in`.web.user.dto.AuthenticationRequest
import com.kdongsu5509.user.adapter.`in`.web.user.dto.AuthenticationResponse
import com.kdongsu5509.user.adapter.`in`.web.user.dto.ReAuthenticationRequest
import com.kdongsu5509.user.application.port.`in`.user.AuthenticateWithOidcUseCase
import com.kdongsu5509.user.application.port.`in`.user.ReissueJWTUseCase
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user/auth")
class AuthController(
    val authenticateWithOidcUseCase: AuthenticateWithOidcUseCase,
    val reissueJwtUseCase: ReissueJWTUseCase
) {
    @PostMapping("/login")
    fun handleIdToken(
        @Validated @RequestBody authenticationRequest: AuthenticationRequest
    ): ResponseEntity<APIResponse<AuthenticationResponse?>> {
        val authResult = authenticateWithOidcUseCase.authenticate(
            authenticationRequest.idToken, authenticationRequest.provider
        )

        return ResponseEntity.status(authResult.statusCode)
            .body(
                APIResponse.successWithHttpStatusCode(
                    authResult.statusCode,
                    AuthenticationResponse(
                        authResult.accessToken,
                        authResult.refreshToken
                    )
                )
            )
    }

    @PostMapping("/reissue")
    fun handleJwtTokenReissueRequest(
        @Validated @RequestBody reAuthenticationRequest: ReAuthenticationRequest
    ): APIResponse<AuthenticationResponse?> {
        val jwt = reissueJwtUseCase.reissue(reAuthenticationRequest.refreshToken)
        return APIResponse.Companion.success(
            AuthenticationResponse(jwt.accessToken, jwt.refreshToken)
        )
    }
}
