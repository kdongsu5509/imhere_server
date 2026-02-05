package com.kdongsu5509.imhereuserservice.application.port.`in`.user

import com.kdongsu5509.imhereuserservice.application.dto.SelfSignedJWT
import com.kdongsu5509.imhereuserservice.domain.user.OAuth2Provider

interface AuthenticateWithOidcUseCase {
    fun verifyIdTokenAndReturnJwt(idToken: String, provider: OAuth2Provider): SelfSignedJWT
}