package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.adapter

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.FriendRestrictionJpaEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.SpringDataFriendRestrictionRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.mapper.FriendRestrictionMapper
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.SpringQueryDSLUserRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.UserJpaEntity
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRestrictionSavePort
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRequestUserInfo
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestriction
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestrictionType
import com.kdongsu5509.imhereuserservice.support.exception.BusinessException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode
import org.springframework.stereotype.Component

@Component
class FriendRestrictionSavePersistenceAdapter(
    private val friendRestrictionMapper: FriendRestrictionMapper,
    private val userRepository: SpringQueryDSLUserRepository,
    private val springDataFriendRestrictionRepository: SpringDataFriendRestrictionRepository,
) : FriendRestrictionSavePort {

    override fun save(
        requester: FriendRequestUserInfo,
        receiver: FriendRequestUserInfo,
        type: FriendRestrictionType
    ): FriendRestriction {
        val newFriendRestrictionEntity = createNewFriendRelationshipEntities(requester.email, receiver.email)
        val result = springDataFriendRestrictionRepository.save(
            newFriendRestrictionEntity
        )

        return friendRestrictionMapper.mapToDomainEntity(result)
    }

    private fun createNewFriendRelationshipEntities(
        requesterEmail: String,
        receiverEmail: String,
    ): FriendRestrictionJpaEntity {
        val users = fetchRequiredUsers(requesterEmail, receiverEmail)

        val requester = users.first { it.email == requesterEmail }
        val receiver = users.first { it.email == receiverEmail }

        return FriendRestrictionJpaEntity.createFromRejection(receiver, requester)
    }

    private fun fetchRequiredUsers(vararg emails: String): List<UserJpaEntity> {
        val users = userRepository.findActiveUsersByEmails(emails[0], emails[1])
        if (users.size != emails.size) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
        return users
    }
}