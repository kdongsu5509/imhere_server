package com.kdongsu5509.imhereuserservice.application.port.out

import com.kdongsu5509.imhereuserservice.domain.User

interface LoadUserPort {
    fun findByEmail(email: String): User
}