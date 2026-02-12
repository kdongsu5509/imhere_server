package com.kdongsu5509.imhereuserservice.application.port.out.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRequest
import java.util.*

interface FriendRequestLoadPort {
    fun findReceivedRequestsAllByEmail(email: String): List<FriendRequest>
    fun findReceivedRequestByRequestId(requestId: UUID): FriendRequest
}