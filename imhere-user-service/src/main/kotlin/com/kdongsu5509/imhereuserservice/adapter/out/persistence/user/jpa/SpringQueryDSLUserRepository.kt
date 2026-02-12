package com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa

import com.kdongsu5509.imhereuserservice.domain.user.UserStatus
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class SpringQueryDSLUserRepository(private val queryFactory: JPAQueryFactory) {

    private val user = QUserJpaEntity.userJpaEntity

    fun findUserByEmail(email: String): Optional<UserJpaEntity> {
        val result = queryFactory.selectFrom(user)
            .where(isEmailMatching(email))
            .fetchOne()

        return Optional.ofNullable(result)
    }

    fun findActiveUserByEmail(email: String): Optional<UserJpaEntity> {
        val result = queryFactory.selectFrom(user)
            .where(
                isEmailMatching(email), isActive()
            )
            .fetchOne()

        return Optional.ofNullable(result)
    }

    fun findActiveUsersByEmailOrNickname(keyword: String): List<UserJpaEntity> {
        if (keyword.isBlank()) return emptyList()

        return queryFactory.selectFrom(user)
            .where(
                isNicknameMatching(keyword)
                    .or(isEmailMatching(keyword)),
                isActive()
            )
            .fetch()
    }

    fun findActiveUsersByEmails(email1: String, email2: String): List<UserJpaEntity> {
        return queryFactory.selectFrom(user)
            .where(
                user.email.`in`(email1, email2),
                isActive()
            )
            .fetch()
    }

    fun findActiveUsersByEmailAndId(email: String, id: UUID): List<UserJpaEntity> {
        return queryFactory.selectFrom(user)
            .where(
                (isEmailMatching(email).or(isIdMatching(id))),
                isActive()
            )
            .fetch()
    }

    private fun isIdMatching(id: UUID): BooleanExpression = user.id.eq(id)

    private fun isEmailMatching(keyword: String): BooleanExpression = user.email.eq(keyword)

    private fun isNicknameMatching(keyword: String): BooleanExpression = user.nickname.eq(keyword)

    private fun isActive(): BooleanExpression = user.status.eq(UserStatus.ACTIVE)
}