package com.kdongsu5509.user.adapter.`in`.web.user

import com.common.testUtil.ControllerTestSupport
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import com.kdongsu5509.user.adapter.`in`.web.user.UserControllerIntegrationTest.Companion.TEST_OWNER_EMAIL
import com.kdongsu5509.user.adapter.`in`.web.user.dto.NicknameChangeRequest
import com.kdongsu5509.user.adapter.out.persistence.user.jpa.SpringDataUserRepository
import com.kdongsu5509.user.adapter.out.persistence.user.jpa.UserJpaEntity
import com.kdongsu5509.user.domain.user.OAuth2Provider
import com.kdongsu5509.user.domain.user.UserRole
import com.kdongsu5509.user.domain.user.UserStatus
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WithMockUser(username = TEST_OWNER_EMAIL)
class UserControllerIntegrationTest : ControllerTestSupport() {

    @Autowired
    lateinit var userRepository: SpringDataUserRepository

    companion object {
        const val BASE_URL = "/api/user/info"
        const val ME_URL = "/me"
        const val NICKNAME_URL = "/nickname"
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
    @DisplayName("없는 사람 조회 - 빈 결과를 검색 결과로 반환")
    fun searchUsers_empty() {
        mockMvc.perform(get(BASE_URL + ME_URL))
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    fun searchMe_success() {
        mockMvc.perform(get(BASE_URL + ME_URL))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.userEmail").value(TEST_OWNER_EMAIL))
            .andExpect(jsonPath("$.data.userNickname").value(TEST_OWNER_NICKNAME))
            .andDo(
                document(
                    "user-get-me",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("사용자")
                            .summary("내 정보 조회")
                            .description("로그인한 사용자의 이메일, 닉네임 정보를 반환합니다.")
                            .build()
                    )
                )
            )
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
            .andDo(
                document(
                    "user-search-by-nickname",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("사용자")
                            .summary("닉네임으로 유저 검색")
                            .description("닉네임 키워드로 유저를 검색합니다. 동일 닉네임의 복수 결과가 반환될 수 있습니다.")
                            .build()
                    )
                )
            )
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
    @DisplayName("내 닉네임 변경")
    fun changeNickname_success() {
        // given
        val newNickname = "고동수"
        val request = NicknameChangeRequest(newNickname)

        // when & then
        mockMvc.perform(
            post(BASE_URL + NICKNAME_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.userEmail").value(TEST_OWNER_EMAIL))
            .andExpect(jsonPath("$.data.userNickname").value(newNickname))
            .andDo(
                document(
                    "user-change-nickname",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("사용자")
                            .summary("닉네임 변경")
                            .description("로그인한 사용자의 닉네임을 변경합니다.")
                            .build()
                    )
                )
            )
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
