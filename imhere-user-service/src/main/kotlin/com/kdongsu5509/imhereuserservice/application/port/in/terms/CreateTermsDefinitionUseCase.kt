package com.kdongsu5509.imhereuserservice.application.port.`in`.terms

import com.kdongsu5509.imhereuserservice.domain.terms.TermsTypes

interface CreateTermsDefinitionUseCase {
    fun createNewTermsDefinition(
        termsName: String,
        termsType: TermsTypes,
        required: Boolean,
    )
}