package com.kdongsu5509.notifications.adapter.`in`

import com.kdongsu5509.notifications.adapter.`in`.dto.FcmTokenInfo
import com.kdongsu5509.notifications.application.port.`in`.SaveFcmTokenUseCasePort
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/notification")
class FcmTokenEnrollController(
    private val saveFcmTokenUserCasePort: SaveFcmTokenUseCasePort
) {
    @PostMapping("/enroll")
    fun enroll(
        @Validated @RequestBody fcmTokenInfo: FcmTokenInfo,
        @AuthenticationPrincipal userDetails: UserDetails
    ) {
        val userEmail = userDetails.username
        saveFcmTokenUserCasePort.save(fcmTokenInfo.fcmToken, userEmail, fcmTokenInfo.deviceType)
    }
}

