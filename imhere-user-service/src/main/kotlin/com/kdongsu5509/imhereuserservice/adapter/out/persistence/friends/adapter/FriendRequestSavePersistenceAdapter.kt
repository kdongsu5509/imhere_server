package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.adapter

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.FriendRequestJpaEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.SpringDataFriendRequestRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.mapper.FriendRequestMapper
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.SpringQueryDSLUserRepository
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRequestSavePort
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRequest
import com.kdongsu5509.imhereuserservice.support.exception.BusinessException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode
import org.springframework.stereotype.Component
import java.util.*

@Component
class FriendRequestSavePersistenceAdapter(
    private val friendRequestMapper: FriendRequestMapper,
    private val userRepository: SpringQueryDSLUserRepository,
    private val springDataFriendRequestRepository: SpringDataFriendRequestRepository,
) : FriendRequestSavePort {
    override fun createFriendshipRequest(myEmail: String, receiverId: UUID, message: String): FriendRequest {
        val meEntity = userRepository.findActiveUserByEmail(myEmail).orElseThrow {
            BusinessException(ErrorCode.USER_NOT_FOUND)
        }
        val receiver = userRepository.findActiveUserByID(receiverId).orElseThrow {
            BusinessException(ErrorCode.USER_NOT_FOUND)
        }

        val newFriendRequest = FriendRequestJpaEntity(
            requester = meEntity,
            receiver = receiver,
            message = message
        )

        val commandResult = springDataFriendRequestRepository.save(newFriendRequest)
        return friendRequestMapper.mapToDomainEntity(commandResult)
    }
}