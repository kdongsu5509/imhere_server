package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friendship

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.FriendshipJpaEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.QFriendshipJpaEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.QUserJpaEntity
import com.kdongsu5509.imhereuserservice.domain.friend.FriendshipStatus
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class SpringQueryDSLFriendRepository(
    private val queryFactory: JPAQueryFactory
) {

    private val friendship = QFriendshipJpaEntity.friendshipJpaEntity
    private val requester = QUserJpaEntity("requesterUser")
    private val receiver = QUserJpaEntity("receiverUser")

    // 1. 내가 보낸 친구 요청 조회 (대기 중)
    fun findSentRequests(myEmailAddress: String): List<FriendshipJpaEntity> {
        return selectFromFriendsWithUser()
            .where(
                friendship.requester.email.eq(myEmailAddress),
                friendship.friendshipStatus.eq(FriendshipStatus.PENDING)
            )
            .fetch()
    }

    // 2. 나에게 온 친구 요청 조회 (대기 중)
    fun findReceivedRequests(myEmailAddress: String): List<FriendshipJpaEntity> {
        return selectFromFriendsWithUser()
            .where(
                friendship.receiver.email.eq(myEmailAddress),
                friendship.friendshipStatus.eq(FriendshipStatus.PENDING)
            )
            .fetch()
    }

    // 3. 서로 수락된 친구 목록 조회 (내가 보낸 사람이든 받은 사람이든 상관없음)
    fun findAcceptedFriends(myEmailAddress: String): List<FriendshipJpaEntity> {
        return selectFromFriendsWithUser()
            .where(
                (friendship.requester.email.eq(myEmailAddress).or(friendship.receiver.email.eq(myEmailAddress))),
                friendship.friendshipStatus.eq(FriendshipStatus.ACCEPTED)
            )
            .fetch()
    }

    private fun selectFromFriendsWithUser() = queryFactory
        .selectFrom(friendship)
        .join(friendship.requester, requester).fetchJoin()
        .join(friendship.receiver, receiver).fetchJoin()
}