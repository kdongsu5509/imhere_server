package com.kdongsu5509.imhereuserservice.application.service

import com.kdongsu5509.imhereuserservice.application.dto.UserInformation
import com.kdongsu5509.imhereuserservice.application.port.`in`.user.UserSearchUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.LoadUserPort
import org.springframework.stereotype.Component

@Component
class UserSearchService(private val loadUserPort: LoadUserPort) : UserSearchUseCase {
    override fun searchUser(keyword: String): List<UserInformation> {
        val matchedUsers = loadUserPort.findByEmailAndNickname(keyword)
        return matchedUsers.map { user -> UserInformation(user.email, user.nickname) }
    }

    override fun searchMe(email: String): UserInformation {
        val me = loadUserPort.findByEmail(email)
        return UserInformation(me.email, me.nickname)
    }
}