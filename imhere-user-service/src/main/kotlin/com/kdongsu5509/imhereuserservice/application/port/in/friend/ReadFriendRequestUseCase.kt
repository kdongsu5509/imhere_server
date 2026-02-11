package com.kdongsu5509.imhereuserservice.application.port.`in`.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRequest
import java.util.*

interface ReadFriendRequestUseCase {
    fun getReceivedAll(email: String): List<FriendRequest>
    fun getReceivedDetail(requestId: UUID): FriendRequest
}