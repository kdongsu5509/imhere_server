package com.kdongsu5509.imhereuserservice.adapter.dto.friends

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class FriendsRequest(
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    @field:NotBlank(message = "이메일은 필수입니다")
    val targetEmail: String
)