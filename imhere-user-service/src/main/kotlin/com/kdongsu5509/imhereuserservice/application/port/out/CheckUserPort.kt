package com.kdongsu5509.imhereuserservice.application.port.out

interface CheckUserPort {
    fun existsByEmail(email: String): Boolean
}