package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.adapter

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.FriendRequestJpaEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.SpringDataFriendRequestRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.mapper.FriendRequestMapper
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.SpringQueryDSLUserRepository
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRequestSavePort
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRequest
import org.springframework.stereotype.Component
import java.util.*

@Component
class FriendRequestSavePersistenceAdapter(
    private val friendRequestMapper: FriendRequestMapper,
    private val userRepository: SpringQueryDSLUserRepository,
    private val springDataFriendRequestRepository: SpringDataFriendRequestRepository,
) : FriendRequestSavePort {
    override fun save(requesterEmail: String, receiverId: UUID, message: String): FriendRequest {
        val queryResult = userRepository.findActiveUsersByEmailAndId(requesterEmail, receiverId)

        val requester = queryResult.first { it.email == requesterEmail }
        val receiver = queryResult.first { it.id == receiverId }

        val newFriendRequest = FriendRequestJpaEntity(
            requester = requester,
            receiver = receiver,
            message = message
        )

        val commandResult = springDataFriendRequestRepository.save(newFriendRequest)
        return friendRequestMapper.mapToDomainEntity(commandResult)
    }
}