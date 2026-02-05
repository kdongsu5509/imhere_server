package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.port.`in`.friend.ReadMyFriendUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendLoadPort
import com.kdongsu5509.imhereuserservice.domain.friend.Friend
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class FriendReadService(private val friendLoadPort: FriendLoadPort) : ReadMyFriendUseCase {

    override fun queryMyFriends(email: String): List<Friend> {
        return friendLoadPort.findMyFriends(email)
    }

}