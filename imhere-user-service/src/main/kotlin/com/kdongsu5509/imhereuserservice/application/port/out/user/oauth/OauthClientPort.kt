package com.kdongsu5509.imhereuserservice.application.port.out.user.oauth

import com.kdongsu5509.imhereuserservice.adapter.out.auth.oauth.dto.OIDCPublicKeyResponse


interface OauthClientPort {
    fun getPublicKeyFromProvider(): OIDCPublicKeyResponse?
    fun refreshPublicKeyFromProvider(): OIDCPublicKeyResponse?
}