package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.mapper

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.FriendRestrictionJpaEntity
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestriction
import org.springframework.stereotype.Component

@Component
class FriendRestrictionMapper {
    fun mapToDomainEntity(entity: FriendRestrictionJpaEntity): FriendRestriction {
        return FriendRestriction(
            friendRestrictionId = entity.id!!,
            actorEmail = entity.actor.email,
            targetEmail = entity.target.email,
            targetNickname = entity.target.nickname,
            restrictionType = entity.type,
            createdAt = entity.createdAt
        )
    }
}