package com.kdongsu5509.user.adapter.`in`.web.friends

import com.common.testUtil.ControllerTestSupport
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.kdongsu5509.user.adapter.out.persistence.friends.jpa.FriendRequestJpaEntity
import com.kdongsu5509.user.adapter.out.persistence.friends.jpa.SpringDataFriendRequestRepository
import com.kdongsu5509.user.adapter.out.persistence.user.jpa.SpringDataUserRepository
import com.kdongsu5509.user.adapter.out.persistence.user.jpa.UserJpaEntity
import com.kdongsu5509.user.application.service.user.SimpleTokenUserDetails
import com.kdongsu5509.user.domain.user.OAuth2Provider
import com.kdongsu5509.user.domain.user.UserRole
import com.kdongsu5509.user.domain.user.UserStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class FriendsRequestCommandControllerIntegrationTest : ControllerTestSupport() {

    companion object {
        const val FRIENDS_REQ_BASE_URL = "/api/user/friends/request"
        const val FRIEND_REQUEST_MESSAGE = "안녕하세요 저는 라티입니다! 친하게 지내요!"
        const val REQ_EMAIL = "requester1@kakao.com"
        const val REC_EMAIL = "receiver@kakao.com"
    }

    private lateinit var requester1: UserJpaEntity
    private lateinit var receiver: UserJpaEntity

    @Autowired
    lateinit var userRepository: SpringDataUserRepository

    @Autowired
    lateinit var friendRequestRepository: SpringDataFriendRequestRepository

    @BeforeEach
    fun setUp() {
        requester1 = createUser(REQ_EMAIL, "요청자")
        receiver = createUser(REC_EMAIL, "수신자")
    }

    @Test
    @WithMockUser(username = REQ_EMAIL)
    @DisplayName("친구 요청 API 호출 시 DB 저장까지 성공한다")
    fun requestFriendship_IntegrationSuccess() {
        val requestDto = mapOf(
            "receiverId" to receiver.id,
            "receiverEmail" to receiver.email,
            "message" to FRIEND_REQUEST_MESSAGE
        )

        mockMvc.perform(
            post(FRIENDS_REQ_BASE_URL)
                .param("v", "1")
                .with(
                    user(
                        SimpleTokenUserDetails(
                            requester1.email, "rati",
                            role = "NORMAL",
                            status = "ACTIVE"
                        )
                    )
                )
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .apply { content(jsonMapper.writeValueAsString(requestDto)) }
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.friendRequestId").exists())
            .andDo(
                document(
                    "friend-request-create",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("친구 요청")
                            .summary("친구 요청 생성")
                            .description("요청자(로그인 사용자)가 수신자(`receiverId`/`receiverEmail`)에게 메시지를 포함한 친구 요청을 생성합니다.")
                            .build()
                    )
                )
            )

        val savedRequests = friendRequestRepository.findAll()
        assertThat(savedRequests).hasSize(1)
            .first()
            .extracting("requester.id", "receiver.id")
            .containsExactly(requester1.id, receiver.id)
    }

    @Test
    @WithMockUser(username = REC_EMAIL)
    @DisplayName("친구 요청 수락 API 호출 시 친구 요청은 DB 삭제, 친구는 DB 저장까지 성공한다")
    fun acceptToFriendRequest() {
        val requestId = createFriendRequest(requester1, receiver)

        performPost("$FRIENDS_REQ_BASE_URL/accept/$requestId", receiver.email)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.friendEmail").value(requester1.email))
            .andDo(
                document(
                    "friend-request-accept",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("친구 요청")
                            .summary("친구 요청 수락")
                            .description("수신자가 `requestId`의 친구 요청을 수락합니다. 친구 요청은 삭제되고 친구 관계(양방향)가 생성됩니다.")
                            .build()
                    )
                )
            )
    }

    @Test
    @WithMockUser(username = REC_EMAIL)
    @DisplayName("친구 요청 거절 API 호출 시 친구 요청은 DB 삭제, 친구는 거절 DB에 저장까지 성공한다")
    fun rejectToFriendRequest() {
        val requestId = createFriendRequest(requester1, receiver)

        performPost("$FRIENDS_REQ_BASE_URL/reject/$requestId", receiver.email)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.targetEmail").value(requester1.email))
            .andDo(
                document(
                    "friend-request-reject",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("친구 요청")
                            .summary("친구 요청 거절")
                            .description("수신자가 `requestId`의 친구 요청을 거절합니다. 친구 요청은 삭제되고 REJECT 타입의 Restriction이 생성됩니다.")
                            .build()
                    )
                )
            )
    }

    private fun createUser(email: String, nickname: String) = userRepository.save(
        UserJpaEntity(email, nickname, UserRole.NORMAL, OAuth2Provider.KAKAO, UserStatus.ACTIVE)
    )

    private fun createFriendRequest(from: UserJpaEntity, to: UserJpaEntity): Long? {
        return friendRequestRepository.save(FriendRequestJpaEntity(from, to, FRIEND_REQUEST_MESSAGE)).id
    }

    private fun performPost(url: String, userEmail: String, content: Any? = null) = mockMvc.perform(
        post(url)
            .param("v", "1")
            .with(user(userEmail))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .apply { if (content != null) content(jsonMapper.writeValueAsString(content)) }
    )
}
