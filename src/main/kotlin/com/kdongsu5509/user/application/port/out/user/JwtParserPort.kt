package com.kdongsu5509.user.application.port.out.user

import com.kdongsu5509.user.application.dto.OIDCDecodePayload

interface JwtParserPort {
    fun parse(idToken: String): OIDCDecodePayload
}