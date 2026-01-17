package com.kdongsu5509.imhereuserservice.application.service

import com.kdongsu5509.imhereuserservice.application.port.out.LoadUserPort
import com.kdongsu5509.imhereuserservice.domain.OAuth2Provider
import com.kdongsu5509.imhereuserservice.domain.User
import com.kdongsu5509.imhereuserservice.domain.UserRole
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UserSearchServiceTest {
    @Mock
    lateinit var loadUserPort: LoadUserPort

    @InjectMocks
    lateinit var userSearchService: UserSearchService

    @Test
    @DisplayName("찾은 사용자가 1명 이상이면 User 리스트로 잘 반환해서 나간다")
    fun searchUser_overThanOne() {
        //given
        val testKeyword = "테스트"
        val testEmail = "test@test.com"
        val testNickname = "테스트"
        val testOauthProvider = OAuth2Provider.KAKAO
        val testRole = UserRole.NORMAL
        `when`(loadUserPort.findByEmailAndNickname(testKeyword)).thenReturn(
            listOf(User(testEmail, testNickname, testOauthProvider, testRole))
        )

        //when, then
        assertDoesNotThrow {
            userSearchService.searchUser(testKeyword)
        }
    }

    @Test
    @DisplayName("찾은 사용자가 0명 이어도 User 리스트로 잘 반환해서 나간다")
    fun searchUser_zero() {
        // given
        val testKeyword = "존재하지않음"
        `when`(loadUserPort.findByEmailAndNickname(testKeyword)).thenReturn(
            listOf()
        )

        // when
        val result = userSearchService.searchUser(testKeyword)

        // then
        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result).isEmpty()
        Assertions.assertThat(result).isInstanceOf(List::class.java)
    }
}