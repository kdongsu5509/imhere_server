package com.kdongsu5509.user.application.port.out.term

import com.kdongsu5509.user.domain.terms.TermsTypes

interface TermsDefinitionSavePort {
    fun saveTermDefinition(termsName: String, termType: TermsTypes, isRequired: Boolean)
}