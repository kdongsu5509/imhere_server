package com.kdongsu5509.user.application.port.out.friend

import com.kdongsu5509.user.domain.friend.FriendRelationship
import java.util.*

interface FriendRelationshipUpdatePort {
    fun updateAlias(userEmail: String, friendRelationshipId: UUID, newFriendAlias: String): FriendRelationship
    fun delete(userEmail: String, friendRelationshipId: UUID)
}