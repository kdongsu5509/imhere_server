package com.kdongsu5509.notifications.application.port.out

import com.kdongsu5509.notifications.domain.DeviceType

interface SaveTokenPersistencePort {
    fun save(fcmToken: String, userEmail: String, deviceType: DeviceType)
}