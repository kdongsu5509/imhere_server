package com.kdongsu5509.imhereuserservice.application.port.`in`

import com.kdongsu5509.imhereuserservice.application.dto.UserInformation

interface UserSearchUseCase {
    fun searchUser(keyword: String): List<UserInformation>
    fun searchMe(email: String): UserInformation
}