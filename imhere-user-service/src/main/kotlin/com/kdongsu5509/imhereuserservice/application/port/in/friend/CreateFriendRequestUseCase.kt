package com.kdongsu5509.imhereuserservice.application.port.`in`.friend

interface CreateFriendRequestUseCase {
    fun request(myEmail: String, targetEmail: String)
}