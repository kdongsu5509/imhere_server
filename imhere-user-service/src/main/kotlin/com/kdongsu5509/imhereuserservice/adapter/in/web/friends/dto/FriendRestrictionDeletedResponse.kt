package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends.dto

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestriction

data class FriendRestrictionDeletedResponse(
    val targetEmail: String,
) {
    companion object {
        fun fromDomain(domain: FriendRestriction): FriendRestrictionDeletedResponse {
            return FriendRestrictionDeletedResponse(
                domain.targetEmail
            )
        }
    }

}
