package com.kdongsu5509.imhereuserservice.adapter.`in`.web.user.dto

data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String
)