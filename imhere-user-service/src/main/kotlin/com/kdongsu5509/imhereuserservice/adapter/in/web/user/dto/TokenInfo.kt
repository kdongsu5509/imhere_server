package com.kdongsu5509.imhereuserservice.adapter.`in`.web.user.dto

import com.kdongsu5509.imhereuserservice.domain.user.OAuth2Provider
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class TokenInfo(
    @field:NotNull(message = "OAuth2 제공자는 필수입니다")
    var provider: OAuth2Provider,
    @field:NotBlank(message = "idToken은 필수입니다.")
    val idToken: String,
)