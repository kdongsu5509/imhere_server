package com.kdongsu5509.user.application.port.out.friend

import com.kdongsu5509.user.domain.friend.FriendRequest

interface FriendRequestLoadPort {
    fun findReceivedRequestsAllByEmail(email: String): List<FriendRequest>
    fun findReceivedRequestByRequestId(requestId: Long): FriendRequest
}