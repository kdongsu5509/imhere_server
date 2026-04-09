package com.kdongsu5509.user.application.service.user

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class AdminSecretFilter(
    private val adminSecret: String
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.requestURI == "/api/admin/auth/ott" && request.method == "POST") {
            val receivedHeader = request.getHeader("X-ADMIN-SECRET")

            if (receivedHeader == null || receivedHeader != adminSecret) {
                logger.warn("부적절한 관리자 접근 시도 감지: IP ${request.remoteAddr}")
                response.status = HttpServletResponse.SC_FORBIDDEN
                response.characterEncoding = "UTF-8"
                response.contentType = "text/plain"
                response.writer.write("관리자 인증 헤더가 누락되었거나 일치하지 않습니다.")
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}
