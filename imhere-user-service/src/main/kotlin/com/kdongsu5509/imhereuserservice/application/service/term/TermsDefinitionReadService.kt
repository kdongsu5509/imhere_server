package com.kdongsu5509.imhereuserservice.application.service.term

import com.kdongsu5509.imhereuserservice.adapter.`in`.web.terms.dto.TermDefinitionResponse
import com.kdongsu5509.imhereuserservice.application.port.`in`.terms.ReadTermsDefinitionUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.term.TermsDefinitionLoadPort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class TermsDefinitionReadService(
    private val termsDefinitionLoadPort: TermsDefinitionLoadPort
) : ReadTermsDefinitionUseCase {
    override fun readAll(pageable: Pageable): Page<TermDefinitionResponse> {
        return termsDefinitionLoadPort.loadAllTermsDefinitions(pageable)
            .map { TermDefinitionResponse.from(it) }
    }
}