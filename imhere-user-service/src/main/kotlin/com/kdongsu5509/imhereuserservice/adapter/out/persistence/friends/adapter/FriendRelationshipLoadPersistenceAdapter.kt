package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.adapter

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.SpringDataFriendRelationshipsRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.mapper.FriendRelationshipMapper
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.SpringQueryDSLUserRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.UserJpaEntity
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRelationshipLoadPort
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRelationship
import com.kdongsu5509.imhereuserservice.support.exception.BusinessException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode
import org.springframework.stereotype.Component

@Component
class FriendRelationshipLoadPersistenceAdapter(
    private val friendRelationshipMapper: FriendRelationshipMapper,
    private val userRepository: SpringQueryDSLUserRepository,
    private val springDataFriendRelationshipsRepository: SpringDataFriendRelationshipsRepository,
) : FriendRelationshipLoadPort {
    override fun findFriendsByUserEmail(email: String): List<FriendRelationship> {
        val userEntity = findUserOrThrow(email)
        val relationshipEntities = springDataFriendRelationshipsRepository.findByOwnerUserId(userEntity.id!!)
        return relationshipEntities.map { friendRelationshipMapper.mapToDomainEntity(it) }
    }

    private fun findUserOrThrow(email: String): UserJpaEntity {
        return userRepository.findUserByEmail(email).orElseThrow {
            BusinessException(ErrorCode.USER_NOT_FOUND)
        }
    }
}