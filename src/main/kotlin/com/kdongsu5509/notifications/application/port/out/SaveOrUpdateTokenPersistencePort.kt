package com.kdongsu5509.notifications.application.port.out

import com.kdongsu5509.notifications.domain.DeviceType

interface SaveOrUpdateTokenPersistencePort {
    fun saveOrUpdate(userEmail: String, fcmToken: String, deviceType: DeviceType)
}