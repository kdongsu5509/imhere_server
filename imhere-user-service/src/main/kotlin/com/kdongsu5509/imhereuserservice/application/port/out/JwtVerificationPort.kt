package com.kdongsu5509.imhereuserservice.application.port.out

import com.kdongsu5509.imhereuserservice.application.dto.OIDCDecodePayload
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws

interface JwtVerificationPort {
    fun verifyPayLoad(payload: OIDCDecodePayload)

    fun verifySignature(
        token: String,
        modulus: String,
        exponent: String
    ): Jws<Claims>
}