package com.kdongsu5509.imhereuserservice.application.service.user

import com.kdongsu5509.imhereuserservice.application.dto.ImHereJwt
import java.util.*

interface JwtTokenProvider {
    fun issueJwtToken(id: UUID, email: String, role: String): ImHereJwt

    fun reissueJwtToken(refreshToken: String): ImHereJwt
}