package com.kdongsu5509.imhereuserservice.application.port.`in`

import com.kdongsu5509.imhereuserservice.application.dto.UserInformation
import com.kdongsu5509.imhereuserservice.domain.OAuth2Provider

interface VerifyOIDCPort {
    fun verify(oidc: String, oAuth2Provider: OAuth2Provider): UserInformation
}