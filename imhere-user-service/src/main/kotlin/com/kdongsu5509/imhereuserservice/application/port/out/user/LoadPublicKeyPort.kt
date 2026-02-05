package com.kdongsu5509.imhereuserservice.application.port.out.user

import com.kdongsu5509.imhereuserservice.adapter.out.dto.OIDCPublicKey

interface LoadPublicKeyPort {
    fun loadPublicKey(kid: String): OIDCPublicKey
}