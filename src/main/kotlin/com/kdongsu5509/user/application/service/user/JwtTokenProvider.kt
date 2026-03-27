package com.kdongsu5509.user.application.service.user

import com.kdongsu5509.user.application.dto.ImHereJwt

interface JwtTokenProvider {
    fun issueJwtToken(imHereJwtTokenElements: ImHereJwtTokenElements): ImHereJwt

    fun reissueJwtToken(refreshToken: String): ImHereJwt
}