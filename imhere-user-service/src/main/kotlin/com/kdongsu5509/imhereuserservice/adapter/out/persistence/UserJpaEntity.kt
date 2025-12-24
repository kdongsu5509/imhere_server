package com.kdongsu5509.imhereuserservice.adapter.out.persistence

import com.kdongsu5509.imhereuserservice.domain.OAuth2Provider
import com.kdongsu5509.imhereuserservice.domain.UserRole
import com.kdongsu5509.imhereuserservice.support.persistence.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
class UserJpaEntity : BaseEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    var id: UUID? = null //TODO: 여기서 생성 방식으로 인한 문제가 발생하여서 동시성 비슷한 문제가 발생.

    var email: String = ""

    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.NORMAL

    @Enumerated(EnumType.STRING)
    var provider: OAuth2Provider = OAuth2Provider.KAKAO

    constructor(email: String, role: UserRole, provider: OAuth2Provider) : this() {
        this.email = email
        this.role = role
        this.provider = provider
    }

    protected constructor()
}