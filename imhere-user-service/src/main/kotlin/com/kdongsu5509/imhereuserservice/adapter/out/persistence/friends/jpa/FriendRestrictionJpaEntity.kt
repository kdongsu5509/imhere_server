package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.common.BaseTimeEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.UserJpaEntity
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestrictionType
import jakarta.persistence.*

@Entity
@Table(name = "friend_restrictions")
class FriendRestrictionJpaEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    val actor: UserJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    val target: UserJpaEntity,

    @Enumerated(EnumType.STRING)
    val type: FriendRestrictionType
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    companion object {
        fun createFromRejection(
            actor: UserJpaEntity,
            target: UserJpaEntity
        ): FriendRestrictionJpaEntity {
            return FriendRestrictionJpaEntity(
                actor, target, FriendRestrictionType.REJECT
            )
        }
    }
}