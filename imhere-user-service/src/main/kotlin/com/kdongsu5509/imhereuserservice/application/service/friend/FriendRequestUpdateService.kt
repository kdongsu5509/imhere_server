package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.UpdateFriendRequestUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRequestLoadPort
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRequestSavePort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
@Transactional
class FriendRequestUpdateService(
    private val friendRequestLoadPort: FriendRequestLoadPort,
    private val friendRequestSavePort: FriendRequestSavePort
) : UpdateFriendRequestUseCase {

    override fun acceptRequest(username: String, requestId: UUID) {
        //1. 검증.
        friendRequestLoadPort.find
        TODO("Not yet implemented")
    }
}