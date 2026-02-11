package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.adapter

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.SpringDataFriendRequestRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.mapper.FriendRequestMapper
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRequestLoadPort
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRequest
import com.kdongsu5509.imhereuserservice.support.exception.BusinessException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode
import org.springframework.stereotype.Component
import java.util.*

@Component
class FriendRequestLoadPersistenceAdapter(
    private val friendRequestMapper: FriendRequestMapper,
    private val springDataFriendRequestRepository: SpringDataFriendRequestRepository,
) : FriendRequestLoadPort {
    override fun findReceivedAll(email: String): List<FriendRequest> {
        val queryResults = springDataFriendRequestRepository.findByReceiverEmail(email)

        if (queryResults.isEmpty()) return listOf()

        return queryResults.map { entity ->
            friendRequestMapper.mapToDomainEntity(entity)
        }
    }

    override fun findReceived(requestId: UUID): FriendRequest {
        val queryResult = springDataFriendRequestRepository.findById(requestId).orElseThrow {
            throw BusinessException(ErrorCode.FRIENDSHIP_REQUEST_NOT_FOUND)
        }

        return friendRequestMapper.mapToDomainEntity(queryResult)
    }
}