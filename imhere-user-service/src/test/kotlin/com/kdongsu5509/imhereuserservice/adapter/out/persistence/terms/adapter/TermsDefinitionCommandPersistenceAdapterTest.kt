package com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.adapter

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa.SpringDataTermsDefinitionRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa.TermsDefinitionJpaEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.mapper.TermDefinitionMapper
import com.kdongsu5509.imhereuserservice.domain.terms.TermsTypes
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class TermsDefinitionCommandPersistenceAdapterTest {

    @Mock
    lateinit var springDataTermsDefinitionRepository: SpringDataTermsDefinitionRepository

    private val mapper = TermDefinitionMapper()
    private lateinit var adapter: TermsDefinitionCommandPersistenceAdapter

    @BeforeEach
    fun setUp() {
        adapter = TermsDefinitionCommandPersistenceAdapter(mapper, springDataTermsDefinitionRepository)
    }

    @Test
    @DisplayName("약관 정의를 성공적으로 저장한다")
    fun save_success() {
        // given
        val termsName = "개인정보 처리방침"
        val termType = TermsTypes.PRIVACY
        val isRequired = true

        // when
        adapter.saveTermDefinition(termsName, termType, isRequired)

        // then
        verify(springDataTermsDefinitionRepository).save(any(TermsDefinitionJpaEntity::class.java))
    }
}