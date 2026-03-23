package com.kdongsu5509.user.adapter.out.persistence.terms.mapper

import com.kdongsu5509.user.adapter.out.persistence.terms.jpa.TermsDefinitionJpaEntity
import com.kdongsu5509.user.adapter.out.persistence.terms.jpa.TermsVersionJpaEntity
import com.kdongsu5509.user.domain.terms.TermVersion
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TermVersionMapper {
    fun mapToJpaEntity(
        termDefinitionJpaEntity: TermsDefinitionJpaEntity,
        version: String,
        content: String,
        effectiveDate: LocalDateTime
    ): TermsVersionJpaEntity {
        return TermsVersionJpaEntity(
            version = version,
            termVersionContent = content,
            isActive = true,
            effectiveDate = effectiveDate,
            terms = termDefinitionJpaEntity
        )
    }

    fun mapToDomainEntity(
        termsVersionJpaEntity: TermsVersionJpaEntity
    ): TermVersion {
        return TermVersion(
            termDefinitionId = termsVersionJpaEntity.terms.id!!,
            version = termsVersionJpaEntity.version,
            content = termsVersionJpaEntity.termVersionContent,
            effectiveDate = termsVersionJpaEntity.effectiveDate
        )
    }
}