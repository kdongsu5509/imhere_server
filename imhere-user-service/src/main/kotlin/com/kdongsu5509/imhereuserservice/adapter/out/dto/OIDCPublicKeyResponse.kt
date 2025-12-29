package com.kdongsu5509.imhereuserservice.adapter.out.dto

data class OIDCPublicKeyResponse(
    val keys: List<OIDCPublicKey> = emptyList()
)