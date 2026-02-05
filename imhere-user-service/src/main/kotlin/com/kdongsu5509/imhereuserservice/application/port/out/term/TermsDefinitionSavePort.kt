package com.kdongsu5509.imhereuserservice.application.port.out.term

import com.kdongsu5509.imhereuserservice.domain.terms.TermsTypes

interface TermsDefinitionSavePort {
    fun saveTermDefinition(termsName: String, termType: TermsTypes, isRequired: Boolean)
}