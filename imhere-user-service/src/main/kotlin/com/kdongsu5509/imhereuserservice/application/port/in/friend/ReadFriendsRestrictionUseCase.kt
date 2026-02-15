package com.kdongsu5509.imhereuserservice.application.port.`in`.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestriction

interface ReadFriendsRestrictionUseCase {
    fun getRestrictedFriends(email: String): List<FriendRestriction>
}