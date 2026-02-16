package com.kdongsu5509.imhereuserservice.application.port.out.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRelationship
import java.util.*

interface FriendRelationshipUpdatePort {
    fun updateAlias(userEmail: String, friendRelationshipId: UUID, newFriendAlias: String): FriendRelationship
    fun delete(userEmail: String, friendRelationshipId: UUID)
}