package com.kdongsu5509.imhereuserservice.adapter.`in`.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.SpringDataUserRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.UserJpaEntity
import com.kdongsu5509.imhereuserservice.domain.OAuth2Provider
import com.kdongsu5509.imhereuserservice.domain.UserRole
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var springDataUserRepository: SpringDataUserRepository

    @Autowired
    lateinit var mockMvc: MockMvc

    companion object {
        const val userSearchUrlWithoutKeyword = "/api/v1/user/search/"
        const val dongsuKo = "고동수"
    }

    @Test
    @DisplayName("없는 사람 조회")
    fun searchUsers() {

        val testKeyword = "none"
        mockMvc.perform(
            get(userSearchUrlWithoutKeyword + testKeyword)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("있는 사람 조회")
    fun searchUsers_with_really_exist() {
        //given
        val testUsersButSizeIsOne = createTestUser(1)
        springDataUserRepository.save(
            testUsersButSizeIsOne[0]
        )
        val testKeyword = "고동수"

        //when, then
        mockMvc.perform(
            get(userSearchUrlWithoutKeyword + testKeyword)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data[0].userEmail").value(testUsersButSizeIsOne[0].email))
            .andExpect(jsonPath("$.data[0].userNickname").value(dongsuKo))
    }

    @Test
    @DisplayName("닉네임 맞는 사람 모두 조회")
    fun searchUsers_with_really_exist_all() {
        //given
        springDataUserRepository.saveAll(createTestUser(10))

        //when, then
        mockMvc.perform(
            get(userSearchUrlWithoutKeyword + dongsuKo)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data", hasSize<Any>(10)))
            .andExpect(jsonPath("$.data[0].userNickname").value(dongsuKo))
    }

    private fun createTestUser(size: Int): List<UserJpaEntity> {
        val list = mutableListOf<UserJpaEntity>()

        for (i in 0 until size) {
            val testEmail = "test$i@test.com"

            val testUserEntity = UserJpaEntity(
                email = testEmail,
                nickname = dongsuKo,
                role = UserRole.NORMAL,
                provider = OAuth2Provider.KAKAO
            )
            list.add(testUserEntity)
        }

        return list
    }
}