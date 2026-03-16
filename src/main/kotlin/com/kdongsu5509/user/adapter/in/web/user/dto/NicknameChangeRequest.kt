package com.kdongsu5509.user.adapter.`in`.web.user.dto

import jakarta.validation.constraints.NotBlank

data class NicknameChangeRequest(
    @field:NotBlank(message = "?�로???�네?��? ?�수?�니??")
    val newNickname: String
)
