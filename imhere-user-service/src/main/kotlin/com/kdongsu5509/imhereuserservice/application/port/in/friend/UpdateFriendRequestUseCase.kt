package com.kdongsu5509.imhereuserservice.application.port.`in`.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRelationship
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestriction

interface UpdateFriendRequestUseCase {
    fun acceptFriendRequest(userEmail: String, friendRequestId: Long): FriendRelationship
    fun rejectFriendRequest(userEmail: String, friendRequestId: Long): FriendRestriction
}