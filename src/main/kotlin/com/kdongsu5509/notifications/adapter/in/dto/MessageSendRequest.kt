package com.kdongsu5509.notifications.adapter.`in`.dto

import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull

data class MessageSendRequest(
    @param:NotNull
    val receiverNumber: String,
    @param:NotNull
    @param:Size(max = 45)
    val message: String
)

//TODO : SMS -> 45자 제한
//TODO : FCM -> 100자 제한


//data class SMS(
//    val receiverNumber: String,
//    val senderNickname: String,
//    val location: String
//)