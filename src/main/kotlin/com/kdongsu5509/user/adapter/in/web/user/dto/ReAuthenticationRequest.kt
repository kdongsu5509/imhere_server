package com.kdongsu5509.user.adapter.`in`.web.user.dto

import jakarta.validation.constraints.NotBlank

data class ReAuthenticationRequest(
    @field:NotBlank(message = "refreshToken은 필수입니다")
    val refreshToken: String
)
