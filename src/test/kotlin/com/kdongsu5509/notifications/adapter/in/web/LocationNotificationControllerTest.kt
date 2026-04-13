package com.kdongsu5509.notifications.adapter.`in`.web

import com.kdongsu5509.notifications.adapter.`in`.web.dto.FcmNotificationRequest
import com.kdongsu5509.notifications.application.port.`in`.NotificationToUserCasePort
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

@WebMvcTest(LocationNotificationController::class)
class LocationNotificationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var notificationToUserCasePort: NotificationToUserCasePort

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
    @DisplayName("위치 알림 대상자 등록 FCM 알림 전송 (NOTI-005)")
    fun send_location_notification() {
        val request = FcmNotificationRequest(
            receiverEmail = "receiver@example.com",
            type = "ANY_TYPE",
            body = "위치 알림 대상자로 등록되었습니다."
        )
        val userDetails = SimpleTokenUserDetails("sender@example.com", "sender-nick", "ROLE_USER", "ACTIVE")

        mockMvc.perform(
            post("/api/notification/fcm/location")
                .with(csrf())
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)

        verify(notificationToUserCasePort).send(
            eq("sender-nick"),
            eq("sender@example.com"),
            eq("receiver@example.com"),
            eq("LOCATION_SHARE_RECIPIENT"),
            eq("위치 알림 대상자로 등록되었습니다.")
        )
    }

    @Test
    @DisplayName("receiverEmail 형식이 잘못되면 400 반환")
    fun send_with_invalid_receiver_email_returns_400() {
        val body = """{"receiverEmail":"not-an-email","type":"ANY_TYPE","body":"등록되었습니다."}"""
        val userDetails = SimpleTokenUserDetails("sender@example.com", "sender-nick", "ROLE_USER", "ACTIVE")

        mockMvc.perform(
            post("/api/notification/fcm/location")
                .with(csrf())
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("body가 공백이면 400 반환")
    fun send_with_blank_body_returns_400() {
        val body = """{"receiverEmail":"receiver@example.com","type":"ANY_TYPE","body":""}"""
        val userDetails = SimpleTokenUserDetails("sender@example.com", "sender-nick", "ROLE_USER", "ACTIVE")

        mockMvc.perform(
            post("/api/notification/fcm/location")
                .with(csrf())
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("인증 없이 요청 시 401")
    fun send_without_auth_returns_401() {
        val request = FcmNotificationRequest(
            receiverEmail = "receiver@example.com",
            type = "ANY_TYPE",
            body = "등록되었습니다."
        )

        mockMvc.perform(
            post("/api/notification/fcm/location")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isUnauthorized)
    }
}
