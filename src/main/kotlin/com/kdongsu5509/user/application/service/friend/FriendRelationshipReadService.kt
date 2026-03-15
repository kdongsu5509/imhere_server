package com.kdongsu5509.user.application.service.friend

import com.kdongsu5509.user.application.port.`in`.friend.ReadFriendsUseCase
import com.kdongsu5509.user.application.port.out.friend.FriendRelationshipLoadPort
import com.kdongsu5509.user.domain.friend.FriendRelationship
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class FriendRelationshipReadService(
    private val friendRelationshipLoadPort: FriendRelationshipLoadPort
) : ReadFriendsUseCase {

    override fun getMyFriends(email: String): List<FriendRelationship> {
        return friendRelationshipLoadPort.findFriendsRelationshipsByUserEmail(email)
    }
}