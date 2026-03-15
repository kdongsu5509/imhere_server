package com.kdongsu5509.user.application.port.out.friend

import com.kdongsu5509.user.domain.friend.FriendRestriction

interface FriendRestrictionLoadPort {
    fun loadAll(email: String): List<FriendRestriction>
    fun loadById(friendRestrictionId: Long): FriendRestriction
}