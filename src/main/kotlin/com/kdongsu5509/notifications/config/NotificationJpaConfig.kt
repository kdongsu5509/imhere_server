package com.kdongsu5509.notifications.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["com.kdongsu5509.notifications.adapter.out.persistence"])
class NotificationJpaConfig {
}