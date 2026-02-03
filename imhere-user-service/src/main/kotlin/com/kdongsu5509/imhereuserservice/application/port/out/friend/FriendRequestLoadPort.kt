package com.kdongsu5509.imhereuserservice.application.port.out.friend

import com.kdongsu5509.imhereuserservice.domain.friend.Friend

interface FriendRequestLoadPort {
    fun findMyRequest(email: String): List<Friend>
    fun findReceived(email: String): List<Friend>
//    fun findRequest(id: )
}