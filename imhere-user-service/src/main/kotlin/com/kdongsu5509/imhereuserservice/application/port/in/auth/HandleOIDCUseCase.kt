package com.kdongsu5509.imhereuserservice.application.port.`in`.auth

import com.kdongsu5509.imhereuserservice.application.dto.SelfSignedJWT
import com.kdongsu5509.imhereuserservice.domain.auth.OAuth2Provider

interface HandleOIDCUseCase {
    fun verifyIdTokenAndReturnJwt(idToken: String, provider: OAuth2Provider): SelfSignedJWT
}