package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friends.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringDataFriendRestrictionRepository : JpaRepository<FriendRestrictionJpaEntity, Long> {
}