package com.kdongsu5509.notifications.adapter.out.persistence

import com.kdongsu5509.notifications.application.port.out.FindTokenPort
import com.kdongsu5509.notifications.application.port.out.SaveOrUpdateTokenPersistencePort
import com.kdongsu5509.notifications.domain.DeviceType
import com.kdongsu5509.notifications.domain.FcmToken
import org.springframework.stereotype.Component

@Component
class FcmOrUpdateTokenPersistenceAdapter(
    private val fcmTokenMapper: FcmTokenMapper,
    private val springDataFcmTokenRepository: SpringDataFcmTokenRepository,
) : SaveOrUpdateTokenPersistencePort, FindTokenPort {

    override fun saveOrUpdate(userEmail: String, fcmToken: String, deviceType: DeviceType) {
        val existingEntity = springDataFcmTokenRepository.findByUserEmail(userEmail)

        if (existingEntity != null) {
            if (existingEntity.token != fcmToken) {
                existingEntity.updateToken(fcmToken)
            }
        } else {
            val newEntity = FcmTokenJpaEntity(
                token = fcmToken,
                userEmail = userEmail,
                deviceType = deviceType
            )
            springDataFcmTokenRepository.save(newEntity)
        }
    }

    override fun findByUserEmail(userEmail: String): FcmToken? {
        return springDataFcmTokenRepository.findByUserEmail(userEmail)
            ?.let { fcmTokenMapper.mapToDomainEntity(it) }
    }
}