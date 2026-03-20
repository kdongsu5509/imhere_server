package com.kdongsu5509.notifications.application.port.`in`

import com.kdongsu5509.notifications.domain.DeviceType

interface ManageFcmTokenUseCasePort {
    fun save(
        userEmail: String,
        fcmToken: String,
        deviceType: DeviceType
    )
}