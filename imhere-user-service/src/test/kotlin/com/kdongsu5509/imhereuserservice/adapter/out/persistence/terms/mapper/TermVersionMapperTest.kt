package com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.mapper

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa.TermsDefinitionJpaEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa.TermsVersionJpaEntity
import com.kdongsu5509.imhereuserservice.domain.terms.TermsTypes
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TermVersionMapperTest {

    companion object {
        const val TERM_TITLE = "테스트용 약관"
        val testTermType = TermsTypes.LOCATION
        const val VERSION = "v1.0"
        const val CONTENT = "약관 내용입니다."
        val EFFECTIVE_DATE: LocalDateTime? = LocalDateTime.now()
    }

    private val termVersionMapper = TermVersionMapper()

    @Test
    @DisplayName("입력 데이터들을 바탕으로 TermsVersionJpaEntity로 올바르게 변환해야 한다")
    fun shouldMapToJpaEntity() {
        // given
        val termDefinition = TermsDefinitionJpaEntity(TERM_TITLE, testTermType, true)

        // when
        val jpaEntity = termVersionMapper.mapToJpaEntity(termDefinition, VERSION, CONTENT, EFFECTIVE_DATE!!)

        // then
        assertThat(jpaEntity.version).isEqualTo(VERSION)
        assertThat(jpaEntity.content).isEqualTo(CONTENT)
        assertThat(jpaEntity.effectiveDate).isEqualTo(EFFECTIVE_DATE)
        assertThat(jpaEntity.terms).isEqualTo(termDefinition)
        assertThat(jpaEntity.isActive).isTrue() // 매퍼에서 true로 고정 설정됨
    }

    @Test
    @DisplayName("TermsVersionJpaEntity를 Domain 객체인 TermVersion으로 올바르게 변환해야 한다")
    fun shouldMapToDomainEntity() {
        // given
        val termDefinition = TermsDefinitionJpaEntity(TERM_TITLE, testTermType, true)
        termDefinition.apply {
            id = 1L
        }
        val jpaEntity = TermsVersionJpaEntity(
            version = "v2.0",
            content = "업데이트된 내용",
            isActive = true,
            effectiveDate = LocalDateTime.of(2026, 2, 6, 0, 0),
            terms = termDefinition
        )

        // when
        val domainTermVersion = termVersionMapper.mapToDomainEntity(jpaEntity)

        // then
        assertThat(domainTermVersion.termDefinitionId).isEqualTo(jpaEntity.terms.id)
        assertThat(domainTermVersion.version).isEqualTo(jpaEntity.version)
        assertThat(domainTermVersion.content).isEqualTo(jpaEntity.content)
        assertThat(domainTermVersion.effectiveDate).isEqualTo(jpaEntity.effectiveDate)
    }
}