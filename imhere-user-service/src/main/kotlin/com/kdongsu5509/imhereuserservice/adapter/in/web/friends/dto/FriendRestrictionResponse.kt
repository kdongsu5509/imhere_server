package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends.dto

import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestriction
import java.time.LocalDateTime

data class FriendRestrictionResponse(
    val friendRestrictionId: Long,
    val targetEmail: String,
    val targetNickname: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun fromDomain(domain: FriendRestriction): FriendRestrictionResponse {
            return FriendRestrictionResponse(
                domain.friendRestrictionId,
                domain.targetEmail,
                domain.targetNickname,
                domain.createdAt
            )
        }
    }

}
