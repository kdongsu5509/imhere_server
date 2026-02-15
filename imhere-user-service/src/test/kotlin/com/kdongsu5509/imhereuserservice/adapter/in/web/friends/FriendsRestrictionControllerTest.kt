package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.FriendRestrictionJpaEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.SpringDataFriendRestrictionRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.SpringDataUserRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.SpringQueryDSLUserRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.UserJpaEntity
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestrictionType
import com.kdongsu5509.imhereuserservice.domain.user.OAuth2Provider
import com.kdongsu5509.imhereuserservice.domain.user.UserRole
import com.kdongsu5509.imhereuserservice.domain.user.UserStatus
import com.kdongsu5509.imhereuserservice.support.config.QueryDslConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(SpringQueryDSLUserRepository::class, QueryDslConfig::class)
class FriendsRestrictionControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val userRepository: SpringDataUserRepository,
    private val friendRestrictionRepository: SpringDataFriendRestrictionRepository
) {
    companion object {
        const val TEST_OWNER_EMAIL = "test0@test.com"
        const val START = 1
        const val END = 5
    }

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
            get("/api/v1/user/friends/restriction")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(END))
            .andExpect(jsonPath("$.data[0].targetEmail").exists())
            .andExpect(jsonPath("$.data[0].restrictionType").value("BLOCK"))
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