package com.kdongsu5509.imhereuserservice.adapter.dto.req

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserSearchRequest(
    @field:Size(min = 2, message = "닉네임은 2글자 이상이어야 합니다.")
    val nickname: String?,

    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String?
)