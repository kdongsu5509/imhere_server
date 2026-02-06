package com.kdongsu5509.imhereuserservice.application.service.term

import com.kdongsu5509.imhereuserservice.adapter.`in`.web.terms.dto.TermDefinitionResponse
import com.kdongsu5509.imhereuserservice.application.port.out.term.TermsDefinitionLoadPort
import com.kdongsu5509.imhereuserservice.domain.terms.TermDefinition
import com.kdongsu5509.imhereuserservice.domain.terms.TermsTypes
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

@ExtendWith(MockitoExtension::class)
class TermsDefinitionReadServiceTest {

    @Mock
    lateinit var termsDefinitionLoadPort: TermsDefinitionLoadPort

    @InjectMocks
    lateinit var termsDefinitionReadService: TermsDefinitionReadService

    @Test
    @DisplayName("약관 정의를 잘 찾아와서 Page 객체로 감싸서 반환한다")
    fun readAll() {
        // given
        val pageable = PageRequest.of(0, 10)
        val termDefinitions = listOf(
            TermDefinition(1L, "서비스 이용약관", TermsTypes.SERVICE, true),
            TermDefinition(2L, "개인정보 처리방침", TermsTypes.PRIVACY, true)
        )
        val page = PageImpl(termDefinitions, pageable, termDefinitions.size.toLong())

        given(termsDefinitionLoadPort.loadAllTermsDefinitions(pageable)).willReturn(page)

        // when
        val result = termsDefinitionReadService.readAll(pageable)

        // then
        assertThat(result.content).hasSize(2)
        assertThat(result.content[0]).isInstanceOf(TermDefinitionResponse::class.java)
        assertThat(result.content[0].title).isEqualTo("서비스 이용약관")

        verify(termsDefinitionLoadPort).loadAllTermsDefinitions(pageable)
    }

    @Test
    @DisplayName("약관 정의가 없어도 Page 객체로 감싸서 반환한다")
    fun readAll_if_not_exist() {
        // given
        val pageable = PageRequest.of(0, 10)
        given(termsDefinitionLoadPort.loadAllTermsDefinitions(pageable))
            .willReturn(Page.empty(pageable))

        // when
        val result = termsDefinitionReadService.readAll(pageable)

        // then
        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isEqualTo(0L)
    }
}