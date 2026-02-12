package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends

import com.kdongsu5509.imhereuserservice.adapter.`in`.web.common.APIResponse
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends.dto.CreateFriendRequest
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends.dto.CreateFriendRequestResponse
import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.CreateFriendRequestUseCase
import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.UpdateFriendRequestUseCase
import jakarta.validation.constraints.NotNull
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/friends/request")
class FriendsRequestCommandController(
    private val createFriendRequestUseCase: CreateFriendRequestUseCase,
    private val updateFriendRequestUseCase: UpdateFriendRequestUseCase
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

    @PostMapping("/accept/{requestId}")
    fun acceptToFriendRequest(
        @Validated
        @NotNull(message = "requestId는 필수입니다")
        @PathVariable
        requestId: Long,
        @AuthenticationPrincipal user: UserDetails,
    ) {
        updateFriendRequestUseCase.acceptRequest(user.username, requestId)
    }

    /**
     * TODO THINGS
     *       - 받은 친구 요청의 수락 및 거절
     *           - 거절 당할 경우 별도의 DB에 포함되도록.
     *           - 친구 요청 테이블에서 제거되도록
     *       - 차후 친구 검색 시 검색 결과에 포함되지 않도록
     */

    //친구 목록 조회	GET	    /api/v1/user/friends
    //친구 삭제	    DELETE	/api/v1/user/friends/{friendId}
}