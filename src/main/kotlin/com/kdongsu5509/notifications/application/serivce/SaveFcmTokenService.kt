package com.kdongsu5509.notifications.application.serivce

import com.kdongsu5509.notifications.application.port.`in`.SaveFcmTokenUseCasePort
import com.kdongsu5509.notifications.application.port.out.SaveTokenPersistencePort
import com.kdongsu5509.notifications.domain.DeviceType
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class SaveFcmTokenService(
    private val saveTokenPersistencePort: SaveTokenPersistencePort,
) : SaveFcmTokenUseCasePort {
    override fun save(fcmToken: String, userEmail: String, deviceType: DeviceType) {
        saveTokenPersistencePort.save(fcmToken, userEmail, deviceType)
    }
}