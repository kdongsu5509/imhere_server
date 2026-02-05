package com.kdongsu5509.imhereuserservice.adapter.`in`.web.terms.dto

import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

data class NewTermVersionRequest(
    @field:NotNull(message = "약관 정의 ID는 필수입니다.")
    @field:Positive
    var termDefinitionId: Long,

    @field:NotBlank(message = "버전 정보는 필수입니다.")
    val version: String,

    @field:NotBlank(message = "약관 본문 내용은 필수입니다.")
    val content: String,

    @field:NotNull(message = "시행일 설정은 필수입니다.")
    @field:FutureOrPresent(message = "시행일은 현재 또는 미래 날짜여야 합니다.")
    var effectiveDate: LocalDateTime
)