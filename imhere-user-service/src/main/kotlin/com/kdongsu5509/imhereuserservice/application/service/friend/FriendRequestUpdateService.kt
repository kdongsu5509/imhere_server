package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.UpdateFriendRequestUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRelationshipSavePort
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRequestLoadPort
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRequestUpdatePort
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRestrictionSavePort
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRelationship
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRequest
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestriction
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestrictionType
import com.kdongsu5509.imhereuserservice.support.exception.BusinessException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class FriendRequestUpdateService(
    private val friendRequestLoadPort: FriendRequestLoadPort,
    private val friendRequestUpdatePort: FriendRequestUpdatePort,
    private val friendRelationshipSavePort: FriendRelationshipSavePort,
    private val friendRestrictionSavePort: FriendRestrictionSavePort
) : UpdateFriendRequestUseCase {

    override fun acceptFriendRequest(userEmail: String, friendRequestId: Long): FriendRelationship {
        val friendRequestQueryResult = verifyAcceptRequest(friendRequestId, userEmail)

        //2-a. 요청 정보를 로드
        //2-b. 요청을 바탕으로 `friend_relationships` 생성
        val acceptanceResult = friendRelationshipSavePort.save(
            requester = friendRequestQueryResult.requester,
            receiver = friendRequestQueryResult.receiver
        )

        //3. 요청 삭제.
        friendRequestUpdatePort.delete(friendRequestQueryResult.friendRequestId!!)

        return acceptanceResult
    }

    override fun rejectFriendRequest(
        userEmail: String,
        friendRequestId: Long
    ): FriendRestriction {
        val friendRequestQueryResult = verifyAcceptRequest(friendRequestId, userEmail)

        val rejectionResult = friendRestrictionSavePort.save(
            requester = friendRequestQueryResult.requester,
            receiver = friendRequestQueryResult.receiver,
            type = FriendRestrictionType.REJECT
        )

        friendRequestUpdatePort.delete(friendRequestQueryResult.friendRequestId!!)

        return rejectionResult
    }

    private fun verifyAcceptRequest(
        friendRequestId: Long, userEmail: String
    ): FriendRequest {
        val friendRequestQueryResult = friendRequestLoadPort.findReceivedRequestByRequestId(friendRequestId)

        if (friendRequestQueryResult.receiver.email != userEmail) {
            throw BusinessException(ErrorCode.FRIENDSHIP_REQUEST_RECEIVER_MISSMATCH)
        }

        return friendRequestQueryResult
    }
}