package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.ReadFriendRequestUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRequestLoadPort
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class FriendRequestReadService(
    private val friendRequestLoadPort: FriendRequestLoadPort
) : ReadFriendRequestUseCase {
    override fun getReceivedAll(email: String): List<FriendRequest> {
        return friendRequestLoadPort.findReceivedRequestsAllByEmail(email)
    }

    override fun getReceivedDetail(requestId: Long): FriendRequest {
        return friendRequestLoadPort.findReceivedRequestByRequestId(requestId)
    }
}