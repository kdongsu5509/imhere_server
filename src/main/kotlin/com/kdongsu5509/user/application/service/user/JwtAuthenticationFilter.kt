package com.kdongsu5509.user.application.service.user

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenUtil: JwtTokenUtil
) : OncePerRequestFilter() {

    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private const val AUTH_HEADER = "Authorization"
    }

    public override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        request.servletPath.startsWith("/actuator")

    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwt = resolveToken(request) ?: return filterChain.doFilter(request, response)

        if (!jwtTokenUtil.validateToken(jwt)) {
            return sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token.")
        }

        if (SecurityContextHolder.getContext().authentication == null) {
            try {
                val authentication = createAuthentication(jwt, request)
                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: IllegalStateException) {
                return sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, e.message ?: "Forbidden")
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTH_HEADER)
        return if (bearerToken?.startsWith(BEARER_PREFIX) == true) {
            bearerToken.removePrefix(BEARER_PREFIX)
        } else null
    }

    private fun createAuthentication(jwt: String, request: HttpServletRequest): UsernamePasswordAuthenticationToken {
        val userDetails = SimpleTokenUserDetails(
            email = jwtTokenUtil.getUserEmailFromToken(jwt),
            nickname = jwtTokenUtil.getUserNicknameFromToken(jwt),
            role = jwtTokenUtil.getRoleFromToken(jwt),
            status = jwtTokenUtil.getStatusFromToken(jwt)
        )

        validateUserStatus(userDetails)

        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities).apply {
            details = WebAuthenticationDetailsSource().buildDetails(request)
        }
    }

    private fun validateUserStatus(userDetails: SimpleTokenUserDetails) {
        if (!userDetails.isEnabled) throw IllegalStateException("This account is disabled.")
        if (!userDetails.isAccountNonLocked) throw IllegalStateException("This account is locked.")
    }

    private fun sendErrorResponse(response: HttpServletResponse, status: Int, message: String) {
        response.status = status
        response.contentType = "application/json;charset=UTF-8"
        response.writer.use { it.print("""{"error": "$message"}""") }
    }
}