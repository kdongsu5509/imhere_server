package com.kdongsu5509.imhereuserservice.application.port.out.user

interface CheckUserPort {
    fun existsByEmail(email: String): Boolean
}