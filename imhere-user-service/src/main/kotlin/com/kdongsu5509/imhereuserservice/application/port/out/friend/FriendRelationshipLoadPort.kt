package com.kdongsu5509.imhereuserservice.application.port.out.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRelationship

interface FriendRelationshipLoadPort {
    fun findFriendsByUserEmail(email: String): List<FriendRelationship>
}