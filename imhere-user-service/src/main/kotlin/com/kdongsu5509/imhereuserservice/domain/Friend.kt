package com.kdongsu5509.imhereuserservice.domain

import java.util.UUID

data class Friend(
    val registerId: UUID,
    val receiverId: UUID,
    val status: FriendshipStatus
)