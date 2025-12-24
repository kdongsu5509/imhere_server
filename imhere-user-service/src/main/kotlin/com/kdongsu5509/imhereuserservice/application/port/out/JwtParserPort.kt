package com.kdongsu5509.imhereuserservice.application.port.out

import com.kdongsu5509.imhereuserservice.application.dto.OIDCDecodePayload

interface JwtParserPort {
    fun parse(idToken: String): OIDCDecodePayload
}