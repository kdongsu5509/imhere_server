package com.kdongsu5509.user.application.port.out.friend

import com.kdongsu5509.user.domain.friend.FriendRelationship
import com.kdongsu5509.user.domain.friend.FriendRequestUserInfo

interface FriendRelationshipSavePort {
    fun save(requester: FriendRequestUserInfo, receiver: FriendRequestUserInfo): FriendRelationship
}