package com.kdongsu5509.notifications.adapter.out.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MessagingErrorCode
import com.kdongsu5509.notifications.domain.FCMMessageTitle
import com.kdongsu5509.support.exception.BusinessException
import com.kdongsu5509.support.exception.FCMErrorCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class FirebaseAdapterTest {

    @Mock
    private lateinit var firebaseMessaging: FirebaseMessaging

    @InjectMocks
    private lateinit var firebaseAdapter: FirebaseAdapter

    private val testToken = "test_fcm_token"
    private val testTitle = FCMMessageTitle.FRIEND_REQUEST
    private val testBody = "테스트 본문"

    @Test
    @DisplayName("정상적인 토큰과 메시지라면 전송에 성공한다")
    fun send_Success() {
        // when
        firebaseAdapter.send(testToken, testTitle, testBody)

        // then
        verify(firebaseMessaging, times(1)).send(any(Message::class.java))
    }

    @Test
    @DisplayName("토큰이 비어있으면 FCM 서버에 요청을 보내지 않는다")
    fun send_BlankToken_NoRequest() {
        // when
        firebaseAdapter.send("", testTitle, testBody)

        // then
        verify(firebaseMessaging, never()).send(any())
    }

    @Test
    @DisplayName("등록 해제된 토큰(UNREGISTERED)이면 BusinessException을 던진다")
    fun send_Unregistered_ThrowsBusinessException() {
        // given
        val exception = mock(FirebaseMessagingException::class.java)
        `when`(exception.messagingErrorCode).thenReturn(MessagingErrorCode.UNREGISTERED)
        `when`(firebaseMessaging.send(any())).thenThrow(exception)

        // when & then
        val ex = assertThrows<BusinessException> {
            firebaseAdapter.send(testToken, testTitle, testBody)
        }
        assertEquals(FCMErrorCode.FCM_TOKEN_UNREGISTERED, ex.errorCode)
    }

    @Test
    @DisplayName("서버 과부하(UNAVAILABLE) 시 RetryableFcmException을 던진다")
    fun send_Unavailable_ThrowsRetryableException() {
        // given
        val exception = mock(FirebaseMessagingException::class.java)
        `when`(exception.messagingErrorCode).thenReturn(MessagingErrorCode.UNAVAILABLE)
        `when`(firebaseMessaging.send(any())).thenThrow(exception)

        // when & then
        assertThrows<RetryableFcmException> {
            firebaseAdapter.send(testToken, testTitle, testBody)
        }
    }

    @Test
    @DisplayName("잘못된 인자(INVALID_ARGUMENT) 시 BusinessException을 던진다")
    fun send_InvalidArgument_ThrowsBusinessException() {
        // given
        val exception = mock(FirebaseMessagingException::class.java)
        `when`(exception.messagingErrorCode).thenReturn(MessagingErrorCode.INVALID_ARGUMENT)
        `when`(firebaseMessaging.send(any())).thenThrow(exception)

        // when & then
        val ex = assertThrows<BusinessException> {
            firebaseAdapter.send(testToken, testTitle, testBody)
        }
        assertEquals(FCMErrorCode.FCM_INVALID_ARGUMENT, ex.errorCode)
    }
}