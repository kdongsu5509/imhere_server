package com.kdongsu5509.user.adapter.`in`.web.friends

import com.common.testUtil.ControllerTestSupport
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.kdongsu5509.user.adapter.out.persistence.friends.jpa.FriendRestrictionJpaEntity
import com.kdongsu5509.user.adapter.out.persistence.friends.jpa.SpringDataFriendRestrictionRepository
import com.kdongsu5509.user.adapter.out.persistence.user.jpa.SpringDataUserRepository
import com.kdongsu5509.user.adapter.out.persistence.user.jpa.UserJpaEntity
import com.kdongsu5509.user.domain.friend.FriendRestrictionType
import com.kdongsu5509.user.domain.user.OAuth2Provider
import com.kdongsu5509.user.domain.user.UserRole
import com.kdongsu5509.user.domain.user.UserStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class FriendsRestrictionControllerTest : ControllerTestSupport() {

    companion object {

        const val FRIENDS_RESTRICTION_BASE_URL = "/api/user/friends/restriction"
        const val TEST_OWNER_EMAIL = "test0@test.com"
        const val START = 1
        const val END = 5
    }

    @Autowired
    lateinit var userRepository: SpringDataUserRepository

    @Autowired
    lateinit var friendRestrictionRepository: SpringDataFriendRestrictionRepository

    @BeforeEach
    fun setUp() {
        val owner = userRepository.save(
            UserJpaEntity(
                TEST_OWNER_EMAIL,
                "나",
                UserRole.NORMAL,
                OAuth2Provider.KAKAO,
                UserStatus.ACTIVE
            )
        )

        createTenFriendsAndRestrictions(owner)
    }

    @Test
    @DisplayName("인증된 사용자의 차단/거절 목록을 가져온다")
    @WithMockUser(username = "test0@test.com")
    fun getMyRestrictedFriends_success() {
        mockMvc.perform(
            get(FRIENDS_RESTRICTION_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(END))
            .andExpect(jsonPath("$.data[0].targetEmail").exists())
            .andExpect(jsonPath("$.data[0].restrictionType").value("BLOCK"))
            .andDo(
                document(
                    "friends-restriction-list",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("친구 제한")
                            .summary("내 제한(BLOCK/REJECT) 목록 조회")
                            .description("로그인한 사용자가 차단하거나 거절한 대상 전체 목록을 타입(BLOCK/REJECT)과 함께 반환합니다.")
                            .build()
                    )
                )
            )
    }

    private fun createTenFriendsAndRestrictions(owner: UserJpaEntity) {
        (START..END).forEach { idx ->
            val friend = userRepository.save(
                createTestFriend(idx)
            )

            friendRestrictionRepository.save(
                createdTestFriendRestriction(owner, friend, idx)
            )
        }
    }

    @Test
    @DisplayName("친구 제한을 삭제하면 삭제된 정보와 함께 201 응답을 받는다")
    @WithMockUser(username = TEST_OWNER_EMAIL)
    fun unrestrict_success() {
        // given
        val restriction = friendRestrictionRepository.findAll()
            .first { it.actor.email == TEST_OWNER_EMAIL }
        val targetId = restriction.id!!
        val targetEmail = restriction.target.email

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("$FRIENDS_RESTRICTION_BASE_URL/$targetId")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.code").value(201))
            .andExpect(jsonPath("$.data.targetEmail").value(targetEmail))
            .andDo(
                document(
                    "friends-restriction-delete-success",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("친구 제한")
                            .summary("친구 제한 해제")
                            .description("본인 소유의 `friendRestrictionId`에 해당하는 제한을 삭제합니다. 성공 시 201과 함께 삭제된 대상 정보를 반환합니다.")
                            .build()
                    )
                )
            )

        val isExists = friendRestrictionRepository.findById(targetId).isPresent
        assertThat(isExists).isFalse()
    }

    @Test
    @DisplayName("본인의 것이 아닌 제한 내역을 삭제하려 하면 예외가 발생한다")
    @WithMockUser(username = "other@test.com")
    fun unrestrict_fail_forbidden() {
        // given -> TEST_OWNER_EMAIL 소유의 데이터
        val restriction = friendRestrictionRepository.findAll()
            .first { it.actor.email == TEST_OWNER_EMAIL }
        val targetId = restriction.id!!

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("$FRIENDS_RESTRICTION_BASE_URL/$targetId")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest) // BusinessException (ErrorCode 확인)
            .andExpect(jsonPath("$.message").exists())
            .andDo(
                document(
                    "friends-restriction-delete-fail-forbidden",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("친구 제한")
                            .summary("친구 제한 해제 실패 - 본인 소유 아님")
                            .description(
                                """
                                로그인한 사용자의 소유가 아닌 `friendRestrictionId`에 대한 제한 해제 요청 시
                                400(BusinessException)이 반환되며, 어떤 데이터도 변경되지 않습니다.
                                """.trimIndent()
                            )
                            .build()
                    )
                )
            )
    }

    private fun createTestFriend(idx: Int): UserJpaEntity = UserJpaEntity(
        "friend$idx@test.com",
        "친구$idx",
        UserRole.NORMAL,
        OAuth2Provider.KAKAO,
        UserStatus.ACTIVE
    )

    private fun createdTestFriendRestriction(
        owner: UserJpaEntity,
        friend: UserJpaEntity,
        idx: Int
    ): FriendRestrictionJpaEntity = FriendRestrictionJpaEntity(
        actor = owner,
        target = friend,
        type = if (idx % 2 == 0) FriendRestrictionType.REJECT else FriendRestrictionType.BLOCK
    )
}