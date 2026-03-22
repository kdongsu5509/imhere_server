package com.kdongsu5509.notifications.domain

data class SMS(
    val receiverNumber: String,
    val senderNickname: String,
    val location: String
)