package com.kdongsu5509.notifications.adapter.out.persistence

import com.kdongsu5509.notifications.adapter.out.BaseEntity
import com.kdongsu5509.notifications.domain.DeviceType
import jakarta.persistence.*

@Entity
@Table(name = "fcm_token")
class FcmTokenJpaEntity(
    @Column(nullable = false)
    var token: String,

    val userEmail: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var deviceType: DeviceType
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    fun updateToken(newToken: String) {
        this.token = newToken
    }
}