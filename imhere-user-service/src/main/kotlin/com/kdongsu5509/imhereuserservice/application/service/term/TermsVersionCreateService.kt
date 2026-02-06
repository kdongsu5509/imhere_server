package com.kdongsu5509.imhereuserservice.application.service.term

import com.kdongsu5509.imhereuserservice.adapter.`in`.web.terms.dto.NewTermVersionRequest
import com.kdongsu5509.imhereuserservice.application.port.`in`.terms.CreateTermVersionUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.term.TermsVersionSavePort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class TermsVersionCreateService(
    private val termsVersionSavePort: TermsVersionSavePort,
) : CreateTermVersionUseCase {
    override fun createNewTermVersion(newTermVersionRequest: NewTermVersionRequest) {
        termsVersionSavePort.saveTermVersion(
            newTermVersionRequest.termDefinitionId,
            newTermVersionRequest.version,
            newTermVersionRequest.content,
            newTermVersionRequest.effectiveDate
        )
    }
}