package com.kdongsu5509.user.application.port.out.term

import com.kdongsu5509.user.domain.terms.TermVersion

interface TermsVersionLoadPort {
    fun loadSpecificActiveTermVersion(termDefinitionId: Long): TermVersion
}