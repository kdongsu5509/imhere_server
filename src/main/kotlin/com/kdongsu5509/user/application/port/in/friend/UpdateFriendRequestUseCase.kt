package com.kdongsu5509.user.application.port.`in`.friend

import com.kdongsu5509.user.domain.friend.FriendRelationship
import com.kdongsu5509.user.domain.friend.FriendRestriction

interface UpdateFriendRequestUseCase {
    fun acceptFriendRequest(userEmail: String, friendRequestId: Long): FriendRelationship
    fun rejectFriendRequest(userEmail: String, friendRequestId: Long): FriendRestriction
}