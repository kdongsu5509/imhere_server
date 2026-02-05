package com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.mapper

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa.TermsDefinitionJpaEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa.TermsVersionJpaEntity
import com.kdongsu5509.imhereuserservice.domain.terms.TermVersion
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TermVersionMapper {
    fun mapToTermVersionJpaEntity(
        termDefinitionJpaEntity: TermsDefinitionJpaEntity,
        version: String,
        content: String,
        effectiveDate: LocalDateTime
    ): TermsVersionJpaEntity {
        return TermsVersionJpaEntity(
            version,
            content,
            true,
            effectiveDate,
            termDefinitionJpaEntity
        )
    }

    fun mapToDomainEntity(
        termsVersionJpaEntity: TermsVersionJpaEntity
    ): TermVersion {
        return TermVersion(
            termsVersionJpaEntity.terms.id!!,
            termsVersionJpaEntity.version,
            termsVersionJpaEntity.content,
            termsVersionJpaEntity.effectiveDate
        )
    }
}