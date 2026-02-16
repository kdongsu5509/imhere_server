package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRelationshipLoadPort
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRelationshipUpdatePort
import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRestrictionSavePort
import com.kdongsu5509.imhereuserservice.application.port.out.user.UserLoadPort
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRelationship
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRequestUserInfo
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestrictionType.BLOCK
import com.kdongsu5509.imhereuserservice.domain.user.OAuth2Provider
import com.kdongsu5509.imhereuserservice.domain.user.User
import com.kdongsu5509.imhereuserservice.domain.user.UserRole
import com.kdongsu5509.imhereuserservice.domain.user.UserStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.time.LocalDateTime
import java.util.*

class FriendRelationshipUpdateServiceTest {

    @Mock
    private lateinit var friendRelationshipUpdatePort: FriendRelationshipUpdatePort

    @Mock
    private lateinit var friendRelationshipLoadPort: FriendRelationshipLoadPort

    @Mock
    private lateinit var friendRestrictionSavePort: FriendRestrictionSavePort

    @Mock
    private lateinit var userLoadPort: UserLoadPort

    @InjectMocks
    private lateinit var friendRelationshipUpdateService: FriendRelationshipUpdateService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    @DisplayName("Successfully change friend alias")
    fun changeFriendAlias() {
        // given
        val userEmail = "test@test.com"
        val relationshipId = UUID.randomUUID()
        val newAlias = "newAlias"
        val relationship = createTestRelationship(relationshipId, "friend@test.com", newAlias)

        `when`(friendRelationshipUpdatePort.updateAlias(userEmail, relationshipId, newAlias))
            .thenReturn(relationship)

        // when
        val result = friendRelationshipUpdateService.changeFriendAlias(userEmail, relationshipId, newAlias)

        // then
        assertThat(result.friendAlias).isEqualTo(newAlias)
        verify(friendRelationshipUpdatePort).updateAlias(userEmail, relationshipId, newAlias)
    }

    @Test
    @DisplayName("Successfully block a friend and delete relationship")
    fun block() {
        // given
        val userEmail = "test@test.com"
        val friendEmail = "friend@test.com"
        val relationshipId = UUID.randomUUID()

        val requester = createTestUser(userEmail, "requester")
        val receiver = createTestUser(friendEmail, "receiver")
        val relationship = createTestRelationship(relationshipId, friendEmail, "friend")

        `when`(friendRelationshipLoadPort.findFriendRelationshipByRelationshipId(relationshipId))
            .thenReturn(relationship)
        `when`(userLoadPort.findActiveUserByEmailOrNull(userEmail)).thenReturn(requester)
        `when`(userLoadPort.findActiveUserByEmailOrNull(friendEmail)).thenReturn(receiver)

        // when
        friendRelationshipUpdateService.block(userEmail, relationshipId)

        // then
        val requesterInfo = FriendRequestUserInfo(requester.id!!, requester.email, requester.nickname)
        val receiverInfo = FriendRequestUserInfo(receiver.id!!, receiver.email, receiver.nickname)

        verify(friendRestrictionSavePort).save(
            receiverInfo,
            requesterInfo,
            BLOCK
        )
        verify(friendRelationshipUpdatePort).delete(userEmail, relationshipId)
    }

    // --- Helper Methods ---
    private fun createTestUser(email: String, nickname: String) = User(
        id = UUID.randomUUID(),
        email = email,
        nickname = nickname,
        oauthProvider = OAuth2Provider.KAKAO,
        role = UserRole.NORMAL,
        status = UserStatus.ACTIVE
    )

    private fun createTestRelationship(id: UUID, friendEmail: String, alias: String) = FriendRelationship(
        friendRelationshipId = id,
        friendEmail = friendEmail,
        friendAlias = alias,
        createdAt = LocalDateTime.now()
    )
}