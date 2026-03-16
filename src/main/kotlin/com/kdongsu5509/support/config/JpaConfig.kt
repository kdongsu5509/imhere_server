<<<<<<<< HEAD:src/main/kotlin/com/kdongsu5509/support/config/JpaConfig.kt
package com.kdongsu5509.support.config
========
package com.kdongsu5509.user.support.config
>>>>>>>> d7b9cc0345ce1535419ec55566096c1a808887e4:src/main/kotlin/com/kdongsu5509/user/support/config/JpaConfig.kt

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
<<<<<<<< HEAD:src/main/kotlin/com/kdongsu5509/support/config/JpaConfig.kt
========
@EnableJpaRepositories(
    basePackages = ["com.kdongsu5509.user.adapter.out.persistence"]
)
>>>>>>>> d7b9cc0345ce1535419ec55566096c1a808887e4:src/main/kotlin/com/kdongsu5509/user/support/config/JpaConfig.kt
class JpaConfig {

    @Bean
    fun auditorProvider(): AuditorAware<String> {
        return AuditorAware {
            val authentication = SecurityContextHolder.getContext().authentication

            if (authentication == null || !authentication.isAuthenticated) {
                return@AuditorAware Optional.empty()
            }

            Optional.ofNullable(authentication.name)
        }
    }
}