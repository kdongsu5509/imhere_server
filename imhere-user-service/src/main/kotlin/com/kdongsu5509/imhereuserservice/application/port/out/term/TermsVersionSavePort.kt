package com.kdongsu5509.imhereuserservice.application.port.out.term

import java.time.LocalDateTime

interface TermsVersionSavePort {
    fun saveTermVersion(termDefinitionId: Long, version: String, content: String, effectiveDate: LocalDateTime)
}