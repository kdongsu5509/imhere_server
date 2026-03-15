package com.kdongsu5509.user.application.port.out.user

import com.kdongsu5509.user.domain.user.User

interface UserLoadPort {
    fun findUserByEmailOrNull(email: String): User?
    fun findActiveUserByEmailOrNull(email: String): User?
    fun findPotentialFriendsByEmailAndNickname(userEmail: String, keyword: String): List<User>
}