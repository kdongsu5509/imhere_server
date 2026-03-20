package com.kdongsu5509.notifications.adapter.out.persistence

import com.kdongsu5509.notifications.domain.FcmToken
import org.springframework.stereotype.Component

@Component
class FcmTokenMapper {
    fun mapToDomainEntity(jpaEntity: FcmTokenJpaEntity): FcmToken {
        return FcmToken(
            id = jpaEntity.id,
            userEmail = jpaEntity.userEmail,
            fcmToken = jpaEntity.token,
            deviceType = jpaEntity.deviceType,
            updatedAt = jpaEntity.updatedAt,
        )
    }
}