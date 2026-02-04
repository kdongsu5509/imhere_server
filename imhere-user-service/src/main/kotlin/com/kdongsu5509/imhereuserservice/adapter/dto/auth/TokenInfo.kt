package com.kdongsu5509.imhereuserservice.adapter.dto.auth

import com.kdongsu5509.imhereuserservice.domain.auth.OAuth2Provider
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class TokenInfo(
    @field:NotEmpty(message = "OAuth2 제공자는 필수입니다")
    val provider: OAuth2Provider,
    @field:NotBlank(message = "idToken은 필수입니다.")
    val idToken: String,
)