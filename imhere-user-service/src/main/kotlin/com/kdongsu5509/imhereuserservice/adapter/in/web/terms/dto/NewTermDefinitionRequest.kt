package com.kdongsu5509.imhereuserservice.adapter.`in`.web.terms.dto

import com.kdongsu5509.imhereuserservice.domain.terms.TermsTypes
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class NewTermDefinitionRequest(
    @field:NotBlank(message = "약관 제목은 필수입니다.")
    @field:Size(max = 100, message = "약관 제목은 100자 이내여야 합니다.")
    val termsName: String,

    @field:NotNull(message = "약관 종류는 필수입니다.")
    var termsType: TermsTypes,

    @field:NotNull(message = "필수 여부 설정이 누락되었습니다.")
    var isRequired: Boolean,
)