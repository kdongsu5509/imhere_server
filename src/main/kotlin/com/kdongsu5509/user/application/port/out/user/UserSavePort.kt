package com.kdongsu5509.user.application.port.out.user

import com.kdongsu5509.user.domain.user.User

interface UserSavePort {
    fun save(user: User): User
}