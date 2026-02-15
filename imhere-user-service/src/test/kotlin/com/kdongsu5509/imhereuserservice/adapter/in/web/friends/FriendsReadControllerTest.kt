package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.FriendRelationshipsJpaEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.SpringDataFriendRelationshipsRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.SpringDataUserRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.UserJpaEntity
import com.kdongsu5509.imhereuserservice.domain.user.OAuth2Provider
import com.kdongsu5509.imhereuserservice.domain.user.UserRole
import com.kdongsu5509.imhereuserservice.domain.user.UserStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
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
class FriendsReadControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val userRepository: SpringDataUserRepository,
    private val friendRelationshipRepository: SpringDataFriendRelationshipsRepository
) {

    @BeforeEach
    fun setUp() {
        val owner = userRepository.save(
            UserJpaEntity(
                "test@test.com",
                "나",
                UserRole.NORMAL,
                OAuth2Provider.KAKAO,
                UserStatus.ACTIVE
            )
        )
        val friend = userRepository.save(
            UserJpaEntity(
                "friend@test.com",
                "친구",
                UserRole.NORMAL,
                OAuth2Provider.KAKAO,
                UserStatus.ACTIVE
            )
        )

        friendRelationshipRepository.save(
            FriendRelationshipsJpaEntity(
                ownerUser = owner,
                friendUser = friend,
                friendAlias = "베프"
            )
        )
    }

    @Test
    @DisplayName("인증된 사용자의 친구 목록을 가져온다")
    @WithMockUser(username = "test@test.com")
    fun getMyFriends_success() {
        mockMvc.perform(
            get("/api/v1/user/friends")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data[0].friendAlias").value("베프"))
            .andExpect(jsonPath("$.data[0].friendEmail").value("friend@test.com"))
    }
}