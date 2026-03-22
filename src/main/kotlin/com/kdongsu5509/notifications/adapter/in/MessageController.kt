//package com.kdongsu5509.notifications.adapter.`in`
//
//import com.kdongsu5509.notifications.adapter.`in`.dto.MessageSendRequest
//import com.kdongsu5509.notifications.application.port.`in`.MessageSendUseCasePort
//import org.springframework.security.core.annotation.AuthenticationPrincipal
//import org.springframework.security.core.userdetails.UserDetails
//import org.springframework.web.bind.annotation.PostMapping
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//
//@RestController
//@RequestMapping("/api/notification", version = "1")
//class MessageController(
//    private val messageSendUseCasePort: MessageSendUseCasePort,
//    private val multipleMessageSendUseCasePort: MultipleMessageSendUseCasePort
//) {
//
//    @PostMapping("/send")
//    fun send(
//        @AuthenticationPrincipal user: UserDetails,
//        @RequestBody request: MessageSendRequest
//    ) {
//        val email = user.username
//        singleMessageSendUseCasePort.send(request, email)
//    }
//
//    @PostMapping("/multipleSend")
//    fun sendMultiple(@RequestBody request: MultipleMessageSendRequest, @AuthenticationPrincipal user: UserDetails) {
//        val email = user.username
//        multipleMessageSendUseCasePort.send(request, email)
//    }
//}