package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends

import com.kdongsu5509.imhereuserservice.adapter.`in`.web.common.APIResponse
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends.dto.FriendRestrictionResponse
import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.ReadFriendsRestrictionUseCase
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user/friends/restriction")
class FriendsRestrictionController(
    private val readFriendsRestrictionUseCase: ReadFriendsRestrictionUseCase
) {
    @GetMapping
    fun getMyFriends(
        @AuthenticationPrincipal user: UserDetails,
    ): APIResponse<List<FriendRestrictionResponse>> {
        val myRestrictedFriends = readFriendsRestrictionUseCase.getRestrictedFriends(user.username)
        return APIResponse.success(
            myRestrictedFriends.map {
                FriendRestrictionResponse.fromDomain(it)
            }
        )
    }
}