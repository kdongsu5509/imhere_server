package com.kdongsu5509.notifications.adapter.out.persistence

import com.kdongsu5509.notifications.application.port.out.FindTokenPort
import com.kdongsu5509.notifications.application.port.out.SaveTokenPersistencePort
import com.kdongsu5509.notifications.domain.DeviceType
import com.kdongsu5509.notifications.domain.FcmToken
import com.kdongsu5509.support.exception.BusinessException
import com.kdongsu5509.support.exception.UserErrorCode
import com.kdongsu5509.user.adapter.out.persistence.user.jpa.SpringDataUserRepository
import org.springframework.stereotype.Component

@Component
class FcmTokenPersistenceAdapter(
    private val fcmTokenMapper: FcmTokenMapper,
    private val springDataFcmTokenRepository: SpringDataFcmTokenRepository,
    private val springDataUserRepository: SpringDataUserRepository,
) : SaveTokenPersistencePort, FindTokenPort {

    override fun save(fcmToken: String, userEmail: String, deviceType: DeviceType) {
        val existingEntity = springDataFcmTokenRepository.findByUserEmail(userEmail)

        if (existingEntity != null) {
            if (existingEntity.token != fcmToken) {
                existingEntity.updateToken(fcmToken)
            }
        } else {
            val user =
                springDataUserRepository.findByEmail(userEmail) ?: throw BusinessException(UserErrorCode.USER_NOT_FOUND)

            val newEntity = FcmTokenJpaEntity(
                token = fcmToken,
                user = user,
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