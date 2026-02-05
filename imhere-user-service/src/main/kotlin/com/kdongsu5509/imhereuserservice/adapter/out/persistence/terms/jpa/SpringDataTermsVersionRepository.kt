package com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SpringDataTermsVersionRepository : JpaRepository<TermsVersionJpaEntity, Long> {
    @Query(
        """
    SELECT v FROM TermsVersionJpaEntity v 
    WHERE v.terms.id = :termsId 
      AND v.isActive = true 
"""
    )
    fun findActiveVersion(termsId: Long): TermsVersionJpaEntity?
}