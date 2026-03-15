package com.kdongsu5509.user.adapter.out.persistence.friends.adapter

import com.kdongsu5509.user.adapter.out.persistence.friends.jpa.SpringDataFriendRestrictionRepository
import com.kdongsu5509.user.application.port.out.friend.FriendRestrictionUpdatePort
import org.springframework.stereotype.Component

@Component
class FriendRestrictionUpdatePersistenceAdapter(
    private val springDataFriendRestrictionRepository: SpringDataFriendRestrictionRepository,
) : FriendRestrictionUpdatePort {
    override fun delete(friendRestrictionId: Long) {
        springDataFriendRestrictionRepository.deleteById(friendRestrictionId)
    }
}