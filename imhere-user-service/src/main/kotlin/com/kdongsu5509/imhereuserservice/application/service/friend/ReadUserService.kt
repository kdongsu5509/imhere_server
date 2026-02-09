package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.dto.UserInformation
import com.kdongsu5509.imhereuserservice.application.port.`in`.user.ReadUserUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.user.UserLoadPort
import com.kdongsu5509.imhereuserservice.support.exception.BusinessException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class ReadUserService(private val userLoadPort: UserLoadPort) : ReadUserUseCase {
    override fun searchUser(keyword: String): List<UserInformation> {
        val matchedUsers = userLoadPort.findByEmailAndNickname(keyword)
        return matchedUsers.map { user ->
            UserInformation.convertToUserInformation(user)
        }
    }

    override fun searchMe(email: String): UserInformation {
        val me = userLoadPort.findActiveUserByEmailOrNull(email) ?: let {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }

        return UserInformation.convertToUserInformation(me)
    }
}