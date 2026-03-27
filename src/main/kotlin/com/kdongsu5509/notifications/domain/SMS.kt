package com.kdongsu5509.notifications.domain

data class SMS(
    val senderNickname: String,
    val receiverNumber: String,
    val location: String
)