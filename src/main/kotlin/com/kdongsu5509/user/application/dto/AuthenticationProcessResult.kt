package com.kdongsu5509.user.application.dto

data class AuthenticationProcessResult(
    val statusCode: Int,
    val accessToken: String,
    val refreshToken: String
)