package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.UpdateFriendRequestUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRequestLoadPort
import com.kdongsu5509.imhereuserservice.support.exception.BusinessException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class FriendRequestUpdateService(
    private val friendRequestLoadPort: FriendRequestLoadPort,
) : UpdateFriendRequestUseCase {

    override fun acceptRequest(userEmail: String, requestId: Long) {
        //1. 검증.
        //1-a. id와 일치하는 것이 없으면 repository에서 오류 발생.
        val request = friendRequestLoadPort.findReceivedRequestByRequestId(requestId)
        //1-b. 내가 받은 것이 맞는 지 다시 확인
        if (request.receiver.email != userEmail) {
            throw BusinessException(ErrorCode.FRIENDSHIP_REQUEST_RECEIVER_MISSMATCH)
        }

        //2. 사용자 정보 업데이트
        //2-a. 요청 정보를 로드
        //2-b. 요청을 바탕으로 `friendship` 생성


    }
}