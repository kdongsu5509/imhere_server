package com.kdongsu5509.user.application.port.`in`.friend

import com.kdongsu5509.user.domain.friend.FriendRequest

interface ReadFriendRequestUseCase {
    fun getReceivedAll(email: String): List<FriendRequest>
    fun getReceivedDetail(requestId: Long): FriendRequest
}