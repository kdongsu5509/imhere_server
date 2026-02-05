package com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.adapter

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.SpringDataUserRepository
import com.kdongsu5509.imhereuserservice.application.port.out.user.CheckUserPort
import org.springframework.stereotype.Component

@Component
class UserCheckPersistenceAdapter(
    private val springDataUserRepository: SpringDataUserRepository,

    ) : CheckUserPort {
    override fun existsByEmail(email: String): Boolean {
        return springDataUserRepository.existsByEmail(email)
    }
}