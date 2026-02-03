package com.kdongsu5509.imhereuserservice.application.port.out.friend

import com.kdongsu5509.imhereuserservice.domain.friend.Friend

interface FriendLoadPort {
    fun findMyFriends(email: String): List<Friend>
}