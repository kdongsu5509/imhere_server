package com.kdongsu5509.user.adapter.out.persistence.terms.jpa

import com.kdongsu5509.user.domain.terms.TermsTypes
import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataTermsDefinitionRepository : JpaRepository<TermsDefinitionJpaEntity, Long> {
    fun existsByTermsTitleAndTermsType(
        termsTitle: String,
        termsType: TermsTypes
    ): Boolean
}