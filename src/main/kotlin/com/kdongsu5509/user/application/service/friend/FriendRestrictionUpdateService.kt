package com.kdongsu5509.user.application.service.friend

import com.kdongsu5509.user.application.port.`in`.friend.UpdateFriendRestrictionUseCase
import com.kdongsu5509.user.application.port.out.friend.FriendRestrictionLoadPort
import com.kdongsu5509.user.application.port.out.friend.FriendRestrictionUpdatePort
import com.kdongsu5509.user.domain.friend.FriendRestriction
import com.kdongsu5509.user.support.exception.BusinessException
import com.kdongsu5509.user.support.exception.ErrorCode
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