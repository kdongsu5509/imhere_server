package com.kdongsu5509.notifications.adapter.`in`

import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.kdongsu5509.support.config.QueryDslConfig
import com.kdongsu5509.user.adapter.out.persistence.user.jpa.SpringQueryDSLUserRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.json.JsonMapper

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(
    SpringQueryDSLUserRepository::class,
    QueryDslConfig::class,
)
class MessageControllerTest {


    @MockitoBean
    protected lateinit var firebaseMessaging: FirebaseMessaging

    @MockitoBean
    protected lateinit var firebaseApp: FirebaseApp

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var jsonMapper: JsonMapper

    @Test
    @WithMockUser(username = "ds.ko@kakao.com")
    @DisplayName("친구 차단 API 호출 시 관계 삭제 및 차단 목록에 추가된다")
    fun blockFriend_Success() {
        mockMvc.perform(
            post("/api/notification/sms/send")
                .with(user("dongsu@kakao.com"))
                .with(csrf())
        )
            .andExpect(status().isOk)
    }
}