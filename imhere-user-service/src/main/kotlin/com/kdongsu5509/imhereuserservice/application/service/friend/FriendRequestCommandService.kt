package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.HandleFriendRequestUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRequestLoadPort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class FriendRequestCommandService(
    private val friendRequestLoadPort: FriendRequestLoadPort
) : HandleFriendRequestUseCase {
    override fun accept(requestId: Long) {
        TODO("Not yet implemented")
    }

    override fun reject(requestId: Long) {
        TODO("Not yet implemented")
    }
}