package com.kdongsu5509.imhereuserservice.application.port.`in`.user

import com.kdongsu5509.imhereuserservice.application.dto.SelfSignedJWT
import com.kdongsu5509.imhereuserservice.domain.user.OAuth2Provider

interface IssueJWTUseCase {
    fun issue(email: String, nickname: String, oauth2Provider: OAuth2Provider): SelfSignedJWT
}