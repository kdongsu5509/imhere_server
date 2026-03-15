package com.kdongsu5509.user.application.port.out.user

import com.kdongsu5509.user.application.dto.OIDCDecodePayload
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