package com.kdongsu5509.imhereuserservice.application.port.out

import com.kdongsu5509.imhereuserservice.domain.auth.User

interface SaveUserPort {
    fun save(user: User)
}