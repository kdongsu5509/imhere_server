package com.kdongsu5509.imhereuserservice.adapter.out.persistence

import com.querydsl.core.QueryFactory
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class SpringQueryDSLUserRepository(private val queryFactory: JPAQueryFactory) {
    fun findUserByKeyword(keyword: String): List<UserJpaEntity>? {
        return null;
    }
}