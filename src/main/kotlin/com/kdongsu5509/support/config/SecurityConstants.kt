package com.kdongsu5509.support.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SecurityConstants(
    @field:Value(value = "\${security.whitelist}")
    val whiteListUrls: List<String>
)