package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends.dto

import java.util.*

data class ReceivedFriendRequestResponseDetail(
    val friendRequestId: UUID,
    val requesterEmail: String,
    val requesterNickname: String,
    val message: String
)