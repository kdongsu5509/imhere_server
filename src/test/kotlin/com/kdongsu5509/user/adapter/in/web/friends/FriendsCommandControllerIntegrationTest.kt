package com.kdongsu5509.user.adapter.`in`.web.friends

import com.common.testUtil.ControllerTestSupport
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.kdongsu5509.user.adapter.`in`.web.friends.FriendsCommandControllerIntegrationTest.Companion.USER_A_EMAIL
import com.kdongsu5509.user.adapter.out.persistence.friends.jpa.FriendRelationshipsJpaEntity
import com.kdongsu5509.user.adapter.out.persistence.friends.jpa.SpringDataFriendRelationshipsRepository
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
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WithMockUser(username = USER_A_EMAIL)
class FriendsCommandControllerIntegrationTest : ControllerTestSupport() {

    companion object {
        const val FRIENDS_COMMAND_BASE_URL = "/api/user/friends"
        const val FRIENDS_ALIAS_URL = "/alias"
        const val FRIENDS_BLOCK_URL = "/block/{friendRelationshipId}"

        const val USER_A_EMAIL = "userA@test.com"
        const val USER_B_EMAIL = "user@test.com"
    }

    @Autowired
    lateinit var userRepository: SpringDataUserRepository

    @Autowired
    lateinit var friendRelationshipsRepository: SpringDataFriendRelationshipsRepository

    @Autowired
    lateinit var friendRestrictionRepository: SpringDataFriendRestrictionRepository

    private lateinit var userA: UserJpaEntity
    private lateinit var userB: UserJpaEntity
    private lateinit var relationshipAtoB: FriendRelationshipsJpaEntity
    private lateinit var relationshipBtoA: FriendRelationshipsJpaEntity

    @BeforeEach
    fun setUp() {
        userA = createUser(USER_A_EMAIL, "UserA")
        userB = createUser(USER_B_EMAIL, "UserB")

        relationshipAtoB = friendRelationshipsRepository.save(FriendRelationshipsJpaEntity.create(userA, userB))
        relationshipBtoA = friendRelationshipsRepository.save(FriendRelationshipsJpaEntity.create(userB, userA))
    }

    @Test
    @DisplayName("친구 삭제 API 호출 시 양방향 관계가 모두 삭제된다")
    fun deleteFriend_Success() {
        mockMvc.perform(
            delete("$FRIENDS_COMMAND_BASE_URL/{friendRelationshipId}", relationshipAtoB.id)
                .with(user(USER_A_EMAIL))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "friends-delete",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("친구")
                            .summary("친구 관계 삭제")
                            .description("요청자의 `friendRelationshipId`를 기준으로 양방향 친구 관계를 모두 삭제합니다.")
                            .build()
                    )
                )
            )

        assertThat(friendRelationshipsRepository.findById(relationshipAtoB.id!!)).isEmpty
        assertThat(friendRelationshipsRepository.findById(relationshipBtoA.id!!)).isEmpty
    }

    @Test
    @DisplayName("친구 차단 API 호출 시 관계 삭제 및 차단 목록에 추가된다")
    fun blockFriend_Success() {
        mockMvc.perform(
            post("$FRIENDS_COMMAND_BASE_URL$FRIENDS_BLOCK_URL", relationshipAtoB.id)
                .with(user(USER_A_EMAIL))
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "friends-block",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("친구")
                            .summary("친구 차단")
                            .description(
                                """
                                `friendRelationshipId`로 지정된 친구 관계를 양방향 삭제하고,
                                해당 대상에 대해 BLOCK 타입의 Restriction을 생성합니다.
                                """.trimIndent()
                            )
                            .build()
                    )
                )
            )

        assertThat(friendRelationshipsRepository.findById(relationshipAtoB.id!!)).isEmpty
        assertThat(friendRelationshipsRepository.findById(relationshipBtoA.id!!)).isEmpty

        val restrictions = friendRestrictionRepository.findByActorId(userA.id!!)
        assertThat(restrictions).hasSize(1)
        assertThat(restrictions[0].target.id).isEqualTo(userB.id)
        assertThat(restrictions[0].type).isEqualTo(FriendRestrictionType.BLOCK)
    }

    @Test
    @DisplayName("친구 별명 변경 API 호출 시 별명이 업데이트된다")
    fun updateFriendAlias_Success() {
        val request = mapOf(
            "friendRelationshipId" to relationshipAtoB.id.toString(),
            "newFriendAlias" to "Bestie"
        )

        mockMvc.perform(
            post(FRIENDS_COMMAND_BASE_URL + FRIENDS_ALIAS_URL)
                .with(user(USER_A_EMAIL))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.friendAlias").value("Bestie"))
            .andDo(
                document(
                    "friends-alias-update",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("친구")
                            .summary("친구 별명 변경")
                            .description("요청자의 지정 `friendRelationshipId`에 대한 친구 별명(`friendAlias`)을 업데이트합니다.")
                            .build()
                    )
                )
            )

        val updated = friendRelationshipsRepository.findById(relationshipAtoB.id!!).get()
        assertThat(updated.friendAlias).isEqualTo("Bestie")
    }

    private fun createUser(email: String, nickname: String) = userRepository.save(
        UserJpaEntity(email, nickname, UserRole.NORMAL, OAuth2Provider.KAKAO, UserStatus.ACTIVE)
    )
}
