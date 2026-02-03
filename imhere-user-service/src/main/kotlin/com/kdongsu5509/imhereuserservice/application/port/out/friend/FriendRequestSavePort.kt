package com.kdongsu5509.imhereuserservice.application.port.out.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendshipStatus

interface FriendRequestSavePort {
    fun createNewFriendship(requesterEmail: String, receiverEmail: String, friendshipStatus: FriendshipStatus)
}