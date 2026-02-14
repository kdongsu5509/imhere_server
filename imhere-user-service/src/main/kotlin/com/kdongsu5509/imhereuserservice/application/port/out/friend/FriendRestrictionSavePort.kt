package com.kdongsu5509.imhereuserservice.application.port.out.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRequestUserInfo
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestriction
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestrictionType

interface FriendRestrictionSavePort {
    fun save(
        requester: FriendRequestUserInfo,
        receiver: FriendRequestUserInfo,
        type: FriendRestrictionType
    ): FriendRestriction
}