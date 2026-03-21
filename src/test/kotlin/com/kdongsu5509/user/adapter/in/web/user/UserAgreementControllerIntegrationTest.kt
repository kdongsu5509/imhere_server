package com.kdongsu5509.user.adapter.`in`.web.user

import com.common.testUtil.ControllerTestSupport
import com.kdongsu5509.user.adapter.`in`.web.user.dto.UserTermsConsentRequest
import com.kdongsu5509.user.adapter.out.persistence.terms.jpa.SpringDataTermsDefinitionRepository
import com.kdongsu5509.user.adapter.out.persistence.terms.jpa.SpringDataTermsVersionRepository
import com.kdongsu5509.user.adapter.out.persistence.terms.jpa.TermsDefinitionJpaEntity
import com.kdongsu5509.user.adapter.out.persistence.terms.jpa.TermsVersionJpaEntity
import com.kdongsu5509.user.adapter.out.persistence.user.jpa.SpringDataUserRepository
import com.kdongsu5509.user.adapter.out.persistence.user.jpa.UserJpaEntity
import com.kdongsu5509.user.application.port.`in`.user.AgreementTermUseCase
import com.kdongsu5509.user.domain.terms.TermsTypes
import com.kdongsu5509.user.domain.user.OAuth2Provider
import com.kdongsu5509.user.domain.user.UserRole
import com.kdongsu5509.user.domain.user.UserStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

class UserAgreementControllerIntegrationTest : ControllerTestSupport() {

    @Autowired
    lateinit var userRepository: SpringDataUserRepository

    @Autowired
    lateinit var termsDefinitionRepository: SpringDataTermsDefinitionRepository

    @Autowired
    lateinit var termsVersionRepository: SpringDataTermsVersionRepository

    @Autowired
    lateinit var agreementTermUseCase: AgreementTermUseCase

    companion object {
        const val BASE_URL = "/api/user/terms"
        const val CONSENT_ALL_URL = "/consent"
        const val CONSENT_SINGLE_URL = "/consent/{termDefinitionId}"
        const val TEST_USER = "test@kakao.com"
        var TEST_DEF_ID1 = 0L
        var TEST_DEF_ID2 = 0L
        var TEST_DEF_ID3 = 0L
    }

    @BeforeEach
    fun setUp() {
        userRepository.save(
            UserJpaEntity(
                TEST_USER, "테스터", UserRole.NORMAL, OAuth2Provider.KAKAO, UserStatus.PENDING
            )
        )

        val testTermDef1 = termsDefinitionRepository.save(
            TermsDefinitionJpaEntity(
                termsTitle = "테스트 약관1", TermsTypes.LOCATION, true
            )
        )
        val testTermDef2 = termsDefinitionRepository.save(
            TermsDefinitionJpaEntity(
                termsTitle = "테스트 약관2", TermsTypes.PRIVACY, true
            )
        )
        val testTermDef3 = termsDefinitionRepository.save(
            TermsDefinitionJpaEntity(
                termsTitle = "테스트 약관2",
                TermsTypes.MARKETING,
                false
            )
        )
        TEST_DEF_ID1 = testTermDef1.id!!
        TEST_DEF_ID2 = testTermDef2.id!!
        TEST_DEF_ID3 = testTermDef3.id!!

        createTestTermVersionEntity(testTermDef1)
        createTestTermVersionEntity(testTermDef2)
        createTestTermVersionEntity(testTermDef3)
    }

    @Test
    @WithMockUser(username = TEST_USER)
    @DisplayName("전체 약관 동의 API가 정상적으로 호출된다")
    fun consentAll_success() {
        val request = UserTermsConsentRequest(
            consents = listOf(
                UserTermsConsentRequest.ConsentDetail(TEST_DEF_ID1, true),
                UserTermsConsentRequest.ConsentDetail(TEST_DEF_ID2, true),
                UserTermsConsentRequest.ConsentDetail(TEST_DEF_ID3, true)
            )
        )

        mockMvc.perform(
            post(BASE_URL + CONSENT_ALL_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(username = TEST_USER)
    @DisplayName("단일 약관 동의 잘 처리한다")
    fun consentSingle_success() {
        mockMvc.perform(
            post("$BASE_URL$CONSENT_SINGLE_URL", TEST_DEF_ID3)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(username = TEST_USER)
    @DisplayName("단일 약관 동의 시 존재하지 않는 ID 전달하면 400 에러가 발생한다")
    fun consentSingle_fail_validation() {
        val invalidId = 999_999_999L

        mockMvc.perform(
            post("$BASE_URL$CONSENT_SINGLE_URL", invalidId)
                .header("Authorization", "Bearer access-token")
        )
            .andExpect(status().isNotFound)
    }

    private fun createTestTermVersionEntity(testTermDef: TermsDefinitionJpaEntity) {
        termsVersionRepository.save(
            TermsVersionJpaEntity(
                version = "1.0",
                content = "내용",
                isActive = true,
                effectiveDate = LocalDateTime.now().plusDays(1),
                testTermDef
            )
        )
    }
}