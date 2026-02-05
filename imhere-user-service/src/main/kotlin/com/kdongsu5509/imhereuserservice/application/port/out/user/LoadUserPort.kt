package com.kdongsu5509.imhereuserservice.application.port.out.user

import com.kdongsu5509.imhereuserservice.domain.user.User

interface LoadUserPort {
    fun findByEmail(email: String): User
    fun findByEmailAndNickname(keyword: String): List<User>
}