package com.kdongsu5509.user.application.port.out.friend

import com.kdongsu5509.user.domain.friend.FriendRelationship
import java.util.*

interface FriendRelationshipLoadPort {
    fun findFriendsRelationshipsByUserEmail(email: String): List<FriendRelationship>
    fun findFriendRelationshipByRelationshipId(id: UUID): FriendRelationship
}