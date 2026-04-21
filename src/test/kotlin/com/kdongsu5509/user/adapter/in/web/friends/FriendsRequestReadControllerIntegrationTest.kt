package com.kdongsu5509.user.adapter.`in`.web.friends

import com.common.testUtil.ControllerTestSupport
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.kdongsu5509.user.adapter.`in`.web.friends.FriendsRequestReadControllerIntegrationTest.Companion.REC_EMAIL
import com.kdongsu5509.user.adapter.out.persistence.friends.jpa.FriendRequestJpaEntity
import com.kdongsu5509.user.adapter.out.persistence.friends.jpa.SpringDataFriendRequestRepository
import com.kdongsu5509.user.adapter.out.persistence.user.jpa.SpringDataUserRepository
import com.kdongsu5509.user.adapter.out.persistence.user.jpa.UserJpaEntity
import com.kdongsu5509.user.domain.user.OAuth2Provider
import com.kdongsu5509.user.domain.user.UserRole
import com.kdongsu5509.user.domain.user.UserStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WithMockUser(username = REC_EMAIL)
class FriendsRequestReadControllerIntegrationTest : ControllerTestSupport() {

    companion object {
        private const val BASE_URL = "/api/user/friends/request"
        const val REQ_EMAIL_1 = "requester1@kakao.com"
        const val REQ_EMAIL_2 = "requester2@kakao.com"
        const val REC_EMAIL = "receiver@kakao.com"
    }

    private lateinit var requester1: UserJpaEntity
    private lateinit var requester2: UserJpaEntity
    private lateinit var receiver: UserJpaEntity

    @Autowired
    lateinit var userRepository: SpringDataUserRepository

    @Autowired
    lateinit var friendRequestRepository: SpringDataFriendRequestRepository

    @BeforeEach
    fun setUp() {
        requester1 = userRepository.save(
            UserJpaEntity(REQ_EMAIL_1, "요청자", UserRole.NORMAL, OAuth2Provider.KAKAO, UserStatus.ACTIVE)
        )
        requester2 = userRepository.save(
            UserJpaEntity(REQ_EMAIL_2, "요청자", UserRole.NORMAL, OAuth2Provider.KAKAO, UserStatus.ACTIVE)
        )

        receiver = userRepository.save(
            UserJpaEntity(REC_EMAIL, "수신자", UserRole.NORMAL, OAuth2Provider.KAKAO, UserStatus.ACTIVE)
        )
    }

    @Test
    @DisplayName("받은 친구 요청 전부를 잘 찾는다.")
    fun getReceivedRequestAll_IntegrationSuccess() {
        //given
        val savedFriendRequest1 = friendRequestRepository.save(
            FriendRequestJpaEntity(
                requester1, receiver, "친하게 지내요"
            )
        )
        val savedFriendRequest2 = friendRequestRepository.save(
            FriendRequestJpaEntity(
                requester2, receiver, "친하게 지내요"
            )
        )

        // when
        val resultActions = mockMvc.perform(
            get(BASE_URL)
                .param("v", "1")
                .with(user(receiver.email))
                .with(csrf())
        )

        // then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data[0].friendRequestId").value(savedFriendRequest1.id.toString()))
            .andExpect(jsonPath("$.data[0].requesterEmail").value(savedFriendRequest1.requester.email))
            .andExpect(jsonPath("$.data[0].requesterNickname").value(savedFriendRequest1.requester.nickname))
            .andExpect(jsonPath("$.data[1].friendRequestId").value(savedFriendRequest2.id.toString()))
            .andExpect(jsonPath("$.data[1].requesterEmail").value(savedFriendRequest2.requester.email))
            .andExpect(jsonPath("$.data[1].requesterNickname").value(savedFriendRequest2.requester.nickname))
            .andDo(
                document(
                    "friend-request-received-list",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("친구 요청")
                            .summary("받은 친구 요청 전체 조회")
                            .description("로그인한 사용자(수신자)가 받은 모든 친구 요청을 요약 형태로 반환합니다.")
                            .build()
                    )
                )
            )
    }

    @Test
    @DisplayName("받은 친구 요청의 상세 정보를 잘 찾아 반환한다.")
    fun getReceivedRequestDetail_IntegrationSuccess() {
        //given
        val savedFriendRequest1 = friendRequestRepository.save(
            FriendRequestJpaEntity(
                requester1, receiver, "친하게 지내요"
            )
        )

        // when
        val resultActions = mockMvc.perform(
            get("$BASE_URL/${savedFriendRequest1.id}")
                .param("v", "1")
                .with(csrf())
        )

        // then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.friendRequestId").value(savedFriendRequest1.id.toString()))
            .andExpect(jsonPath("$.data.requesterEmail").value(savedFriendRequest1.requester.email))
            .andExpect(jsonPath("$.data.requesterNickname").value(savedFriendRequest1.requester.nickname))
            .andExpect(jsonPath("$.data.message").exists())
            .andDo(
                document(
                    "friend-request-received-detail",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("친구 요청")
                            .summary("받은 친구 요청 상세 조회")
                            .description("`requestId`로 받은 단일 친구 요청의 상세 내용(메시지 포함)을 반환합니다.")
                            .build()
                    )
                )
            )
    }
}