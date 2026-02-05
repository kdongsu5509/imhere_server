package com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.adapter

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.SpringDataUserRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.SpringQueryDSLUserRepository
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.UserJpaEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.mapper.UserMapper
import com.kdongsu5509.imhereuserservice.application.port.out.user.LoadUserPort
import com.kdongsu5509.imhereuserservice.domain.user.User
import com.kdongsu5509.imhereuserservice.support.exception.BusinessException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode
import org.springframework.stereotype.Component

@Component
class UserLoadPersistenceAdapter(
    private val userMapper: UserMapper,
    private val springDataUserRepository: SpringDataUserRepository,
    private val springQueryDSLUserRepository: SpringQueryDSLUserRepository

) : LoadUserPort {
    override fun findByEmail(email: String): User {
        val findJpaEntity: UserJpaEntity? = springDataUserRepository.findByEmail(email)
        findJpaEntity ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        return userMapper.mapToDomainEntity(findJpaEntity)
    }

    override fun findByEmailAndNickname(keyword: String): List<User> {
        val findJpaEntities = springQueryDSLUserRepository.findUserByKeyword(keyword)
        if (findJpaEntities.isEmpty()) return listOf()
        return findJpaEntities.stream()
            .map { entity -> userMapper.mapToDomainEntity(entity) }
            .toList()
    }
}