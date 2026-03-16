<<<<<<<< HEAD:src/main/kotlin/com/kdongsu5509/support/config/QueryDslConfig.kt
package com.kdongsu5509.support.config
========
package com.kdongsu5509.user.support.config
>>>>>>>> d7b9cc0345ce1535419ec55566096c1a808887e4:src/main/kotlin/com/kdongsu5509/user/support/config/QueryDslConfig.kt

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QueryDslConfig(private val entityManager: EntityManager) {
    @Bean
    fun japQueryFactory(): JPAQueryFactory {
        return JPAQueryFactory(entityManager)
    }
}