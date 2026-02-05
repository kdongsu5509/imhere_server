package com.kdongsu5509.imhereuserservice.application.port.out.user

import com.kdongsu5509.imhereuserservice.domain.user.User

interface UserLoadPort {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): User
    fun findByEmailAndNickname(keyword: String): List<User>
}