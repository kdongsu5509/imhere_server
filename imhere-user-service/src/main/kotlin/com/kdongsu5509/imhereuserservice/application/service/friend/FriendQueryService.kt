package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.QueryMyFriendUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendLoadPort
import com.kdongsu5509.imhereuserservice.domain.Friend
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class FriendQueryService(private val friendLoadPort: FriendLoadPort) : QueryMyFriendUseCase {

    override fun queryMyFriends(email: String): List<Friend> {
        return friendLoadPort.findMyFriends(email)
    }
    
}