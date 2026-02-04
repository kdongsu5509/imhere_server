package com.kdongsu5509.imhereuserservice.adapter.dto.auth

import jakarta.validation.constraints.NotBlank

data class JwtRefreshToken(
    @field:NotBlank(message = "refreshToken은 필수입니다")
    val refreshToken: String
)