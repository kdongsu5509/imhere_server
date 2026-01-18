package com.kdongsu5509.imhereuserservice.application.service.auth.jwt

import com.kdongsu5509.imhereuserservice.application.dto.SelfSignedJWT

interface JwtTokenProvider {
    fun issueJwtAuth(email: String, role: String): SelfSignedJWT

    fun reissueJwtToken(refreshToken: String): SelfSignedJWT
}