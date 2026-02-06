package com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.mapper

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa.TermsDefinitionJpaEntity
import com.kdongsu5509.imhereuserservice.domain.terms.TermDefinition
import com.kdongsu5509.imhereuserservice.domain.terms.TermsTypes
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class TermDefinitionMapperTest {

    companion object {
        const val TERM_TITLE = "테스트용 약관"
        val testTermsType = TermsTypes.LOCATION
    }

    val termDefinitionMapper: TermDefinitionMapper = TermDefinitionMapper()

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    @DisplayName("Jpa 엔티티를 잘 만든다.")
    fun mapToJpaEntity_success(testIsRequired: Boolean) {
        //when
        val result = termDefinitionMapper.mapToJpaEntity(TERM_TITLE, testTermsType, testIsRequired)

        //then
        Assertions.assertThat(result::class.java).isEqualTo(TermsDefinitionJpaEntity::class.java)
        Assertions.assertThat(result.termsTitle).isEqualTo(TERM_TITLE)
        Assertions.assertThat(result.termsType).isEqualTo(testTermsType)
        Assertions.assertThat(result.isRequired).isEqualTo(testIsRequired)
    }

    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    @DisplayName("Jpa 엔티티를 도메인 객체로 잘 만든다.")
    fun mapToDomainEntity_success(testIsRequired: Boolean) {
        //given
        val testJpaEntity = TermsDefinitionJpaEntity(
            TERM_TITLE, testTermsType, testIsRequired
        ).apply {
            id = 999L
        }

        //when
        val result = termDefinitionMapper.mapToDomainEntity(testJpaEntity)

        //then
        Assertions.assertThat(result::class.java).isEqualTo(TermDefinition::class.java)
        Assertions.assertThat(result.title).isEqualTo(TERM_TITLE)
        Assertions.assertThat(result.termsTypes).isEqualTo(testTermsType)
        Assertions.assertThat(result.isRequired).isEqualTo(testIsRequired)
    }

}