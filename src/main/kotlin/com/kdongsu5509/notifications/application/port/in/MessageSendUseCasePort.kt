package com.kdongsu5509.notifications.application.port.`in`

interface MessageSendUseCasePort {
    fun send(senderNickname: String, receiverNumber: String, location: String)
    fun sendMultiple(
        senderNickname: String,
        receiverNumbers: List<String>,
        location: String
    )
}