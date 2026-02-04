package com.kdongsu5509.imhereuserservice.domain.terms

import java.time.LocalDateTime

data class TermVersion(
    val termDefinitionId: Long,
    val version: String, // z.B : v1.0
    val content: String, //약관 내용
    val effectiveDate: LocalDateTime //법적 유효 실행일
)
