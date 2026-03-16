package com.kdongsu5509.user.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["com.kdongsu5509.user.adapter.out.persistence"])
class UserJpaConfig {
}