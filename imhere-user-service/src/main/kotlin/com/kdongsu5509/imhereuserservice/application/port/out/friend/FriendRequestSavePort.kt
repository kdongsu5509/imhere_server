package com.kdongsu5509.imhereuserservice.application.port.out.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRequest
import java.util.*

interface FriendRequestSavePort {
    fun save(requesterEmail: String, receiverId: UUID, message: String): FriendRequest
}