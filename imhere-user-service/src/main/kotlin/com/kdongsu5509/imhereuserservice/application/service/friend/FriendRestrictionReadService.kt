package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.ReadFriendsRestrictionUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRestrictionLoadPort
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestriction
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class FriendRestrictionReadService(
    private val friendRestrictionLoadPort: FriendRestrictionLoadPort
) : ReadFriendsRestrictionUseCase {
    override fun getRestrictedFriends(email: String): List<FriendRestriction> {
        return friendRestrictionLoadPort.loadAll(email)
    }
}