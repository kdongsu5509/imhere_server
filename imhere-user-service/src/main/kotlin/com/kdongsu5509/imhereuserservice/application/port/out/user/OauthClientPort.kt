package com.kdongsu5509.imhereuserservice.application.port.out.user

import com.kdongsu5509.imhereuserservice.adapter.out.dto.OIDCPublicKeyResponse


interface OauthClientPort {
    fun getPublicKeyFromProvider(): OIDCPublicKeyResponse?
    fun refreshPublicKeyFromProvider(): OIDCPublicKeyResponse?
}