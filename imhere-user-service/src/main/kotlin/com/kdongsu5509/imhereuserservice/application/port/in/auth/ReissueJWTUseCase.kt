package com.kdongsu5509.imhereuserservice.application.port.`in`.auth

import com.kdongsu5509.imhereuserservice.application.dto.SelfSignedJWT

interface ReissueJWTUseCase {
    fun reissue(refreshToken: String): SelfSignedJWT
}