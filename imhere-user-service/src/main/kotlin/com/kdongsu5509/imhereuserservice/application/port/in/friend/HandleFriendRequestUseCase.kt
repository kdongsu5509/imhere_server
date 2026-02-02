package com.kdongsu5509.imhereuserservice.application.port.`in`.friend

interface HandleFriendRequestUseCase {
    //수락
    fun accept(requestId: Long)

    //거절
    fun reject(requestId: Long)
}