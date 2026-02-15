package com.kdongsu5509.imhereuserservice.application.port.`in`.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRelationship

interface ReadFriendsUseCase {
    fun getMyFriends(email: String): List<FriendRelationship>
}