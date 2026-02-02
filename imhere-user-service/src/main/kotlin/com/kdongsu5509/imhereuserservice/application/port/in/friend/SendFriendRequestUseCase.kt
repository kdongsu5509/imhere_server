package com.kdongsu5509.imhereuserservice.application.port.`in`.friend

interface SendFriendRequestUseCase {
    fun request(myEmail: String, targetEmail: String)
}