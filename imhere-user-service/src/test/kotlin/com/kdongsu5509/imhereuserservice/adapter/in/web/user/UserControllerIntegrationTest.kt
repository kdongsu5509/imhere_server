package com.kdongsu5509.imhereuserservice.adapter.`in`.web.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.user.UserControllerIntegrationTest.Companion.TEST_OWNER_EMAIL
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.user.dto.NicknameChangeRequest
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.SpringDataUserRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.UserJpaEntity
import com.kdongsu5509.imhereuserservice.domain.user.OAuth2Provider
import com.kdongsu5509.imhereuserservice.domain.user.UserRole
import com.kdongsu5509.imhereuserservice.domain.user.UserStatus
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@WithMockUser(username = TEST_OWNER_EMAIL)
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var userRepository: SpringDataUserRepository

    @Autowired
    lateinit var mockMvc: MockMvc

    companion object {
        const val BASE_URL = "/api/v1/user/info"
        const val TEST_EMAIL = "test@test.com"
        const val TEST_NICKNAME = "tester"
        const val TEST_OWNER_EMAIL = "owner@owner.com"
        const val TEST_OWNER_NICKNAME = "owner"
    }

    @BeforeEach
    fun setUp() {
        saveUser(TEST_OWNER_EMAIL, TEST_OWNER_NICKNAME)
    }

    @Test
    @DisplayName("없는 사람 조회 - 빈 결과 반환")
    fun searchUsers_empty() {
        mockMvc.perform(get("$BASE_URL/none"))
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("존재하는 닉네임으로 조회 시 유저 정보 반환")
    fun searchUsers_success() {
        // given
        val user = saveUser(email = "exist@test.com", nickname = TEST_NICKNAME)

        // when & then
        mockMvc.perform(get("$BASE_URL/${user.nickname}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data[0].userId").value(user.id.toString()))
            .andExpect(jsonPath("$.data[0].userEmail").value(user.email))
            .andExpect(jsonPath("$.data[0].userNickname").value(TEST_NICKNAME))
    }

    @Test
    @DisplayName("닉네임이 중복된 경우 모든 유저 조회")
    fun searchUsers_multiple_results() {
        // given
        repeat(10) { i -> saveUser(email = "test$i@test.com") }

        // when & then
        mockMvc.perform(get("$BASE_URL/$TEST_NICKNAME"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data", hasSize<Any>(10)))
            .andExpect(jsonPath("$.data[0].userNickname").value(TEST_NICKNAME))
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    fun searchMe_success() {
        // when & then
        mockMvc.perform(get("$BASE_URL/me"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.userEmail").value(TEST_OWNER_EMAIL))
            .andExpect(jsonPath("$.data.userNickname").value(TEST_OWNER_NICKNAME))
    }


    @Test
    @DisplayName("내 닉네임 변경")
    fun changeNickname_success() {
        // given
        val newNickname = "dongsuKo"
        val request = NicknameChangeRequest(newNickname)

        // when & then
        mockMvc.perform(
            post("$BASE_URL/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.userEmail").value(TEST_OWNER_EMAIL))
            .andExpect(jsonPath("$.data.userNickname").value(newNickname))
    }

    private fun saveUser(
        email: String = TEST_EMAIL,
        nickname: String = TEST_NICKNAME,
        role: UserRole = UserRole.NORMAL,
        provider: OAuth2Provider = OAuth2Provider.KAKAO,
        status: UserStatus = UserStatus.ACTIVE
    ): UserJpaEntity {
        return userRepository.save(
            UserJpaEntity(
                email = email,
                nickname = nickname,
                role = role,
                provider = provider,
                status = status
            )
        )
    }

}