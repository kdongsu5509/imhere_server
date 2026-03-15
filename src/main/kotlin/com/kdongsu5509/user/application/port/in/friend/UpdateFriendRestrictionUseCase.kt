package com.kdongsu5509.user.application.port.`in`.friend

import com.kdongsu5509.user.domain.friend.FriendRestriction

interface UpdateFriendRestrictionUseCase {
    fun deleteRestriction(userEmail: String, friendRestrictionId: Long): FriendRestriction
}