package com.kdongsu5509.user.application.port.`in`.terms

import com.kdongsu5509.user.adapter.`in`.web.terms.dto.NewTermDefinitionRequest

interface CreateTermsDefinitionUseCase {
    fun createNewTermsDefinition(newTermDefinitionRequest: NewTermDefinitionRequest)
}