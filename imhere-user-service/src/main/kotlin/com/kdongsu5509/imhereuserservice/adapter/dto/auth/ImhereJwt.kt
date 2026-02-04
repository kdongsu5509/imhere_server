package com.kdongsu5509.imhereuserservice.adapter.dto.auth

data class ImhereJwt(
    val accessToken: String,
    val refreshToken: String
)