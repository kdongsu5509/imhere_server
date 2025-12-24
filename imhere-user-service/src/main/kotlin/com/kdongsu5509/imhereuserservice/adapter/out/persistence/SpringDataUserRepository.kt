package com.kdongsu5509.imhereuserservice.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SpringDataUserRepository : JpaRepository<UserJpaEntity, UUID> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): UserJpaEntity?
}