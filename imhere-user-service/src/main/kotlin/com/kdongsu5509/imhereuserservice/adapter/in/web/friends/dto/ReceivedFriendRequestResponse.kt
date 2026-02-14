package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends.dto

data class ReceivedFriendRequestResponse(
    val friendRequestId: Long,
    val requesterEmail: String,
    val requesterNickname: String
)