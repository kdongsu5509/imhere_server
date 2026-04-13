package com.kdongsu5509.notifications.adapter.`in`.web

import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.kdongsu5509.notifications.application.port.`in`.MessageSendUseCasePort
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

@WebMvcTest(MessageController::class)
@ExtendWith(RestDocumentationExtension::class)
class MessageControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var messageSendUseCasePort: MessageSendUseCasePort

    @MockitoBean
    private lateinit var accessLogPrinter: AccessLogPrinter

    @MockitoBean
    private lateinit var discordUserErrorNotifier: DiscordUserErrorNotifier

    @MockitoBean
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @MockitoBean
    private lateinit var securityConstants: SecurityConstants

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
        const val SMS_SEND_PATH = "/api/notification/sms/send"
        const val SMS_MULTIPLE_SEND_PATH = "/api/notification/sms/send/multi"
    }

    @Test
    @DisplayName("단일 SMS 전송 성공")
    fun send_sms_success() {
        val body = """{"receiverNumber":"01012345678","location":"판교역"}"""
        val userDetails = SimpleTokenUserDetails("user@example.com", "라티", "ROLE_USER", "ACTIVE")

        mockMvc.perform(
            post(SMS_SEND_PATH)
                .with(csrf())
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isOk)
            .andDo(
                document(
                    "single-sms/success",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("알림 - 문자 알림")
                            .summary("문자를 통한 알람 발송용 api")
                            .description("앱을 사용하지 않는 사람에게 SMS로 알람 발송")
                            .build()
                    )
                )
            )

        verify(messageSendUseCasePort).send(
            eq("라티"),
            eq("01012345678"),
            eq("판교역")
        )
    }

    @Test
    @DisplayName("다중 SMS 전송 성공")
    fun send_multiple_sms_success() {
        val body = """{"receiversNumbers":["01011112222","01033334444"],"location":"강남역"}"""
        val userDetails = SimpleTokenUserDetails("user@example.com", "라티", "ROLE_USER", "ACTIVE")

        mockMvc.perform(
            post(SMS_MULTIPLE_SEND_PATH)
                .with(csrf())
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isOk)
            .andDo(
                document(
                    "multiple-sms/success",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("알림 - 다중 문자 알림")
                            .summary("문자(여러통)를 통한 알람 발송용 api")
                            .description("앱을 사용하지 않는 사람에게 SMS로 알람 발송")
                            .build()
                    )
                )
            )

        verify(messageSendUseCasePort).sendMultiple(
            eq("라티"),
            eq(listOf("01011112222", "01033334444")),
            eq("강남역")
        )
    }

    @Test
    @DisplayName("인증 없이 단일 SMS 요청 시 401")
    fun send_without_auth_returns_401() {
        val body = """{"receiverNumber":"01012345678","location":"판교역"}"""

        mockMvc.perform(
            post(SMS_SEND_PATH)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("인증 없이 다중 SMS 요청 시 401")
    fun send_multiple_without_auth_returns_401() {
        val body = """{"receiversNumbers":["01011112222"],"location":"강남역"}"""

        mockMvc.perform(
            post(SMS_MULTIPLE_SEND_PATH)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isUnauthorized)
    }
}
