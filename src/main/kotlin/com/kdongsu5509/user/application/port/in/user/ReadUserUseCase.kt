package com.kdongsu5509.user.application.port.`in`.user

import com.kdongsu5509.user.application.dto.UserInformation

interface ReadUserUseCase {
    fun searchPotentialFriendsUser(userEmail: String, keyword: String): List<UserInformation>
    fun searchMe(email: String): UserInformation
}