package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.common.BaseTimeEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.UserJpaEntity
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
@Table(name = "friend_relationships")
class FriendRelationshipsJpaEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    val ownerUser: UserJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_user_id", nullable = false)
    val friendUser: UserJpaEntity,

    @Column(name = "friend_alias", nullable = false)
    var friendAlias: String

) : BaseTimeEntity() {
    @Id
    @GeneratedValue
    @UuidGenerator
    val id: UUID? = null

    fun changeFriendAlias(newFriendAlias: String) {
        this.friendAlias = newFriendAlias
    }

    companion object {
        fun createFromAcceptance(
            owner: UserJpaEntity,
            friend: UserJpaEntity,
            alias: String? = null
        ): FriendRelationshipsJpaEntity {
            return FriendRelationshipsJpaEntity(
                ownerUser = owner,
                friendUser = friend,
                friendAlias = alias ?: friend.nickname
            )
        }
    }
}