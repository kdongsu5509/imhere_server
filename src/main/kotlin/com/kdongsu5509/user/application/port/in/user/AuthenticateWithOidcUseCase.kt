package com.kdongsu5509.user.application.port.`in`.user

import com.kdongsu5509.user.application.dto.AuthenticationProcessResult
import com.kdongsu5509.user.domain.user.OAuth2Provider

interface AuthenticateWithOidcUseCase {
    fun authenticate(idToken: String, provider: OAuth2Provider): AuthenticationProcessResult
}