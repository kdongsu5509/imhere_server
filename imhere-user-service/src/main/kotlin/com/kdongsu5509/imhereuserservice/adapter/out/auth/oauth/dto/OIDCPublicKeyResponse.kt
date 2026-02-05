package com.kdongsu5509.imhereuserservice.adapter.out.auth.oauth.dto

data class OIDCPublicKeyResponse(
    val keys: List<OIDCPublicKey> = emptyList()
)