package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friend

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user/friends")
class FriendsQueryController {
    //내 친구 조회
    @GetMapping
    fun getMyFriends(
    ) {
        //TODO
    }

    //요청
    @GetMapping("/sent")
    fun getSentRequest(
    ) {
        //TODO
    }

    @GetMapping("/received")
    fun getReceivedRequest(
    ) {
        //TODO
    }
}