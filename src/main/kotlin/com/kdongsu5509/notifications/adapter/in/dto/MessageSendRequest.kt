package com.kdongsu5509.notifications.adapter.`in`.dto

import org.jetbrains.annotations.NotNull

data class MessageSendRequest(
    @param:NotNull
    val message: String,

    @param:NotNull
    val receiverNumber: String
)
