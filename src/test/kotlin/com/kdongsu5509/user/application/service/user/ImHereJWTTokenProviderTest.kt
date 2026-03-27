package com.kdongsu5509.user.application.service.user

import com.kdongsu5509.support.exception.BusinessException
import com.kdongsu5509.user.application.port.out.user.CachePort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class ImHereJWTTokenProviderTest {

    companion object {
        const val USER_EMAIL = "test@example.com"
        const val USER_NICKNAME = "라티"

        const val ROLE = "USER"
        const val NORMAL_ROLE = "ROLE_NORMAL"

        const val ACTIVE_STATUS = "ACTIVE"

        const val ACCESS_TOKEN = "access-token"
        const val REFRESH_TOKEN = "refresh-token"

        const val NEW_ACCESS_TOKEN = "new-access-token"
        const val NEW_REFRESH_TOKEN = "new-refresh-token"

        val TEST_UUID: UUID? = UUID.randomUUID()
        val EXP_DATE: LocalDateTime = LocalDateTime.now().plusDays(7)
        const val REDIS_KEY = "refresh:$USER_EMAIL"
    }

    @Mock
    private lateinit var jwtTokenIssuer: JwtTokenIssuer

    @Mock
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @Mock
    private lateinit var cachePort: CachePort

    private lateinit var imHereJWTTokenProvider: ImHereJWTTokenProvider

    private lateinit var imHereJwtTokenElements: ImHereJwtTokenElements

    @BeforeEach
    fun setUp() {
        imHereJWTTokenProvider = ImHereJWTTokenProvider(jwtTokenIssuer, jwtTokenUtil, cachePort)
        imHereJwtTokenElements = ImHereJwtTokenElements(
            uid = TEST_UUID!!,
            userEmail = USER_EMAIL,
            userNickname = USER_NICKNAME,
            role = ROLE,
            status = ACTIVE_STATUS
        )
    }

    @Test
    @DisplayName("JWT 인증 토큰을 성공적으로 발급한다")
    fun issueJwtAuth_success() {
        // given
        `when`(jwtTokenIssuer.createAccessToken(imHereJwtTokenElements)).thenReturn(ACCESS_TOKEN)
        `when`(jwtTokenIssuer.createRefreshToken(imHereJwtTokenElements)).thenReturn(REFRESH_TOKEN)
        `when`(jwtTokenUtil.getExpirationDateFromToken(REFRESH_TOKEN)).thenReturn(EXP_DATE)

        // when
        val result = imHereJWTTokenProvider.issueJwtToken(imHereJwtTokenElements)

        // then
        assertThat(result).isNotNull
        assertThat(result.accessToken).isEqualTo(ACCESS_TOKEN)
        assertThat(result.refreshToken).isEqualTo(REFRESH_TOKEN)

        verify(jwtTokenIssuer).createAccessToken(imHereJwtTokenElements)
        verify(jwtTokenIssuer).createRefreshToken(imHereJwtTokenElements)
        verify(jwtTokenUtil).getExpirationDateFromToken(REFRESH_TOKEN)
    }

    @Test
    @DisplayName("유효한 리프레시 토큰으로 JWT 토큰을 재발급한다")
    fun reissueJwtToken_validRefreshTokenByRefreshToken_success() {
        // given
        `when`(jwtTokenUtil.getUserEmailFromToken(REFRESH_TOKEN)).thenReturn(USER_EMAIL)
        `when`(jwtTokenUtil.getRoleFromToken(REFRESH_TOKEN)).thenReturn(ROLE) // setUp()과 동일한 role 유지
        `when`(jwtTokenUtil.getUIDFromToken(REFRESH_TOKEN)).thenReturn(TEST_UUID)
        `when`(jwtTokenUtil.getUserNicknameFromToken(REFRESH_TOKEN)).thenReturn(USER_NICKNAME)
        `when`(jwtTokenUtil.getStatusFromToken(REFRESH_TOKEN)).thenReturn(ACTIVE_STATUS)
        `when`(jwtTokenUtil.validateToken(REFRESH_TOKEN)).thenReturn(true)
        `when`(cachePort.find(REDIS_KEY, String::class.java)).thenReturn(REFRESH_TOKEN)

        `when`(jwtTokenIssuer.createAccessToken(imHereJwtTokenElements)).thenReturn(NEW_ACCESS_TOKEN)
        `when`(jwtTokenIssuer.createRefreshToken(imHereJwtTokenElements)).thenReturn(NEW_REFRESH_TOKEN)
        `when`(jwtTokenUtil.getExpirationDateFromToken(NEW_REFRESH_TOKEN)).thenReturn(EXP_DATE)

        // when
        val result = imHereJWTTokenProvider.reissueJwtTokenByRefreshToken(REFRESH_TOKEN)

        // then
        assertThat(result).isNotNull
        assertThat(result.accessToken).isEqualTo(NEW_ACCESS_TOKEN)
        assertThat(result.refreshToken).isEqualTo(NEW_REFRESH_TOKEN)

        verify(jwtTokenUtil).validateToken(REFRESH_TOKEN)
        verify(cachePort).find(REDIS_KEY, String::class.java)
        verify(jwtTokenIssuer).createAccessToken(imHereJwtTokenElements)

        val keyCaptor: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
        val valueCaptor: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
        val durationCaptor: ArgumentCaptor<Duration> = ArgumentCaptor.forClass(Duration::class.java)
        verify(cachePort).save(capture(keyCaptor), capture(valueCaptor), capture(durationCaptor))
        assertThat(keyCaptor.value).isEqualTo(REDIS_KEY)
        assertThat(valueCaptor.value).isEqualTo(NEW_REFRESH_TOKEN)
    }

    private fun <T> capture(captor: ArgumentCaptor<T>): T = captor.capture()

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰으로 재발급 시 예외가 발생한다")
    fun reissueJwtToken_invalidRefreshTokenByRefreshToken_throwsException() {
        // given
        val invalidRefreshToken = "invalid-refresh-token"

        `when`(jwtTokenUtil.getUserEmailFromToken(invalidRefreshToken)).thenReturn(USER_EMAIL)
        `when`(jwtTokenUtil.getRoleFromToken(invalidRefreshToken)).thenReturn(ROLE)
        `when`(jwtTokenUtil.getUIDFromToken(invalidRefreshToken)).thenReturn(TEST_UUID)
        `when`(jwtTokenUtil.getUserNicknameFromToken(invalidRefreshToken)).thenReturn(USER_NICKNAME)
        `when`(jwtTokenUtil.getStatusFromToken(invalidRefreshToken)).thenReturn(ACTIVE_STATUS)
        `when`(jwtTokenUtil.validateToken(invalidRefreshToken)).thenReturn(false)

        // when & then
        assertThrows<BusinessException> {
            imHereJWTTokenProvider.reissueJwtTokenByRefreshToken(invalidRefreshToken)
        }.also { exception ->
            assertThat(exception.message).isEqualTo("잘못된 토큰입니다")
        }
    }

    @Test
    @DisplayName("만료된 리프레시 토큰으로 재발급 시 예외가 발생한다")
    fun reissueJwtToken_expiredRefreshTokenByRefreshToken_throwsException() {
        // given
        val expiredRefreshToken = "expired-refresh-token"

        `when`(jwtTokenUtil.getUIDFromToken(expiredRefreshToken)).thenReturn(TEST_UUID) // UUID.randomUUID() 제거
        `when`(jwtTokenUtil.getUserEmailFromToken(expiredRefreshToken)).thenReturn(USER_EMAIL)
        `when`(jwtTokenUtil.getUserNicknameFromToken(expiredRefreshToken)).thenReturn(USER_NICKNAME)
        `when`(jwtTokenUtil.getRoleFromToken(expiredRefreshToken)).thenReturn(ROLE)
        `when`(jwtTokenUtil.getStatusFromToken(expiredRefreshToken)).thenReturn(ACTIVE_STATUS)
        `when`(jwtTokenUtil.validateToken(expiredRefreshToken)).thenReturn(false)

        // when & then
        assertThrows<BusinessException> {
            imHereJWTTokenProvider.reissueJwtTokenByRefreshToken(expiredRefreshToken)
        }.also { exception ->
            assertThat(exception.message).isEqualTo("잘못된 토큰입니다")
        }
    }

    @Test
    @DisplayName("Redis에 저장된 토큰과 일치하지 않는 리프레시 토큰으로 재발급 시 예외가 발생한다")
    fun reissueJwtToken_mismatchedRefreshTokenByRefreshToken_throwsException() {
        // given
        val refreshToken = "refresh-token"
        val differentTokenInRedis = "different-refresh-token"
        val redisKey = "refresh:$USER_EMAIL"

        `when`(jwtTokenUtil.getUserEmailFromToken(refreshToken)).thenReturn(USER_EMAIL)
        `when`(jwtTokenUtil.getRoleFromToken(refreshToken)).thenReturn(ROLE)
        `when`(jwtTokenUtil.getUIDFromToken(refreshToken)).thenReturn(TEST_UUID)
        `when`(jwtTokenUtil.getUserNicknameFromToken(refreshToken)).thenReturn(USER_NICKNAME)
        `when`(jwtTokenUtil.getStatusFromToken(refreshToken)).thenReturn(ACTIVE_STATUS)

        `when`(jwtTokenUtil.validateToken(refreshToken)).thenReturn(true)
        `when`(cachePort.find(redisKey, String::class.java)).thenReturn(differentTokenInRedis)

        // when & then
        assertThrows<BusinessException> {
            imHereJWTTokenProvider.reissueJwtTokenByRefreshToken(refreshToken)
        }.also { exception ->
            assertThat(exception.message).isEqualTo("잘못된 토큰입니다")
        }
    }

    @Test
    @DisplayName("Redis에 리프레시 토큰이 없을 때 재발급 시 예외가 발생한다")
    fun reissueJwtToken_noTokenByRefreshTokenInRedis_throwsException() {
        // given
        `when`(jwtTokenUtil.getUserEmailFromToken(REFRESH_TOKEN)).thenReturn(USER_EMAIL)
        `when`(jwtTokenUtil.getRoleFromToken(REFRESH_TOKEN)).thenReturn(ROLE)
        `when`(jwtTokenUtil.getUIDFromToken(REFRESH_TOKEN)).thenReturn(TEST_UUID)
        `when`(jwtTokenUtil.getUserNicknameFromToken(REFRESH_TOKEN)).thenReturn(USER_NICKNAME)
        `when`(jwtTokenUtil.getStatusFromToken(REFRESH_TOKEN)).thenReturn(ACTIVE_STATUS)

        `when`(jwtTokenUtil.validateToken(REFRESH_TOKEN)).thenReturn(true)
        `when`(cachePort.find(REDIS_KEY, String::class.java)).thenReturn(null)

        // when & then
        assertThrows<BusinessException> {
            imHereJWTTokenProvider.reissueJwtTokenByRefreshToken(REFRESH_TOKEN)
        }.also { exception ->
            assertThat(exception.message).isEqualTo("잘못된 토큰입니다") // Exception 타입 및 메시지 수정
        }
    }

    @Test
    @DisplayName("재발급 시 토큰에서 추출한 role을 그대로 전달한다")
    fun reissueJwtTokenByRefreshToken_passesRoleAsIs() {
        // given
        `when`(jwtTokenUtil.getUserEmailFromToken(REFRESH_TOKEN)).thenReturn(USER_EMAIL)
        `when`(jwtTokenUtil.getRoleFromToken(REFRESH_TOKEN)).thenReturn(NORMAL_ROLE)
        `when`(jwtTokenUtil.getUIDFromToken(REFRESH_TOKEN)).thenReturn(TEST_UUID)
        `when`(jwtTokenUtil.getUserNicknameFromToken(REFRESH_TOKEN)).thenReturn(USER_NICKNAME)
        `when`(jwtTokenUtil.getStatusFromToken(REFRESH_TOKEN)).thenReturn(ACTIVE_STATUS)
        `when`(jwtTokenUtil.validateToken(REFRESH_TOKEN)).thenReturn(true)
        `when`(cachePort.find(REDIS_KEY, String::class.java)).thenReturn(REFRESH_TOKEN)

        val expectedElements = imHereJwtTokenElements.copy(role = NORMAL_ROLE)

        `when`(jwtTokenIssuer.createAccessToken(expectedElements)).thenReturn(NEW_ACCESS_TOKEN)
        `when`(jwtTokenIssuer.createRefreshToken(expectedElements)).thenReturn(NEW_REFRESH_TOKEN)
        `when`(jwtTokenUtil.getExpirationDateFromToken(NEW_REFRESH_TOKEN)).thenReturn(EXP_DATE)

        // when
        val result = imHereJWTTokenProvider.reissueJwtTokenByRefreshToken(REFRESH_TOKEN)

        // then
        assertThat(result.accessToken).isEqualTo(NEW_ACCESS_TOKEN)
        assertThat(result.refreshToken).isEqualTo(NEW_REFRESH_TOKEN)

        verify(jwtTokenIssuer).createAccessToken(expectedElements)
        verify(jwtTokenIssuer).createRefreshToken(expectedElements)
    }
}