package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.adapter

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.SpringDataFriendRequestRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.mapper.FriendRequestMapper
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.SpringQueryDSLUserRepository
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRequestLoadPort
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRequest
import org.springframework.stereotype.Component

@Component
class FriendRequestLoadPersistenceAdapter(
    private val userRepository: SpringQueryDSLUserRepository,
    private val friendRequestMapper: FriendRequestMapper,
    private val springDataFriendRequestRepository: SpringDataFriendRequestRepository,
) : FriendRequestLoadPort {
//    override fun createFriendshipRequest(myEmail: String, receiverId: UUID, message: String): FriendRequest {
//        val meEntity = userRepository.findActiveUserByEmail(myEmail).orElseThrow {
//            throw BusinessException(ErrorCode.USER_NOT_FOUND)
//        }
//        val receiver = findNotNullUserById(receiverId)
//
//        val newFriendRequest = FriendRequestJpaEntity(
//            requester = meEntity,
//            receiver = receiver,
//            message = message
//        )
//
//        val commandResult = springDataFriendRequestRepository.save(newFriendRequest)
//        return friendRequestMapper.mapToDomainEntity(commandResult)
//    }
//
//    private fun findNotNullUserById(receiverId: UUID): UserJpaEntity {
//        val receiver = springDataUserRepository.findById(receiverId)
//        if (receiver.isEmpty) {
//            throw BusinessException(ErrorCode.USER_NOT_FOUND)
//        }
//        return receiver.get()
//    }

    override fun findReceived(email: String): List<FriendRequest> {
        TODO("Not yet implemented")
    }
}