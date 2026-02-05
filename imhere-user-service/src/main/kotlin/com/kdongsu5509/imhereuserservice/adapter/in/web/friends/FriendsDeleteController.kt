package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends

import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.CreateFriendRequestUseCase
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user/friends")
class FriendsDeleteController(
    private val createFriendRequestUseCase: CreateFriendRequestUseCase
) {
    @PostMapping("/{id}")
    fun acceptToRequest(
        @PathVariable @NotBlank(message = "요청 id 는 필수입니다")
        id: String
    ) {
        //TODO
    }
    //친구 삭제	    DELETE	/api/v1/user/friends/{friendId}
}