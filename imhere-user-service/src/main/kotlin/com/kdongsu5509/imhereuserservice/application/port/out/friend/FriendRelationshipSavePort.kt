package com.kdongsu5509.imhereuserservice.application.port.out.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRelationship
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRequestUserInfo

interface FriendRelationshipSavePort {
    fun save(requester: FriendRequestUserInfo, receiver: FriendRequestUserInfo): FriendRelationship
}