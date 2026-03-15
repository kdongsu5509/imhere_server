package com.kdongsu5509.user.application.port.out.user.oauth

import com.kdongsu5509.user.adapter.out.auth.oauth.dto.OIDCPublicKey

interface PublicKeyLoadPort {
    fun loadPublicKey(kid: String): OIDCPublicKey
}