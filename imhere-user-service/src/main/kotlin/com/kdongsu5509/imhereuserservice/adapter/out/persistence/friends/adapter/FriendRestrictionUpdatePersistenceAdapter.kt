package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.adapter

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.SpringDataFriendRestrictionRepository
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRestrictionUpdatePort
import org.springframework.stereotype.Component

@Component
class FriendRestrictionUpdatePersistenceAdapter(
    private val springDataFriendRestrictionRepository: SpringDataFriendRestrictionRepository,
) : FriendRestrictionUpdatePort {
    override fun delete(friendRestrictionId: Long) {
        springDataFriendRestrictionRepository.deleteById(friendRestrictionId)
    }
}