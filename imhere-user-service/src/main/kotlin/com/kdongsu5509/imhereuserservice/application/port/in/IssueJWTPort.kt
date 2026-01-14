package com.kdongsu5509.imhereuserservice.application.port.`in`

import com.kdongsu5509.imhereuserservice.application.dto.SelfSignedJWT
import com.kdongsu5509.imhereuserservice.domain.OAuth2Provider

interface IssueJWTPort {
    fun issue(email: String, nickname: String, oauth2Provider: OAuth2Provider): SelfSignedJWT
}