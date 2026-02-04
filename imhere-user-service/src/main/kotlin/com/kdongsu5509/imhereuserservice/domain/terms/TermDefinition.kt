package com.kdongsu5509.imhereuserservice.domain.terms

data class TermDefinition(
    val title: String, // 약관 이름
    val termsTypes: TermsTypes, // 약관 종류
    val isRequired: Boolean, // 필수 여부
)
