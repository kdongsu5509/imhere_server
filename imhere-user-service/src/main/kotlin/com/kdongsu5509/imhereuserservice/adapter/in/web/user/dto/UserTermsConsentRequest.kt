package com.kdongsu5509.imhereuserservice.adapter.`in`.web.user.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class UserTermsConsentRequest(
    @field:NotEmpty
    val consents: List<ConsentDetail>
) {
    data class ConsentDetail(
        @field:NotNull
        val termDefinitionId: Long,
        @field:NotNull
        val isAgreed: Boolean
    )
}