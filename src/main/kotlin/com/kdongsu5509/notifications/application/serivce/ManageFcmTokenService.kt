package com.kdongsu5509.notifications.application.serivce

import com.kdongsu5509.notifications.application.port.`in`.ManageFcmTokenUseCasePort
import com.kdongsu5509.notifications.application.port.out.SaveOrUpdateTokenPersistencePort
import com.kdongsu5509.notifications.domain.DeviceType
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class ManageFcmTokenService(
    private val saveOrUpdateTokenPersistencePort: SaveOrUpdateTokenPersistencePort,
) : ManageFcmTokenUseCasePort {
    override fun save(userEmail: String, fcmToken: String, deviceType: DeviceType) {
        saveOrUpdateTokenPersistencePort.saveOrUpdate(fcmToken, userEmail, deviceType)
    }
}