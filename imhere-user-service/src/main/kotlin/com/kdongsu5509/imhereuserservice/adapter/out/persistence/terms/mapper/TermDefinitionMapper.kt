package com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.mapper

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa.TermsDefinitionJpaEntity
import com.kdongsu5509.imhereuserservice.domain.terms.TermDefinition
import com.kdongsu5509.imhereuserservice.domain.terms.TermsTypes
import org.springframework.stereotype.Component

@Component
class TermDefinitionMapper {
    fun mapToTermDefinitionJpaEntity(
        termsTitle: String,
        termType: TermsTypes,
        isRequired: Boolean
    ): TermsDefinitionJpaEntity {
        return TermsDefinitionJpaEntity(
            termsTitle, termType, isRequired
        )
    }

    fun mapToJpaEntityToDomainEntity(
        jpaEntity: TermsDefinitionJpaEntity
    ): TermDefinition {
        return TermDefinition(
            jpaEntity.termsTitle,
            jpaEntity.termsType,
            jpaEntity.isRequired
        )
    }
}