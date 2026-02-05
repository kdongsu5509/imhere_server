package com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.common.BaseTimeEntity
import com.kdongsu5509.imhereuserservice.domain.user.OAuth2Provider
import com.kdongsu5509.imhereuserservice.domain.user.UserRole
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
@Table(name = "users")
class UserJpaEntity : BaseTimeEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    var id: UUID? = null

    @Column(unique = true)
    var email: String = ""

    var nickname: String = ""

    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.NORMAL

    @Enumerated(EnumType.STRING)
    var provider: OAuth2Provider = OAuth2Provider.KAKAO

    constructor(email: String, nickname: String, role: UserRole, provider: OAuth2Provider) : this() {
        this.email = email
        this.nickname = nickname
        this.role = role
        this.provider = provider
    }

    protected constructor()
}