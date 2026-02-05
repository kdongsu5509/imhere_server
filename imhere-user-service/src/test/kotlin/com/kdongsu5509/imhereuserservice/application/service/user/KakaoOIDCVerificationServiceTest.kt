package com.kdongsu5509.imhereuserservice.application.service.user

import com.kdongsu5509.imhereuserservice.application.dto.OIDCDecodePayload
import com.kdongsu5509.imhereuserservice.application.port.out.user.JwtParserPort
import com.kdongsu5509.imhereuserservice.application.port.out.user.JwtVerificationPort
import io.jsonwebtoken.MalformedJwtException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class KakaoOIDCVerificationServiceTest {

    @Mock
    private lateinit var jwtParserPort: JwtParserPort

    @Mock
    private lateinit var jwtVerificationPort: JwtVerificationPort

    private lateinit var kakaoOIDCVerificationService: KakaoOIDCVerificationService

    private val payload = OIDCDecodePayload(
        iss = "https://kauth.kakao.com",
        aud = "test-audience",
        sub = "test-sub",
        email = "test@example.com",
        nickname = "고동수"
    )

    @BeforeEach
    fun setUp() {
        kakaoOIDCVerificationService = KakaoOIDCVerificationService(jwtParserPort, jwtVerificationPort)
    }

    @Test
    @DisplayName("유효한 ID 토큰으로 사용자 정보를 성공적으로 반환한다")
    fun verifyAndReturnUserInformation_success() {
        // given
        val idToken = "valid-id-token"
        val email = "test@example.com"

        `when`(jwtParserPort.parse(idToken)).thenReturn(payload)
        doNothing().`when`(jwtVerificationPort).verifyPayLoad(payload)

        // when
        val result = kakaoOIDCVerificationService.verifyAndReturnUserInformation(idToken)

        // then
        assertThat(result).isNotNull()
        assertThat(result.email).isEqualTo(email)
        assertThat(result.nickname).isEqualTo("고동수")

        verify(jwtParserPort).parse(idToken)
        verify(jwtVerificationPort).verifyPayLoad(payload)
    }

    @Test
    @DisplayName("이메일이 없는 ID 토큰은 예외를 발생시킨다")
    fun verifyAndReturnUserInformation_noEmail_throwsException() {
        // given
        val idToken = "token-without-email"
        val payloadWithoutEmail = OIDCDecodePayload(
            iss = "https://kauth.kakao.com",
            aud = "test-audience",
            sub = "test-sub",
            email = null,
            nickname = "고동수"
        )

        `when`(jwtParserPort.parse(idToken)).thenReturn(payloadWithoutEmail)
        doNothing().`when`(jwtVerificationPort).verifyPayLoad(payloadWithoutEmail)

        // when & then
        assertThrows<MalformedJwtException> {
            kakaoOIDCVerificationService.verifyAndReturnUserInformation(idToken)
        }.also { exception ->
            assertThat(exception.message).isEqualTo("ID 토큰에 이메일 정보가 없습니다")
        }

        verify(jwtParserPort).parse(idToken)
        verify(jwtVerificationPort).verifyPayLoad(payloadWithoutEmail)
    }

    @Test
    @DisplayName("페이로드 검증 실패 시 예외를 전파한다")
    fun verifyAndReturnUserInformation_payloadVerificationFails_throwsException() {
        // given
        val idToken = "invalid-token"
        val exception = MalformedJwtException("Invalid payload")

        `when`(jwtParserPort.parse(idToken)).thenReturn(payload)
        doThrow(exception).`when`(jwtVerificationPort).verifyPayLoad(payload)

        // when & then
        assertThrows<MalformedJwtException> {
            kakaoOIDCVerificationService.verifyAndReturnUserInformation(idToken)
        }

        verify(jwtParserPort).parse(idToken)
        verify(jwtVerificationPort).verifyPayLoad(payload)
    }

    @Test
    @DisplayName("닉네임이 없는 ID 토큰은 빈 문자열로 반환한다")
    fun verifyAndReturnUserInformation_noNickname_returnsEmptyString() {
        // given
        val idToken = "token-without-nickname"
        val payloadWithoutNickname = OIDCDecodePayload(
            iss = "https://kauth.kakao.com",
            aud = "test-audience",
            sub = "test-sub",
            email = "test@example.com",
            nickname = null
        )

        `when`(jwtParserPort.parse(idToken)).thenReturn(payloadWithoutNickname)
        doNothing().`when`(jwtVerificationPort).verifyPayLoad(payloadWithoutNickname)

        // when
        val result = kakaoOIDCVerificationService.verifyAndReturnUserInformation(idToken)

        // then
        assertThat(result).isNotNull()
        assertThat(result.email).isEqualTo("test@example.com")
        assertThat(result.nickname).isEqualTo("")

        verify(jwtParserPort).parse(idToken)
        verify(jwtVerificationPort).verifyPayLoad(payloadWithoutNickname)
    }
}

