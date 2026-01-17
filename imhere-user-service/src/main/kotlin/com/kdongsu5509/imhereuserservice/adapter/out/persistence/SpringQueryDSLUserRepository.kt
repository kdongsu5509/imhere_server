package com.kdongsu5509.imhereuserservice.adapter.out.persistence

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class SpringQueryDSLUserRepository(private val queryFactory: JPAQueryFactory) {

    private val user = QUserJpaEntity.userJpaEntity

    fun findUserByKeyword(keyword: String): List<UserJpaEntity> {
        return queryFactory.selectFrom(user)
            .where(
                isNameMatching(keyword)
                    .or(isEmailMatching(keyword))
            )
            .fetch()
    }

    private fun isEmailMatching(keyword: String): BooleanExpression = user.email.eq(keyword)

    private fun isNameMatching(keyword: String): BooleanExpression = user.nickname.eq(keyword)
}