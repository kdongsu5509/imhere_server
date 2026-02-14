package com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.QFriendRelationshipsJpaEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.QFriendRequestJpaEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa.QFriendRestrictionJpaEntity
import com.kdongsu5509.imhereuserservice.domain.user.UserStatus
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions
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

    fun searchNewFriendCandidates(userEmail: String, keyword: String): List<UserJpaEntity> {
        val currentUserId = queryFactory.select(user.id)
            .from(user)
            .where(isEmailMatching(userEmail))
            .fetchOne() ?: return emptyList()

        return queryFactory.selectFrom(user)
            .where(
                isNicknameMatching(keyword).or(isEmailMatching(keyword)),
                isActive(),
                user.id.ne(currentUserId),
                isNotAlreadyFriend(currentUserId),
                isNotUnderRequest(currentUserId),
                isNotRestricted(currentUserId)
            ).fetch()
    }

    private fun isIdMatching(id: UUID): BooleanExpression = user.id.eq(id)

    private fun isEmailMatching(keyword: String): BooleanExpression = user.email.eq(keyword)

    private fun isNicknameMatching(keyword: String): BooleanExpression = user.nickname.eq(keyword)

    private fun isActive(): BooleanExpression = user.status.eq(UserStatus.ACTIVE)

    private fun isNotAlreadyFriend(currentUserId: UUID): BooleanExpression {
        val relationship = QFriendRelationshipsJpaEntity.friendRelationshipsJpaEntity

        return user.id.notIn(
            //내가 주인 관계인 친구 제외.
            JPAExpressions.select(relationship.friendUser.id)
                .from(relationship)
                .where(relationship.ownerUser.id.eq(currentUserId))
        ).and(
            //내가 타겟 관계인 친구 제외
            user.id.notIn(
                JPAExpressions.select(relationship.ownerUser.id)
                    .from(relationship)
                    .where(relationship.friendUser.id.eq(currentUserId))
            )
        )
    }

    private fun isNotUnderRequest(currentUserId: UUID): BooleanExpression {
        val requests = QFriendRequestJpaEntity.friendRequestJpaEntity
        return user.id.notIn(
            //내가 보낸 요청 받은 사람 제외
            JPAExpressions.select(requests.receiver.id)
                .from(requests)
                .where(requests.requester.id.eq(currentUserId))
        ).and(
            user.id.notIn(
                // 내가 받은 요청 보낸 사람 제외
                JPAExpressions.select(requests.requester.id)
                    .from(requests)
                    .where(requests.receiver.id.eq(currentUserId))
            )
        )
    }

    private fun isNotRestricted(currentUserId: UUID): BooleanExpression {
        val restrictions = QFriendRestrictionJpaEntity.friendRestrictionJpaEntity
        return user.id.notIn(
            //내가 차단/거절한 사람들 제외
            JPAExpressions
                .select(restrictions.target.id)
                .from(restrictions)
                .where(restrictions.actor.id.eq(currentUserId))
        ).and(
            user.id.notIn(
                //나를 차단/거절한 사람들 제외
                JPAExpressions.select(restrictions.actor.id)
                    .from(restrictions)
                    .where(restrictions.target.id.eq(currentUserId))
            )
        )
    }
}