package com.kdongsu5509.user.adapter.out.auth.oauth.dto

data class OIDCPublicKeyResponse(
    val keys: List<OIDCPublicKey> = emptyList()
)