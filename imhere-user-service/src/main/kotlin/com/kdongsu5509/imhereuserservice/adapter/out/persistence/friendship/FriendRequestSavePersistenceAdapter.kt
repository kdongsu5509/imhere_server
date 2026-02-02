package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friendship

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.FriendshipJpaEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.SpringDataUserRepository
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRequestSavePort
import com.kdongsu5509.imhereuserservice.domain.FriendshipStatus
import org.springframework.stereotype.Component

@Component
class FriendRequestSavePersistenceAdapter(
    private val springDataFriendshipRepository: SpringDataFriendshipRepository,
    private val springDataUserRepository: SpringDataUserRepository
) : FriendRequestSavePort {
    override fun createNewFriendship(
        requesterEmail: String,
        receiverEmail: String,
        friendshipStatus: FriendshipStatus
    ) {
        val requester = springDataUserRepository.findByEmail(requesterEmail)
            ?: throw IllegalArgumentException("Requester not found: $requesterEmail")
        val receiver = springDataUserRepository.findByEmail(receiverEmail)
            ?: throw IllegalArgumentException("Receiver not found: $receiverEmail")

        springDataFriendshipRepository.save(
            FriendshipJpaEntity(requester, receiver, friendshipStatus)
        )
    }
}