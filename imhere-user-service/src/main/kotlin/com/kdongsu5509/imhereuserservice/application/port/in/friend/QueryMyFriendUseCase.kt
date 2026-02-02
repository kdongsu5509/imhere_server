package com.kdongsu5509.imhereuserservice.application.port.`in`.friend

import com.kdongsu5509.imhereuserservice.domain.Friend

interface QueryMyFriendUseCase {
    //내 친구들
    fun queryMyFriends(email: String): List<Friend>
}