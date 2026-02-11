package com.kdongsu5509.imhereuserservice.application.port.out.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRequest
import java.util.*

interface FriendRequestLoadPort {
    fun findReceivedAll(email: String): List<FriendRequest>
    fun findReceived(requestId: UUID): FriendRequest
}