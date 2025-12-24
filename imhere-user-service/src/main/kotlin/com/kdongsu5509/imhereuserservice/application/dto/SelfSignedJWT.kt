package com.kdongsu5509.imhereuserservice.application.dto

data class SelfSignedJWT(
    val accessToken: String,
    val refreshToken: String
)