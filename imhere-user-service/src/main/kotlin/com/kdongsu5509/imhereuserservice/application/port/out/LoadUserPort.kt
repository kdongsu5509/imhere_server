package com.kdongsu5509.imhereuserservice.application.port.out

import com.kdongsu5509.imhereuserservice.domain.auth.User

interface LoadUserPort {
    fun findByEmail(email: String): User
    fun findByEmailAndNickname(keyword: String): List<User>
}