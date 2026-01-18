package com.kdongsu5509.imhereuserservice.application.port.`in`.auth

import com.kdongsu5509.imhereuserservice.application.dto.UserInformation
import com.kdongsu5509.imhereuserservice.domain.OAuth2Provider

interface VerifyOIDCUseCase {
    fun verify(oidc: String, oAuth2Provider: OAuth2Provider): UserInformation
}