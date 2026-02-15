package com.kdongsu5509.imhereuserservice.application.port.out.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestriction

interface FriendRestrictionLoadPort {
    fun loadAll(email: String): List<FriendRestriction>
    fun loadById(friendRestrictionId: Long): FriendRestriction
}