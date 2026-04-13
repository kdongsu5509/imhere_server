package com.kdongsu5509.notifications.adapter.`in`.web

import com.kdongsu5509.notifications.adapter.`in`.web.dto.FcmTokenInfo
import com.kdongsu5509.notifications.application.port.`in`.ManageFcmTokenUseCasePort
import com.kdongsu5509.notifications.domain.DeviceType
import com.kdongsu5509.support.config.SecurityConstants
import com.kdongsu5509.support.external.DiscordUserErrorNotifier
import com.kdongsu5509.support.logger.AccessLogPrinter
import com.kdongsu5509.user.application.service.user.JwtTokenUtil
import com.kdongsu5509.user.application.service.user.SimpleTokenUserDetails
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.json.JsonMapper

@WebMvcTest(FcmTokenEnrollController::class)
class FcmTokenEnrollControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var enrollFcmTokenUserCasePort: ManageFcmTokenUseCasePort

    @MockitoBean
    private lateinit var accessLogPrinter: AccessLogPrinter

    @MockitoBean
    private lateinit var discordUserErrorNotifier: DiscordUserErrorNotifier

    @MockitoBean
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @MockitoBean
    private lateinit var securityConstants: SecurityConstants

    @Autowired
    private lateinit var objectMapper: JsonMapper

    @Test
    @DisplayName("FCM 토큰 등록 성공 - AOS")
    fun enroll_fcm_token_aos_success() {
        val request = FcmTokenInfo(
            fcmToken = "valid-fcm-token",
            deviceType = DeviceType.AOS
        )
        val userDetails = SimpleTokenUserDetails("user@example.com", "user-nick", "ROLE_USER", "ACTIVE")

        mockMvc.perform(
            post("/api/notification/fcmToken")
                .with(csrf())
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)

        verify(enrollFcmTokenUserCasePort).save(
            eq("valid-fcm-token"),
            eq("user@example.com"),
            eq(DeviceType.AOS)
        )
    }

    @Test
    @DisplayName("FCM 토큰 등록 성공 - IOS")
    fun enroll_fcm_token_ios_success() {
        val request = FcmTokenInfo(
            fcmToken = "valid-ios-fcm-token",
            deviceType = DeviceType.IOS
        )
        val userDetails = SimpleTokenUserDetails("ios-user@example.com", "ios-nick", "ROLE_USER", "ACTIVE")

        mockMvc.perform(
            post("/api/notification/fcmToken")
                .with(csrf())
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)

        verify(enrollFcmTokenUserCasePort).save(
            eq("valid-ios-fcm-token"),
            eq("ios-user@example.com"),
            eq(DeviceType.IOS)
        )
    }

    @Test
    @DisplayName("인증 없이 요청 시 401")
    fun enroll_without_auth_returns_401() {
        val request = FcmTokenInfo(
            fcmToken = "valid-fcm-token",
            deviceType = DeviceType.AOS
        )

        mockMvc.perform(
            post("/api/notification/fcmToken")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isUnauthorized)
    }
}
