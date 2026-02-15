package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.adapter

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.SpringDataFriendRestrictionRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.mapper.FriendRestrictionMapper
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.SpringQueryDSLUserRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.UserJpaEntity
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRestrictionLoadPort
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestriction
import com.kdongsu5509.imhereuserservice.support.exception.BusinessException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode
import org.springframework.stereotype.Component

@Component
class FriendRestrictionLoadPersistenceAdapter(
    private val friendRestrictionMapper: FriendRestrictionMapper,
    private val userRepository: SpringQueryDSLUserRepository,
    private val springDataFriendRestrictionRepository: SpringDataFriendRestrictionRepository,
) : FriendRestrictionLoadPort {

    override fun loadAll(email: String): List<FriendRestriction> {
        fetchRequiredUser(email)
        val queryResult = springDataFriendRestrictionRepository.findByActorId(
            fetchRequiredUser(email).id!!
        )

        return queryResult.map { result ->
            friendRestrictionMapper.mapToDomainEntity(result)
        }
    }

    private fun fetchRequiredUser(email: String): UserJpaEntity {
        return userRepository.findActiveUserByEmail(email).orElseThrow {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
    }
}