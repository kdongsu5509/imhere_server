package com.kdongsu5509.imhereuserservice.application.service.friend

import com.kdongsu5509.imhereuserservice.application.port.out.friend.FriendRestrictionLoadPort
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestriction
import com.kdongsu5509.imhereuserservice.domain.friend.FriendRestrictionType
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

@ExtendWith(MockitoExtension::class)
class FriendRestrictionReadServiceTest {

    @Mock
    private lateinit var friendRestrictionLoadPort: FriendRestrictionLoadPort

    @InjectMocks
    private lateinit var friendRestrictionReadService: FriendRestrictionReadService

    @Test
    @DisplayName("loadPort에 잘 전달한다")
    fun getRestrictedFriends_success() {
        //given
        val testEmail = "test@test.com"
        val testFriendRestrictions = listOf(
            FriendRestriction(
                friendRestrictionId = 100L,
                targetEmail = "target@target.com",
                targetNickname = "차단혹은거절자",
                restrictionType = FriendRestrictionType.REJECT,
                createdAt = LocalDateTime.now().minusHours(1)
            )
        )
        given(friendRestrictionLoadPort.loadAll(testEmail)).willReturn(testFriendRestrictions)

        //when
        friendRestrictionReadService.getRestrictedFriends(testEmail)

        //then
        verify(friendRestrictionLoadPort, times(1)).loadAll(testEmail)
    }
}