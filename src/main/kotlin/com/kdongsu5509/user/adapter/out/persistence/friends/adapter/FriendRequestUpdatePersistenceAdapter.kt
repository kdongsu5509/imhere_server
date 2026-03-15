package com.kdongsu5509.user.adapter.out.persistence.friends.adapter

import com.kdongsu5509.user.adapter.out.persistence.friends.jpa.SpringDataFriendRequestRepository
import com.kdongsu5509.user.application.port.out.friend.FriendRequestUpdatePort
import org.springframework.stereotype.Component

@Component
class FriendRequestUpdatePersistenceAdapter(
    private val springDataFriendRequestRepository: SpringDataFriendRequestRepository,
) : FriendRequestUpdatePort {
    override fun delete(friendRequestId: Long) {
        springDataFriendRequestRepository.deleteById(friendRequestId)
    }
}