package com.kdongsu5509.user.application.port.out.friend

import com.kdongsu5509.user.domain.friend.FriendRequestUserInfo
import com.kdongsu5509.user.domain.friend.FriendRestriction
import com.kdongsu5509.user.domain.friend.FriendRestrictionType

interface FriendRestrictionSavePort {
    fun save(
        requester: FriendRequestUserInfo,
        receiver: FriendRequestUserInfo,
        type: FriendRestrictionType
    ): FriendRestriction
}