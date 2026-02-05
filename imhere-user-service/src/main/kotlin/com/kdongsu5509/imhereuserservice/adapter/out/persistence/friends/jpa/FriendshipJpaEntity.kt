package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.common.BaseTimeEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.UserJpaEntity
import com.kdongsu5509.imhereuserservice.domain.friend.FriendshipStatus
import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.util.*

@Entity
@Table(name = "friendship")
class FriendshipJpaEntity : BaseTimeEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    var id: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    var requester: UserJpaEntity? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    var receiver: UserJpaEntity? = null

    @Enumerated(EnumType.STRING)
    var friendshipStatus: FriendshipStatus? = null

    constructor(
        requester: UserJpaEntity?,
        receiver: UserJpaEntity?,
        friendshipStatus: FriendshipStatus?
    ) : this() {
        this.requester = requester
        this.receiver = receiver
        this.friendshipStatus = friendshipStatus
    }

    protected constructor()
}