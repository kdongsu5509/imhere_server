package com.kdongsu5509.notifications.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ExternalSMSProperties::class)
class ExternalSMSConfig