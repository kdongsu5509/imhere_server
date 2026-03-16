package com.kdongsu5509.notifications.domain

import java.time.LocalDateTime

data class FcmToken(
    var id: Long?,
    var userEmail: String,
    var fcmToken: String,
    var deviceType: DeviceType,
    var updatedAt: LocalDateTime?
)