package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SpringDataFriendRestrictionRepository : JpaRepository<FriendRestrictionJpaEntity, Long> {
    @Query(
        """
        select fr from FriendRestrictionJpaEntity fr 
        join fetch fr.target 
        where fr.actor.id = :actorId
    """
    )
    fun findByActorId(actorId: UUID): List<FriendRestrictionJpaEntity>
}