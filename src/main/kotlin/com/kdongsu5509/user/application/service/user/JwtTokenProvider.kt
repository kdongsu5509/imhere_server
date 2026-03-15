package com.kdongsu5509.user.application.service.user

import com.kdongsu5509.user.application.dto.ImHereJwt
import java.util.*

interface JwtTokenProvider {
    fun issueJwtToken(id: UUID, email: String, role: String): ImHereJwt

    fun reissueJwtToken(refreshToken: String): ImHereJwt
}