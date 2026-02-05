package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.adapter

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.FriendshipJpaEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.SpringQueryDSLFriendRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.mapper.FriendMapper
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendLoadPort
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRequestLoadPort
import com.kdongsu5509.imhereuserservice.domain.friend.Friend
import org.springframework.stereotype.Component

@Component
class FriendQueryPersistenceAdapter(
    private val friendMapper: FriendMapper,
    private val springQueryDSLFriendRepository: SpringQueryDSLFriendRepository
) : FriendLoadPort, FriendRequestLoadPort {

    override fun findMyFriends(email: String): List<Friend> {
        return convertToProperReturnType(
            springQueryDSLFriendRepository.findAcceptedFriends(email),
            email
        )
    }

    override fun findMyRequest(email: String): List<Friend> {
        return convertToProperReturnType(
            springQueryDSLFriendRepository.findSentRequests(email),
            email
        )
    }

    override fun findReceived(email: String): List<Friend> {
        return convertToProperReturnType(
            springQueryDSLFriendRepository.findReceivedRequests(email),
            email
        )
    }

    private fun convertToProperReturnType(entities: List<FriendshipJpaEntity>, currentUserEmail: String): List<Friend> {
        if (entities.size == 0) {
            return listOf()
        }
        return entities.map { entity ->
            friendMapper.mapToDomainEntity(entity, currentUserEmail)
        }
    }
}