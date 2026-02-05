package com.kdongsu5509.imhereuserservice.application.port.out.user.oauth

import com.kdongsu5509.imhereuserservice.adapter.out.auth.oauth.dto.OIDCPublicKey

interface PublicKeyLoadPort {
    fun loadPublicKey(kid: String): OIDCPublicKey
}