package com.kdongsu5509.imhereuserservice.adapter.out.persistence

import com.kdongsu5509.imhereuserservice.domain.User
import org.springframework.stereotype.Component

@Component
class UserMapper {
    fun mapToJpaEntity(domainUser: User): UserJpaEntity {
        return UserJpaEntity(
            domainUser.email,
            domainUser.role,
            domainUser.oauthProvider
        )
    }

    fun mapToDomainEntity(jpaEntity: UserJpaEntity): User {
        return User(
            jpaEntity.email,
            jpaEntity.provider,
            jpaEntity.role
        )
    }
}