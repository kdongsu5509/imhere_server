package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.common.BaseTimeEntity
import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.UserJpaEntity
import jakarta.persistence.*

@Entity
@Table(name = "friend_request")
class FriendRequestJpaEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    val requester: UserJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    val receiver: UserJpaEntity,

    @Column(nullable = false)
    var message: String? = null
) : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}