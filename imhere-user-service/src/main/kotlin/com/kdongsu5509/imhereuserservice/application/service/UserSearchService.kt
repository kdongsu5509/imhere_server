package com.kdongsu5509.imhereuserservice.application.service

import com.kdongsu5509.imhereuserservice.application.port.`in`.UserSearchUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.LoadUserPort
import org.springframework.stereotype.Component

@Component
class UserSearchService(private val loadUserPort: LoadUserPort) : UserSearchUseCase {
    override fun searchUser(keyword: String) {
        loadUserPort.findByEmailAndNickname()
        TODO("Not yet implemented")
        return
    }
}