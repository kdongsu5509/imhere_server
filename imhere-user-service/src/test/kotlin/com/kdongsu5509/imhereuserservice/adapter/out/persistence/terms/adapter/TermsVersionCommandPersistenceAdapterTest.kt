package com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.adapter

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa.SpringDataTermsDefinitionRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa.SpringDataTermsVersionRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.mapper.TermVersionMapper
import com.kdongsu5509.imhereuserservice.support.exception.BusinessException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class TermsVersionCommandPersistenceAdapterFailureTest {

    @Mock
    lateinit var termVersionMapper: TermVersionMapper

    @Mock
    lateinit var springDataTermsDefinitionRepository: SpringDataTermsDefinitionRepository

    @Mock
    lateinit var springDataTermsVersionRepository: SpringDataTermsVersionRepository

    private lateinit var adapter: TermsVersionCommandPersistenceAdapter

    @BeforeEach
    fun setUp() {
        adapter = TermsVersionCommandPersistenceAdapter(
            termVersionMapper,
            springDataTermsDefinitionRepository,
            springDataTermsVersionRepository
        )
    }

    @Test
    @DisplayName("존재하지 않는 약관 ID로 저장 요청 시 TERM_DEFINITION_NOT_FOUND 예외가 발생해야 한다")
    fun saveTermVersion_fail_definitionNotFound() {
        // given
        val invalidId = 999L
        val version = "v1.0"
        val content = "내용"
        val effectiveDate = LocalDateTime.now()

        given(springDataTermsDefinitionRepository.findById(invalidId))
            .willReturn(Optional.empty())

        // when & then
        assertThatThrownBy {
            adapter.saveTermVersion(invalidId, version, content, effectiveDate)
        }
            .isInstanceOf(BusinessException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TERM_DEFINITION_NOT_FOUND)
    }
}