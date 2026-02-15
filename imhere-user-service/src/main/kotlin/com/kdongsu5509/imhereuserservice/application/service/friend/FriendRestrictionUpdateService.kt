package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.UpdateFriendRestrictionUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRestrictionLoadPort
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRestrictionUpdatePort
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestriction
import com.kdongsu5509.imhereuserservice.support.exception.BusinessException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class FriendRestrictionUpdateService(
    private val friendRestrictionLoadPort: FriendRestrictionLoadPort,
    private val friendRestrictionUpdatePort: FriendRestrictionUpdatePort
) : UpdateFriendRestrictionUseCase {

    override fun deleteRestriction(
        userEmail: String, friendRestrictionId: Long
    ): FriendRestriction {
        val friendRestriction = getTargetFriendRestriction(friendRestrictionId, userEmail)

        friendRestrictionUpdatePort.delete(friendRestriction.friendRestrictionId)

        return friendRestriction
    }

    private fun getTargetFriendRestriction(friendRestrictionId: Long, userEmail: String): FriendRestriction {
        val friendRestriction = friendRestrictionLoadPort.loadById(friendRestrictionId)

        if (friendRestriction.actorEmail != userEmail) {
            throw BusinessException(ErrorCode.FRIEND_RESTRICTION_ACTOR_MISS_MATCH)
        }
        return friendRestriction
    }
}