package com.kdongsu5509.imhereuserservice.application.port.out.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRelationship
import java.util.*

interface FriendRelationshipLoadPort {
    fun findFriendsRelationshipsByUserEmail(email: String): List<FriendRelationship>
    fun findFriendRelationshipByRelationshipId(id: UUID): FriendRelationship
}