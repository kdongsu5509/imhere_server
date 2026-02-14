package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.mapper

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.FriendRelationshipsJpaEntity
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRelationship
import org.springframework.stereotype.Component

@Component
class FriendRelationshipMapper {
    fun mapToDomainEntity(entity: FriendRelationshipsJpaEntity): FriendRelationship {
        return FriendRelationship(
            friendRelationshipId = entity.id!!,
            friendEmail = entity.friendUser.email,
            friendAlias = entity.friendAlias,
            createdAt = entity.createdAt
        )
    }
}