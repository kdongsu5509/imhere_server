package com.kdongsu5509.imhereuserservice.adapter.`in`.web.terms

import com.fasterxml.jackson.databind.ObjectMapper
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.terms.dto.NewTermDefinitionRequest
import com.kdongsu5509.imhereuserservice.domain.terms.TermsTypes
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class TermsAdminControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) {

    @Test
    @WithMockUser(roles = ["ADMIN"])
    @DisplayName("관리자 권한으로 새로운 약관 종류를 생성한다.")
    fun createNewTermDefinition_Success() {
        // given
        val request = NewTermDefinitionRequest("위치 정보 이용 약관", TermsTypes.LOCATION, true)

        // when & then
        mockMvc.perform(
            post("/api/v1/user/terms/definition")
                .with(csrf()) // Spring Security 사용 시 필요
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["USER"]) // 관리자가 아닌 일반 유저
    @DisplayName("관리자 권한이 없으면 약관 생성에 실패한다(403).")
    fun createNewTermDefinition_Forbidden() {
        // given
        val request = NewTermDefinitionRequest("잘못된 접근", TermsTypes.SERVICE, true)

        // when & then
        mockMvc.perform(
            post("/api/v1/user/terms/definition")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isForbidden)
    }
}