package com.kdongsu5509.imhereuserservice.application.port.`in`.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestriction

interface UpdateFriendRestrictionUseCase {
    fun deleteRestriction(userEmail: String, friendRestrictionId: Long): FriendRestriction
}