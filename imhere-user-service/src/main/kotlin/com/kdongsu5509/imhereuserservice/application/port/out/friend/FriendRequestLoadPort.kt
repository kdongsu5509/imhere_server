package com.kdongsu5509.imhereuserservice.application.port.out.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRequest

interface FriendRequestLoadPort {
    fun findReceived(email: String): List<FriendRequest>
}