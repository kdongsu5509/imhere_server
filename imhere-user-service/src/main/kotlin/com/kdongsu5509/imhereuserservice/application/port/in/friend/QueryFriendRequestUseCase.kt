package com.kdongsu5509.imhereuserservice.application.port.`in`.friend

import com.kdongsu5509.imhereuserservice.domain.Friend

interface QueryFriendRequestUseCase {
    //내가 보낸 것
    fun queryMySent(email: String): List<Friend>

    //남이 나에게 보낸 것
    fun queryReceived(email: String): List<Friend>
}