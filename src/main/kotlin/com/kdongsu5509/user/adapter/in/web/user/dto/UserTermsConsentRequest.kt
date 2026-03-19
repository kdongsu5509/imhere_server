package com.kdongsu5509.user.adapter.`in`.web.user.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class UserTermsConsentRequest(
    @field:NotEmpty
    val consents: List<ConsentDetail>
) {
    data class ConsentDetail(
        @param:NotNull
        val termDefinitionId: Long,
        @param:NotNull
        @param:JsonProperty("agreed")
        val isAgreed: Boolean
    )
}
