package com.kdongsu5509.user.adapter.`in`.web.user

import com.common.testUtil.ControllerTestSupport
import com.common.testUtil.TestJwtBuilder
import com.kdongsu5509.support.exception.AuthErrorCode
import com.kdongsu5509.user.adapter.out.auth.oauth.KakaoOauthClient
import com.kdongsu5509.user.adapter.out.auth.oauth.dto.OIDCPublicKey
import com.kdongsu5509.user.adapter.out.auth.oauth.dto.OIDCPublicKeyResponse
import com.kdongsu5509.user.application.port.out.user.UserSavePort
import com.kdongsu5509.user.domain.user.OAuth2Provider
import com.kdongsu5509.user.domain.user.User
import com.kdongsu5509.user.domain.user.UserRole
import com.kdongsu5509.user.domain.user.UserStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.security.interfaces.RSAPublicKey
import java.util.*

class AuthControllerIntegrationTest : ControllerTestSupport() {

    companion object {
        const val BASE_URL = "/api/user/auth"
        const val LOGIN_URL = "/login"
        const val REISSUE_URL = "/reissue"
        const val DEFAULT_TEST_EMAIL = "ds.ko@kakao.com"
    }

    @Autowired
    lateinit var userSavePort: UserSavePort

    @MockitoBean
    lateinit var kakaoOauthClient: KakaoOauthClient

    @Value("\${oidc.kakao.cache-key}")
    lateinit var kakaoCacheKey: String

    @BeforeEach
    fun setUp() {
        setMockKakaoPublicKey()
    }

    @Test
    @DisplayName("신규 유저는 kakao oauth를 통해 201 상태코드로 가입된다")
    fun login_success_new_member() {
        // given
        val idToken = TestJwtBuilder.buildValidIdTokenWithCustomEmail("new-user@kakao.com")

        // when & then
        performLogin(idToken)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data.accessToken").exists())
    }

    @Test
    @DisplayName("PENDING인 사용자도 201 상태코드를 반환한다")
    fun login_success_pending_member() {
        // given
        val email = "pending@kakao.com"
        saveUser(email, UserStatus.PENDING)
        val idToken = TestJwtBuilder.buildValidIdTokenWithCustomEmail(email)

        // when & then
        performLogin(idToken)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data.accessToken").exists())
    }

    @Test
    @DisplayName("기존 유저는 kakao oauth를 통해 200 상태코드로 로그인된다")
    fun login_success_existing_member() {
        // given
        saveUser(DEFAULT_TEST_EMAIL, UserStatus.ACTIVE)
        val idToken = TestJwtBuilder.buildValidIdToken()

        // when & then
        // 1. 첫 로그인
        performLogin(idToken).andExpect(status().isOk)

        // 2. 재로그인 (동일 정보)
        performLogin(idToken)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.accessToken").exists())
    }

    @Test
    @DisplayName("잘못된 토큰에 대해서는 오류 코드를 반환한다.")
    fun login_fail_with_invalid_token() {
        // given
        val invalidToken = "invalid_token"

        // when & then
        performLogin(invalidToken)
            .andExpect(status().is4xxClientError)
            .andExpect(jsonPath("$.code").value(400)) // APIResponse의 code 필드 확인
            .andExpect(jsonPath("$.data.message").value(AuthErrorCode.OIDC_INVALID.message))
    }

    @Test
    @WithMockUser(username = DEFAULT_TEST_EMAIL)
    @DisplayName("정상적인 refreshToken은 잘 통과한다.")
    fun reissue_okay() {
        // given
        val refreshToken = obtainRefreshToken()
        val requestBody = jsonMapper.writeValueAsString(
            mapOf("refreshToken" to refreshToken)
        )

        // when & then
        mockMvc.perform(
            post(BASE_URL + REISSUE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.accessToken").exists())
    }

    // --- Helper Methods ---

    private fun performLogin(idToken: String) = mockMvc.perform(
        post(BASE_URL + LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                jsonMapper.writeValueAsString(
                    mapOf(
                        "provider" to "KAKAO",
                        "idToken" to idToken
                    )
                )
            )
    )

    private fun saveUser(email: String, status: UserStatus) {
        userSavePort.save(
            User(UUID.randomUUID(), email, "테스터", OAuth2Provider.KAKAO, UserRole.NORMAL, status = status)
        )
    }

    private fun obtainRefreshToken(): String {
        val idToken = TestJwtBuilder.buildValidIdToken()
        val response = performLogin(idToken).andReturn().response.contentAsString

        return jsonMapper.readTree(response)
            .path("data")
            .path("refreshToken")
            .stringValue()
    }

    private fun setMockKakaoPublicKey() {
        val rsaPublicKey = TestJwtBuilder.testPublicKey as RSAPublicKey
        val encoder = Base64.getUrlEncoder().withoutPadding()

        val mockKey = OIDCPublicKey(
            kid = TestJwtBuilder.KAKAO_HEADER_KID,
            kty = "RSA",
            alg = TestJwtBuilder.KAKAO_HEADER_ALG,
            use = "sig",
            n = encoder.encodeToString(rsaPublicKey.modulus.toByteArray()),
            e = encoder.encodeToString(rsaPublicKey.publicExponent.toByteArray())
        )

        val mockResponse = OIDCPublicKeyResponse(keys = listOf(mockKey))
        `when`(kakaoOauthClient.getPublicKeyFromProvider()).thenReturn(mockResponse)
        redisTemplate.opsForValue().set(kakaoCacheKey, mockResponse)
    }
}
