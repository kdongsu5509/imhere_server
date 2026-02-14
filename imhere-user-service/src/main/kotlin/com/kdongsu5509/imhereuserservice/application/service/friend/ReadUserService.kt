package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.dto.UserInformation
import com.kdongsu5509.imhereuserservice.application.port.`in`.user.ReadUserUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.user.UserLoadPort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class ReadUserService(private val userLoadPort: UserLoadPort) : ReadUserUseCase {
    override fun searchPotentialFriendsUser(userEmail: String, keyword: String): List<UserInformation> {
        return userLoadPort.findPotentialFriendsByEmailAndNickname(userEmail, keyword)
            .map { user ->
                UserInformation.convertToUserInformation(user)
            }
    }

    override fun searchMe(email: String): UserInformation {
        val me = userLoadPort.findActiveUserByEmailOrNull(email)
        return UserInformation.convertToUserInformation(me!!)
    }
}