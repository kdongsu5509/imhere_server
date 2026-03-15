package com.kdongsu5509.user.application.port.`in`.terms

import com.kdongsu5509.user.adapter.`in`.web.terms.dto.TermVersionResponse

interface ReadTermsVersionUseCase {
    fun read(termDefinitionId: Long): TermVersionResponse
}