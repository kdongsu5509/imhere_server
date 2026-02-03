package com.kdongsu5509.imhereuserservice.application.port.`in`.auth

import com.kdongsu5509.imhereuserservice.application.dto.SelfSignedJWT
import com.kdongsu5509.imhereuserservice.domain.auth.OAuth2Provider

interface IssueJWTUseCase {
    fun issue(email: String, nickname: String, oauth2Provider: OAuth2Provider): SelfSignedJWT
}