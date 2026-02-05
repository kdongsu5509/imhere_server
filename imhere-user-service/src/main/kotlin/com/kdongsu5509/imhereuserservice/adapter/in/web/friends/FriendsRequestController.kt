package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends

import com.kdongsu5509.imhereuserservice.adapter.`in`.web.common.APIResponse
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends.dto.FriendsRequest
import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.SendFriendRequestUseCase
import jakarta.validation.constraints.NotBlank
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/friends")
class FriendsRequestController(
    private val sendFriendRequestUseCase: SendFriendRequestUseCase
) {
    @PostMapping("/requests")
    fun requestFriendship(
        @AuthenticationPrincipal user: UserDetails,
        @Validated @RequestBody friendsRequest: FriendsRequest
    ): APIResponse<Unit> {

        sendFriendRequestUseCase.request(
            user.username,
            friendsRequest.targetEmail
        )

        return APIResponse.success()
    }

    @PostMapping("/accept/{id}")
    fun acceptToRequest(
        @PathVariable @NotBlank(message = "요청 id 는 필수입니다")
        id: String
    ) {
        //TODO
    }

    @PostMapping("/reject/{id}")
    fun rejectToRequest(
        @PathVariable @NotBlank(message = "요청 id 는 필수입니다")
        id: String
    ) {
        //TODO
    }
    //친구 목록 조회	GET	    /api/v1/user/friends
    //친구 삭제	    DELETE	/api/v1/user/friends/{friendId}
}