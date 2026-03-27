package com.kdongsu5509.user.adapter.out.persistence.terms.mapper

import com.kdongsu5509.user.adapter.out.persistence.terms.jpa.TermsDefinitionJpaEntity
import com.kdongsu5509.user.domain.terms.TermDefinition
import com.kdongsu5509.user.domain.terms.TermsTypes
import org.springframework.stereotype.Component

@Component
class TermDefinitionMapper {
    fun mapToJpaEntity(
        termsTitle: String,
        termType: TermsTypes,
        isRequired: Boolean
    ): TermsDefinitionJpaEntity {
        return TermsDefinitionJpaEntity(
            termsTitle = termsTitle,
            isRequired = isRequired,
            termsType = termType
        )
    }

    fun mapToDomainEntity(
        jpaEntity: TermsDefinitionJpaEntity
    ): TermDefinition {
        return TermDefinition(
            id = jpaEntity.id!!,
            title = jpaEntity.termsTitle,
            termsTypes = jpaEntity.termsType,
            isRequired = jpaEntity.isRequired
        )
    }
}