package com.kdongsu5509.user.application.port.`in`.user

import com.kdongsu5509.user.application.dto.UserInformation

interface UpdateUserUseCase {
    fun changeNickName(userEmail: String, newNickname: String): UserInformation
}