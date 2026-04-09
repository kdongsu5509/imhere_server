package com.kdongsu5509.support.external

import com.kdongsu5509.user.application.service.user.ImHereJwtTokenElements
import com.kdongsu5509.user.application.service.user.JwtTokenIssuer
import com.kdongsu5509.user.domain.user.UserRole
import com.kdongsu5509.user.domain.user.UserStatus
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class OttLoginSuccessHandler(
    private val jwtTokenIssuer: JwtTokenIssuer,
    @param:Value("\${admin.id}") private val adminId: String,
    @param:Value("\${admin.nickname}") private val adminNickname: String
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val adminTokenElements = ImHereJwtTokenElements(
            uid = UUID.nameUUIDFromBytes(adminId.toByteArray()),
            userEmail = adminId,
            userNickname = adminNickname,
            role = UserRole.ADMIN.name,
            status = UserStatus.ACTIVE.name
        )

        val accessToken = jwtTokenIssuer.createAdminAccessToken(adminTokenElements)

        response.status = HttpServletResponse.SC_OK
        response.contentType = "application/json;charset=UTF-8"
        response.writer.write("""{"accessToken":"$accessToken"}""")
    }
}
