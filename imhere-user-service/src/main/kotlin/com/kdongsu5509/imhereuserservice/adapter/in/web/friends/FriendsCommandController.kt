package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends

import org.jetbrains.annotations.NotNull
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user/friends")
class FriendsCommandController(
) {
    //TODO : 3가지 API 추가.
    //1. 친구 삭제 - 친구와 나 둘 다 삭제.
    //2. 친구 차단 - 삭제 + 차단
    //3. 친구 별명 변경

    @PostMapping("/block/{friendRelationshipId}")
    fun blockFriend(
        @AuthenticationPrincipal user: UserDetails,
        @PathVariable @Validated @NotNull friendRelationshipId: Long
    ) {

    }

    @DeleteMapping
    fun deleteMyFriends() {

    }
}