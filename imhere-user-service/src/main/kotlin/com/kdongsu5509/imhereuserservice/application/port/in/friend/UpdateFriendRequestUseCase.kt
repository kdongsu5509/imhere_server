package com.kdongsu5509.imhereuserservice.application.port.`in`.friend

import java.util.*

interface UpdateFriendRequestUseCase {
    fun acceptRequest(username: String, requestId: UUID)
}