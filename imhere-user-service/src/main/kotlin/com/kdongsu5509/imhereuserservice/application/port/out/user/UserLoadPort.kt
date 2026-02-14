package com.kdongsu5509.imhereuserservice.application.port.out.user

import com.kdongsu5509.imhereuserservice.domain.user.User

interface UserLoadPort {
    fun findUserByEmailOrNull(email: String): User?
    fun findActiveUserByEmailOrNull(email: String): User?
    fun findPotentialFriendsByEmailAndNickname(userEmail: String, keyword: String): List<User>
}