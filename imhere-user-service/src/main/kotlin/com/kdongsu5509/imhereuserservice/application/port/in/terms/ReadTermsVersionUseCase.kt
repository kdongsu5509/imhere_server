package com.kdongsu5509.imhereuserservice.application.port.`in`.terms

import com.kdongsu5509.imhereuserservice.adapter.`in`.web.terms.dto.TermVersionResponse

interface ReadTermsVersionUseCase {
    fun read(termDefinitionId: Long): TermVersionResponse
}