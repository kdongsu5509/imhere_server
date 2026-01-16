package com.kdongsu5509.imhereuserservice.adapter.out.persistence

import com.kdongsu5509.imhereuserservice.domain.FriendshipStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.hibernate.annotations.UuidGenerator
import java.util.UUID

@Entity
class FriendsJpaEntity : BaseTimeEntity {
    @Id
    @GeneratedValue
    @UuidGenerator
    var id: UUID? = null

    var registerId: UUID? = null

    var receiverId: UUID? = null

    @Enumerated(EnumType.STRING)
    var friendshipStatus: FriendshipStatus? = null

    constructor(id: UUID?, registerId: UUID?, receiverId: UUID?, friendshipStatus: FriendshipStatus?): this() {
        this.id = id
        this.registerId = registerId
        this.receiverId = receiverId
        this.friendshipStatus = friendshipStatus
    }

    protected constructor()
}