package com.kdongsu5509.user.application.port.`in`.friend

import com.kdongsu5509.user.domain.friend.FriendRestriction

interface ReadFriendsRestrictionUseCase {
    fun getRestrictedFriends(email: String): List<FriendRestriction>
}