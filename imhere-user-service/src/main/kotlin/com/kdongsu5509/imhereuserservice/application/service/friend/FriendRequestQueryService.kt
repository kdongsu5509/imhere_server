package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.QueryFriendRequestUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRequestLoadPort
import com.kdongsu5509.imhereuserservice.domain.Friend
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class FriendRequestQueryService(private val friendRequestLoadPort: FriendRequestLoadPort) : QueryFriendRequestUseCase {
    override fun queryMySent(email: String): List<Friend> {
        TODO("Not yet implemented")
    }

    override fun queryReceived(email: String): List<Friend> {
        TODO("Not yet implemented")
    }
}