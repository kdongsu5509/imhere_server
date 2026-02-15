package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SpringDataFriendRelationshipsRepository : JpaRepository<FriendRelationshipsJpaEntity, UUID> {
    @Query("select f from FriendRelationshipsJpaEntity f join fetch f.ownerUser join fetch f.friendUser where f.ownerUser.id = :ownerUserId")
    fun findByOwnerUserId(ownerUserId: UUID): List<FriendRelationshipsJpaEntity>
}