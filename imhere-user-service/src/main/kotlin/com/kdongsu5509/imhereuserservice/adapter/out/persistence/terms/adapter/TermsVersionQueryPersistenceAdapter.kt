package com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.adapter

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa.SpringDataTermsDefinitionRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa.SpringDataTermsVersionRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.mapper.TermDefinitionMapper
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.mapper.TermVersionMapper
import com.kdongsu5509.imhereuserservice.application.port.out.term.TermsVersionLoadPort
import com.kdongsu5509.imhereuserservice.domain.terms.TermVersion
import org.springframework.stereotype.Component

@Component
class TermsVersionQueryPersistenceAdapter(
    private val termDefinitionMapper: TermDefinitionMapper,
    private val termVersionMapper: TermVersionMapper,
    private val springDataTermsDefinitionRepository: SpringDataTermsDefinitionRepository,
    private val springDataTermsVersionRepository: SpringDataTermsVersionRepository
) :
    TermsVersionLoadPort {
    override fun loadPrevTermVersion(termDefinitionId: Long): TermVersion {
        TODO("Not yet implemented")
    }

}