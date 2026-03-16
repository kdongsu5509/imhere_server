package com.kdongsu5509.notifications.adapter.out.persistence

import com.kdongsu5509.notifications.adapter.out.BaseEntity
import com.kdongsu5509.notifications.domain.DeviceType
import com.kdongsu5509.user.adapter.out.persistence.user.jpa.UserJpaEntity
import jakarta.persistence.*

@Entity
@Table(name = "fcm_token")
class FcmTokenJpaEntity(
    @Column(nullable = false)
    var token: String,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    val user: UserJpaEntity,

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