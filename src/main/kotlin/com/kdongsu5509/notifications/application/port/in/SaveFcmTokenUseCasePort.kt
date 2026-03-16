package com.kdongsu5509.notifications.application.port.`in`

import com.kdongsu5509.notifications.domain.DeviceType

interface SaveFcmTokenUseCasePort {
    fun save(fcmToken: String, userEmail: String, deviceType: DeviceType)
}