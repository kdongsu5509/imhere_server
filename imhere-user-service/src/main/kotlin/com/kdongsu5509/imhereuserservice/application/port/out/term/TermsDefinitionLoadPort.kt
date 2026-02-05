package com.kdongsu5509.imhereuserservice.application.port.out.term

import com.kdongsu5509.imhereuserservice.domain.terms.TermDefinition
import com.kdongsu5509.imhereuserservice.domain.terms.TermsTypes

interface TermsDefinitionLoadPort {
    fun checkExistence(
        termsTitle: String,
        termsType: TermsTypes
    ): Boolean

    fun loadTermDefinition(
        termDefinitionId: Long
    ): TermDefinition
}