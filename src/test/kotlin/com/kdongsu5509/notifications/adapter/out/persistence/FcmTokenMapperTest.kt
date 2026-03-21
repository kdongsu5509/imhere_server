package com.kdongsu5509.notifications.adapter.out.persistence

import com.kdongsu5509.notifications.domain.DeviceType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class FcmTokenMapperTest {

    private val mapper = FcmTokenMapper()

    @Test
    @DisplayName("JPA 엔티티의 모든 필드가 도메인 엔티티로 정확히 매핑되어야 한다")
    fun mapToDomainEntity_Success() {
        // given
        val now = LocalDateTime.now()
        val jpaEntity = FcmTokenJpaEntity(
            userEmail = "rati@example.com",
            token = "fcm_token_value",
            deviceType = DeviceType.AOS,
        )
        jpaEntity.apply {
            id = 999L
            updatedAt = LocalDateTime.now()
        }

        // when
        val domain = mapper.mapToDomainEntity(jpaEntity)

        // then
        assertEquals(jpaEntity.id, domain.id)
        assertEquals(jpaEntity.userEmail, domain.userEmail)
        assertEquals(jpaEntity.token, domain.fcmToken)
        assertEquals(jpaEntity.deviceType, domain.deviceType)
        assertEquals(jpaEntity.updatedAt, domain.updatedAt)
    }
}