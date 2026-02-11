package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends.dto

import java.util.*

data class ReceivedFriendRequestResponse(
    val friendRequestId: UUID,
    val requesterEmail: String,
    val requesterNickname: String
)