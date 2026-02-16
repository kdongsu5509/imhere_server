package com.kdongsu5509.imhereuserservice.application.port.`in`.friend

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRelationship
import java.util.*

interface UpdateFriendsUseCase {
    fun changeFriendAlias(userEmail: String, friendRelationshipId: UUID, newFriendAlias: String): FriendRelationship
    fun block(userEmail: String, friendRelationshipId: UUID)
    fun deleteRelationship(userEmail: String, friendRelationshipId: UUID)
}