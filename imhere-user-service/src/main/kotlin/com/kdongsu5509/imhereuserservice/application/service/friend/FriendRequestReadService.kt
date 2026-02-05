package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.ReadFriendRequestUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRequestLoadPort
import com.kdongsu5509.imhereuserservice.domain.friend.Friend
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class FriendRequestReadService(private val friendRequestLoadPort: FriendRequestLoadPort) : ReadFriendRequestUseCase {
    override fun queryMySent(email: String): List<Friend> {
        TODO("Not yet implemented")
    }

    override fun queryReceived(email: String): List<Friend> {
        TODO("Not yet implemented")
    }
}