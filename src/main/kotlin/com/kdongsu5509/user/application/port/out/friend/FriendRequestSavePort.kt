package com.kdongsu5509.user.application.port.out.friend

import com.kdongsu5509.user.domain.friend.FriendRequest
import java.util.*

interface FriendRequestSavePort {
    fun save(requesterEmail: String, receiverId: UUID, message: String): FriendRequest
}