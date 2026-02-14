package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends.dto

data class ReceivedFriendRequestResponseDetail(
    val friendRequestId: Long,
    val requesterEmail: String,
    val requesterNickname: String,
    val message: String
)