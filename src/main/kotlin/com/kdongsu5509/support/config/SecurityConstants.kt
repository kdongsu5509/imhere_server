package com.kdongsu5509.support.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security")
class SecurityConstants(
    val whitelist: List<String> = emptyList()
)