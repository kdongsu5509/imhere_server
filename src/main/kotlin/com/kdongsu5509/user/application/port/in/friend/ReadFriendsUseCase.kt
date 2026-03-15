package com.kdongsu5509.user.application.port.`in`.friend

import com.kdongsu5509.user.domain.friend.FriendRelationship

interface ReadFriendsUseCase {
    fun getMyFriends(email: String): List<FriendRelationship>
}