package com.kdongsu5509.user.application.port.`in`.terms

import com.kdongsu5509.user.adapter.`in`.web.terms.dto.TermDefinitionResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ReadTermsDefinitionUseCase {
    fun readAll(pageable: Pageable): Page<TermDefinitionResponse>
}