package com.kdongsu5509.imhereuserservice.application.port.`in`

import com.kdongsu5509.imhereuserservice.application.dto.SelfSignedJWT

interface ReissueJWTPort {
    fun reissue(refreshToken: String): SelfSignedJWT
}