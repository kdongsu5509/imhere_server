package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRelationshipLoadPort
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRelationship
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class FriendRelationshipReadServiceTest {
    @Mock
    private lateinit var friendRelationshipLoadPort: FriendRelationshipLoadPort

    @InjectMocks
    private lateinit var friendRelationshipReadService: FriendRelationshipReadService

    @Test
    @DisplayName("FriendRelationshipReadService는 load Port에 요청을 잘 전달 및 결과를 잘 반환함.")
    fun getMyFriends_success() {
        //given
        val testEmail = "test@test.com"
        val testFriendRelationships = (1..5).map { idx -> createFriendRelationship(idx) }
        given(friendRelationshipLoadPort.findFriendsByUserEmail(testEmail))
            .willReturn(testFriendRelationships)

        //when
        friendRelationshipReadService.getMyFriends(testEmail)

        //then
        verify(friendRelationshipLoadPort, times(1)).findFriendsByUserEmail(testEmail)
    }

    private fun createFriendRelationship(idx: Int): FriendRelationship {
        return FriendRelationship(
            friendRelationshipId = UUID.randomUUID(),
            friendEmail = "test$idx@test.com",
            friendAlias = "테스터",
            createdAt = LocalDateTime.now().minusDays(1)
        )
    }

}