package com.kdongsu5509.imhereuserservice.application.port.`in`.user

import com.kdongsu5509.imhereuserservice.application.dto.UserInformation

interface ReadUserUseCase {
    fun searchUser(keyword: String): List<UserInformation>
    fun searchMe(email: String): UserInformation
}