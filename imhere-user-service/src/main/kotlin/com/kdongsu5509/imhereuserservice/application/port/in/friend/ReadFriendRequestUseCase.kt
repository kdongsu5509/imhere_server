package com.kdongsu5509.imhereuserservice.application.port.`in`.friend

import com.kdongsu5509.imhereuserservice.domain.friend.Friend

interface ReadFriendRequestUseCase {
    fun queryReceived(email: String): List<Friend>
}