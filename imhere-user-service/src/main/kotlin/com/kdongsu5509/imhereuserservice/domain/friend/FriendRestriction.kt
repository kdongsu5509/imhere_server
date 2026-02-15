package com.kdongsu5509.imhereuserservice.domain.friend

import java.time.LocalDateTime

data class FriendRestriction(
    val friendRestrictionId: Long,
    val targetEmail: String,
    val targetNickname: String,
    val restrictionType: FriendRestrictionType,
    val createdAt: LocalDateTime
)