package com.kdongsu5509.notifications.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "solapi")
data class ExternalSMSProperties(
    var sender: String = "",
    var apiKey: String = "",
    var apiSecret: String = ""
)