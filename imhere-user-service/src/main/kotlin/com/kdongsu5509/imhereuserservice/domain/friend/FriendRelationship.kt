package com.kdongsu5509.imhereuserservice.domain.friend

import java.time.LocalDateTime
import java.util.*

data class FriendRelationship(
    val friendRelationshipId: UUID,
    val friendEmail: String,
    val friendAlias: String,
    val createdAt: LocalDateTime
)