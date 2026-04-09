package com.kdongsu5509.support.config

import com.common.testUtil.ControllerTestSupport
import com.kdongsu5509.support.external.DiscordMessageSender
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class AdminAuthIntegrationTest : ControllerTestSupport() {

    companion object {
        private const val OTT_GENERATE_URL = "/api/admin/auth/ott"
        private const val OTT_LOGIN_URL = "/api/admin/auth"
        private const val ADMIN_SECRET_HEADER = "X-ADMIN-SECRET"
    }

    @MockitoBean
    lateinit var discordMessageSender: DiscordMessageSender

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Value("\${admin.secret}")
    lateinit var adminSecret: String

    @Value("\${admin.id}")
    lateinit var adminId: String

    @Test
    @DisplayName("올바른 관리자 시크릿으로 OTT 발급 시 200과 안내 메시지를 반환한다")
    fun `OTT 발급 성공`() {
        mockMvc.perform(
            post(OTT_GENERATE_URL)
                .header(ADMIN_SECRET_HEADER, adminSecret)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", adminId)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Token has been sent to Discord. Please check your channel."))
    }

    @Test
    @DisplayName("관리자 시크릿 헤더 없이 OTT 발급 요청 시 403을 반환한다")
    fun `시크릿 헤더 없는 OTT 발급 요청은 403`() {
        mockMvc.perform(
            post(OTT_GENERATE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", adminId)
        )
            .andExpect(status().isForbidden)
    }

    @Test
    @DisplayName("잘못된 관리자 시크릿으로 OTT 발급 요청 시 403을 반환한다")
    fun `잘못된 시크릿으로 OTT 발급 요청은 403`() {
        mockMvc.perform(
            post(OTT_GENERATE_URL)
                .header(ADMIN_SECRET_HEADER, "wrong-secret")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", adminId)
        )
            .andExpect(status().isForbidden)
    }

    @Test
    @DisplayName("유효한 OTT로 로그인하면 200과 accessToken을 반환한다")
    fun `유효한 OTT 로그인 성공 시 accessToken 발급`() {
        // given: OTT 발급
        mockMvc.perform(
            post(OTT_GENERATE_URL)
                .header(ADMIN_SECRET_HEADER, adminSecret)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", adminId)
        ).andExpect(status().isOk)

        val token = jdbcTemplate.queryForObject(
            "SELECT token_value FROM one_time_tokens WHERE username = ?",
            String::class.java,
            adminId
        )

        // when & then: OTT 소모 → JWT 발급
        mockMvc.perform(
            post(OTT_LOGIN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("token", token!!)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").exists())
    }

    @Test
    @DisplayName("유효하지 않은 OTT로 로그인 시 401을 반환한다")
    fun `유효하지 않은 OTT 로그인은 401`() {
        mockMvc.perform(
            post(OTT_LOGIN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("token", "invalid-token-value")
        )
            .andExpect(status().isUnauthorized)
    }
}
