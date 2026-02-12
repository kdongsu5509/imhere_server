package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends

import com.kdongsu5509.imhereuserservice.adapter.`in`.web.common.APIResponse
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends.dto.ReceivedFriendRequestResponse
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends.dto.ReceivedFriendRequestResponseDetail
import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.ReadFriendRequestUseCase
import jakarta.validation.constraints.NotNull
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/user/friends/request")
class FriendsRequestReadController(
    private val readFriendRequestUseCase: ReadFriendRequestUseCase
) {
    @GetMapping
    fun getReceivedRequestAll(
        @AuthenticationPrincipal user: UserDetails,
    ): APIResponse<List<ReceivedFriendRequestResponse>> {
        val receivedRequest = readFriendRequestUseCase.getReceivedAll(user.username)
        val response = receivedRequest.map { result ->
            ReceivedFriendRequestResponse(
                result.friendRequestId!!,
                result.requester.email,
                result.requester.nickname
            )
        }
        return APIResponse.success(response)
    }

    @GetMapping("/{requestId}")
    fun getReceivedRequestDetail(
        @Validated
        @NotNull
        @PathVariable
        requestId: UUID
    ): APIResponse<ReceivedFriendRequestResponseDetail> {
        val result = readFriendRequestUseCase.getReceivedDetail(requestId)
        return APIResponse.success(
            ReceivedFriendRequestResponseDetail(
                result.friendRequestId!!,
                result.requester.email,
                result.requester.nickname,
                result.message!!
            )
        )
    }
}