package com.kdongsu5509.imhereuserservice.application.service.auth

import com.kdongsu5509.imhereuserservice.application.service.auth.jwt.JwtTokenUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import java.io.PrintWriter
import java.io.StringWriter

@ExtendWith(MockitoExtension::class)
class JwtAuthenticationFilterTest {

    @Mock
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @Mock
    private lateinit var request: HttpServletRequest

    @Mock
    private lateinit var response: HttpServletResponse

    @Mock
    private lateinit var filterChain: FilterChain

    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    @BeforeEach
    fun setUp() {
        jwtAuthenticationFilter = JwtAuthenticationFilter(jwtTokenUtil)
        SecurityContextHolder.clearContext()
    }

    @Test
    @DisplayName("유효한 JWT 토큰으로 인증을 성공적으로 처리한다")
    fun doFilterInternal_validToken_success() {
        // given
        val token = "valid-jwt-token"
        val email = "test@example.com"
        val role = "ROLE_USER"
        val bearerToken = "Bearer $token"

        Mockito.`when`(request.getHeader("Authorization")).thenReturn(bearerToken)
        Mockito.`when`(jwtTokenUtil.validateToken(token)).thenReturn(true)
        Mockito.`when`(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(email)
        Mockito.`when`(jwtTokenUtil.getRoleFromToken(token)).thenReturn(role)

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain)

        // then
        Mockito.verify(jwtTokenUtil).validateToken(token)
        Mockito.verify(jwtTokenUtil).getUsernameFromToken(token)
        Mockito.verify(jwtTokenUtil).getRoleFromToken(token)
        Mockito.verify(filterChain).doFilter(request, response)
        Assertions.assertThat(SecurityContextHolder.getContext().authentication).isNotNull()
        Assertions.assertThat(SecurityContextHolder.getContext().authentication.name).isEqualTo(email)
    }

    @Test
    @DisplayName("토큰이 없으면 필터를 통과한다")
    fun doFilterInternal_noToken_passesThrough() {
        // given
        Mockito.`when`(request.getHeader("Authorization")).thenReturn(null)

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain)

        // then
        Mockito.verify(jwtTokenUtil, Mockito.never()).validateToken(ArgumentMatchers.anyString())
        Mockito.verify(filterChain).doFilter(request, response)
        Assertions.assertThat(SecurityContextHolder.getContext().authentication).isNull()
    }

    @Test
    @DisplayName("Bearer 접두사가 없으면 필터를 통과한다")
    fun doFilterInternal_noBearerPrefix_passesThrough() {
        // given
        Mockito.`when`(request.getHeader("Authorization")).thenReturn("invalid-token")

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain)

        // then
        Mockito.verify(jwtTokenUtil, Mockito.never()).validateToken(ArgumentMatchers.anyString())
        Mockito.verify(filterChain).doFilter(request, response)
    }

    @Test
    @DisplayName("유효하지 않은 토큰은 401 응답을 반환한다")
    fun doFilterInternal_invalidToken_returns401() {
        // given
        val token = "invalid-jwt-token"
        val bearerToken = "Bearer $token"

        Mockito.`when`(request.getHeader("Authorization")).thenReturn(bearerToken)
        Mockito.`when`(jwtTokenUtil.validateToken(token)).thenReturn(false)
        Mockito.`when`(response.writer).thenReturn(PrintWriter(StringWriter()))

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain)

        // then
        Mockito.verify(jwtTokenUtil).validateToken(token)
        Mockito.verify(response).status = 401
        Mockito.verify(response).contentType = "application/json;charset=UTF-8"
        Mockito.verify(filterChain, Mockito.never()).doFilter(request, response)
    }

    @Test
    @DisplayName("actuator 경로는 필터를 건너뛴다")
    fun shouldNotFilter_actuatorPath_returnsTrue() {
        // given
        Mockito.`when`(request.servletPath).thenReturn("/actuator/health")

        // when
        val result = jwtAuthenticationFilter.shouldNotFilter(request)

        // then
        Assertions.assertThat(result).isTrue()
    }

    @Test
    @DisplayName("일반 경로는 필터를 실행한다")
    fun shouldNotFilter_normalPath_returnsFalse() {
        // given
        Mockito.`when`(request.servletPath).thenReturn("/api/test")

        // when
        val result = jwtAuthenticationFilter.shouldNotFilter(request)

        // then
        Assertions.assertThat(result).isFalse()
    }

    @Test
    @DisplayName("이미 인증된 사용자가 있으면 새로운 인증을 설정하지 않는다")
    fun doFilterInternal_alreadyAuthenticated_doesNotSetNewAuthentication() {
        // given
        val token = "valid-jwt-token"
        val email = "test@example.com"
        val role = "ROLE_USER"
        val bearerToken = "Bearer $token"

        // 기존 인증 설정
        val existingAuth = UsernamePasswordAuthenticationToken(
            "existing@example.com",
            null,
            listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        )
        SecurityContextHolder.getContext().authentication = existingAuth

        Mockito.`when`(request.getHeader("Authorization")).thenReturn(bearerToken)
        Mockito.`when`(jwtTokenUtil.validateToken(token)).thenReturn(true)
        Mockito.`when`(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(email)
        Mockito.`when`(jwtTokenUtil.getRoleFromToken(token)).thenReturn(role)

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain)

        // then
        Mockito.verify(jwtTokenUtil).validateToken(token)
        Mockito.verify(filterChain).doFilter(request, response)
        // 기존 인증이 유지되어야 함
        Assertions.assertThat(SecurityContextHolder.getContext().authentication).isEqualTo(existingAuth)
    }
}