package com.kdongsu5509.imhereuserservice.application.port.`in`.user

import com.kdongsu5509.imhereuserservice.application.dto.SelfSignedJWT

interface ReissueJWTUseCase {
    fun reissue(refreshToken: String): SelfSignedJWT
}