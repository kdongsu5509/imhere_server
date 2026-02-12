package com.kdongsu5509.imhereuserservice.application.port.`in`.friend

interface UpdateFriendRequestUseCase {
    fun acceptRequest(userEmail: String, requestId: Long)
}