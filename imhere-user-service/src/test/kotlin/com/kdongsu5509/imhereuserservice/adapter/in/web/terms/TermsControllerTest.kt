package com.kdongsu5509.imhereuserservice.adapter.`in`.web.terms

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa.SpringDataTermsDefinitionRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa.SpringDataTermsVersionRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa.TermsDefinitionJpaEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.terms.jpa.TermsVersionJpaEntity
import com.kdongsu5509.imhereuserservice.domain.terms.TermsTypes
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.test.Test

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class TermsControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val springDataTermsDefinitionRepository: SpringDataTermsDefinitionRepository,
    private val springDataTermsVersionRepository: SpringDataTermsVersionRepository
) {

    @Test
    @DisplayName("약관 목록 조회 시 페이징된 결과를 반환한다")
    fun readAllTermsDefinitions_Success() {
        // given: 테스트용 데이터 2개 저장
        springDataTermsDefinitionRepository.saveAll(
            listOf(
                TermsDefinitionJpaEntity("서비스 이용약관", TermsTypes.SERVICE, true),
                TermsDefinitionJpaEntity("개인정보 처리방침", TermsTypes.PRIVACY, true)
            )
        )

        // when & then
        mockMvc.perform(
            get("/api/v1/user/terms")
                .param("page", "0")
                .param("size", "10")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content.length()").value(2))
            .andExpect(jsonPath("$.data.content[0].title").exists())
    }

    @Test
    @DisplayName("약관 버전 상세 조회 시 활성화된 버전을 반환한다")
    fun readTermsVersion_Success() {
        // given
        val definition = springDataTermsDefinitionRepository.save(
            TermsDefinitionJpaEntity("위치 정보 약관", TermsTypes.LOCATION, true)
        )
        springDataTermsVersionRepository.save(
            TermsVersionJpaEntity("v1.0", "약관 내용", true, LocalDateTime.now(), definition)
        )

        // when & then
        mockMvc.perform(
            get("/api/v1/user/terms/version/${definition.id}")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.version").value("v1.0"))
            .andExpect(jsonPath("$.data.content").value("약관 내용"))
    }

    @Test
    @DisplayName("존재하지 않는 약관 ID로 버전 조회 시 에러를 반환한다")
    fun readTermsVersion_Fail_NotFound() {
        // when & then
        mockMvc.perform(
            get("/api/v1/user/terms/version/9999")
        )
            .andExpect(status().isNotFound)
    }
}