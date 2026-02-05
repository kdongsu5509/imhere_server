package com.kdongsu5509.imhereuserservice.application.service.term

import com.kdongsu5509.imhereuserservice.application.port.`in`.terms.CreateTermVersionUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.term.TermsVersionSavePort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
@Transactional
class TermsVersionCreateService(
    private val termsVersionSavePort: TermsVersionSavePort,
) : CreateTermVersionUseCase {
    override fun createNewTermVersion(
        termDefinitionId: Long,
        version: String,
        content: String,
        effectiveDate: LocalDateTime
    ) {
        termsVersionSavePort.saveTermVersion(
            termDefinitionId, version, content, effectiveDate
        )
    }
}