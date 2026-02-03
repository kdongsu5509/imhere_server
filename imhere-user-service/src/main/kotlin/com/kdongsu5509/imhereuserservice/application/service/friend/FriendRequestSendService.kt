package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.SendFriendRequestUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRequestSavePort
import com.kdongsu5509.imhereuserservice.domain.friend.FriendshipStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class FriendRequestSendService(
    private val friendRequestSavePort: FriendRequestSavePort
) : SendFriendRequestUseCase {

    companion object {
        val ON_PENDING_STATUS = FriendshipStatus.PENDING
    }

    override fun request(myEmail: String, targetEmail: String) {
        friendRequestSavePort.createNewFriendship(
            myEmail,
            targetEmail,
            ON_PENDING_STATUS
        )
    }
}