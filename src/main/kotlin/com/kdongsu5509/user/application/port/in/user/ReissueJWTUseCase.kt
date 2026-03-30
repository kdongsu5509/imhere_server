package com.kdongsu5509.user.application.port.`in`.user

import com.kdongsu5509.user.application.dto.ImHereJwt

interface ReissueJWTUseCase {
    fun reissueByRefreshToken(refreshToken: String): ImHereJwt
    fun reissueByUserEmail(email: String): ImHereJwt
}