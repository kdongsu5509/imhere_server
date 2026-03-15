package com.kdongsu5509.user.application.port.out.user.oauth

import com.kdongsu5509.user.application.dto.OIDCUserInformation

interface OIDCVerificationPort {
    fun verifyAndReturnUserInformation(idToken: String): OIDCUserInformation
}