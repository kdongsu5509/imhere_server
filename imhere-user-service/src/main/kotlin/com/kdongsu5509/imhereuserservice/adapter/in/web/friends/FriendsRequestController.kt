package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends

import com.kdongsu5509.imhereuserservice.adapter.`in`.web.common.APIResponse
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends.dto.CreateFriendRequest
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends.dto.CreateFriendRequestResponse
import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.CreateFriendRequestUseCase
import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.ReadFriendRequestUseCase
import jakarta.validation.constraints.NotBlank
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/friends/request")
class FriendsRequestController(
    private val createFriendRequestUseCase: CreateFriendRequestUseCase,
    private val readFriendRequestUseCase: ReadFriendRequestUseCase
) {
    /**
     * 요청 생성
     */
    @PostMapping
    fun requestFriendship(
        @AuthenticationPrincipal user: UserDetails,
        @Validated @RequestBody createFriendRequest: CreateFriendRequest
    ): APIResponse<CreateFriendRequestResponse> {

        val result = createFriendRequestUseCase.request(
            user.username,
            createFriendRequest.receiverId,
            createFriendRequest.message
        )

        return APIResponse.success(
            CreateFriendRequestResponse(result.friendRequestId!!)
        )
    }

    /**
     * 요청 조회
     */
    @GetMapping
    fun getReceivedRequest(
        @AuthenticationPrincipal user: UserDetails,
    ) {
        readFriendRequestUseCase.queryReceived(user.username)
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