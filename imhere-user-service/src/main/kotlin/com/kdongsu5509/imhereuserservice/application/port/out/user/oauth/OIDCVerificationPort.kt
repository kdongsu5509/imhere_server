package com.kdongsu5509.imhereuserservice.application.port.out.user.oauth

import com.kdongsu5509.imhereuserservice.application.dto.UserInformation

interface OIDCVerificationPort {
    fun verifyAndReturnUserInformation(idToken: String): UserInformation
}