package com.kdongsu5509.notifications.adapter.`in`.web

import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.kdongsu5509.notifications.adapter.`in`.web.dto.FcmTokenInfo
import com.kdongsu5509.notifications.application.port.`in`.ManageFcmTokenUseCasePort
import com.kdongsu5509.notifications.domain.DeviceType
import com.kdongsu5509.support.config.SecurityConstants
import com.kdongsu5509.support.external.DiscordUserErrorNotifier
import com.kdongsu5509.support.logger.AccessLogPrinter
import com.kdongsu5509.user.application.service.user.JwtTokenUtil
import com.kdongsu5509.user.application.service.user.SimpleTokenUserDetails
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter
import tools.jackson.databind.json.JsonMapper

@WebMvcTest(FcmTokenEnrollController::class)
@ExtendWith(RestDocumentationExtension::class)
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

    @BeforeEach
    fun setUp(
        webApplicationContext: WebApplicationContext,
        restDocumentation: RestDocumentationContextProvider
    ) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
            .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
            .addFilters<DefaultMockMvcBuilder>(CharacterEncodingFilter("UTF-8", true))
            .build()
    }

    companion object {
        const val FCM_ENROLL_PATH = "/api/notification/fcmToken"
    }

    @Test
    @DisplayName("FCM 토큰 등록 성공 - AOS")
    fun enroll_fcm_token_aos_success() {
        val request = FcmTokenInfo(
            fcmToken = "valid-fcm-token",
            deviceType = DeviceType.AOS
        )
        val userDetails = SimpleTokenUserDetails("user@example.com", "user-nick", "ROLE_USER", "ACTIVE")

        mockMvc.perform(
            post(FCM_ENROLL_PATH)
                .with(csrf())
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
            .andDo(
                document(
                    "fcm-enroll/success_aos",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("알림 - FCM 등록")
                            .summary("FCM 토큰 서버 저장용 api")
                            .description("FCM토큰을 서버에서 사용할 수 있도록 저장 시 사용")
                            .build()
                    )
                )
            )

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
            post(FCM_ENROLL_PATH)
                .with(csrf())
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
            .andDo(
                document(
                    "fcm-enroll/success_ios",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("알림 - FCM 등록")
                            .summary("FCM 토큰 서버 저장용 api")
                            .description("FCM토큰을 서버에서 사용할 수 있도록 저장 시 사용")
                            .build()
                    )
                )
            )

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
            post(FCM_ENROLL_PATH)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isUnauthorized)
    }
}
