package com.kdongsu5509.imhereuserservice.adapter.out.persistence.friendship

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.FriendshipJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SpringDataFriendshipRepository : JpaRepository<FriendshipJpaEntity, UUID> {
}