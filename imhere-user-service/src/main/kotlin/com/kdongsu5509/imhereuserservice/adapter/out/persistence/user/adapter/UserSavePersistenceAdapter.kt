package com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.adapter

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.SpringDataUserRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.mapper.UserMapper
import com.kdongsu5509.imhereuserservice.application.port.out.user.SaveUserPort
import com.kdongsu5509.imhereuserservice.domain.user.User
import org.springframework.stereotype.Component

@Component
class UserSavePersistenceAdapter(
    private val userMapper: UserMapper,
    private val springDataUserRepository: SpringDataUserRepository,

    ) : SaveUserPort {
    override fun save(user: User) {
        val jpaEntity = userMapper.mapToJpaEntity(user)
        springDataUserRepository.save(jpaEntity)
    }
}