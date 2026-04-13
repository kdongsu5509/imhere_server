package com.kdongsu5509.notifications.adapter.out.firebase

import com.google.firebase.messaging.*
import com.kdongsu5509.notifications.domain.FCMMessageTitle
import com.kdongsu5509.support.exception.BusinessException
import com.kdongsu5509.support.exception.FCMErrorCode
import org.assertj.core.api.Assertions.assertThat
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

    companion object {
        const val FCM_TOKEN = "sample-token"
        val TITLE = FCMMessageTitle.FRIEND_REQUEST
        const val BODY = "테스트 내용"
    }

    @Test
    @DisplayName("메시지 전송 성공")
    fun send_success() {
        // when
        firebaseAdapter.send(FCM_TOKEN, TITLE, BODY)

        // then
        verify(firebaseMessaging).send(any(Message::class.java))
    }

    @Test
    @DisplayName("토큰이 공백이면 전송하지 않음")
    fun send_blank_token() {
        // when
        firebaseAdapter.send("", TITLE, BODY)

        // then
        verify(firebaseMessaging, never()).send(any())
    }

    @Test
    @DisplayName("등록 해제된 토큰일 경우 BusinessException(FCM_TOKEN_UNREGISTERED) 발생")
    fun send_unregistered_token() {
        // given
        val exception = mock(FirebaseMessagingException::class.java)
        `when`(exception.messagingErrorCode).thenReturn(MessagingErrorCode.UNREGISTERED)
        `when`(firebaseMessaging.send(any(Message::class.java))).thenThrow(exception)

        // when & then
        val businessException = assertThrows<BusinessException> {
            firebaseAdapter.send(FCM_TOKEN, TITLE, BODY)
        }
        assertThat(businessException.errorCode).isEqualTo(FCMErrorCode.FCM_TOKEN_UNREGISTERED)
    }

    @Test
    @DisplayName("재시도 가능한 오류일 경우 RetryableFcmException 발생")
    fun send_retryable_error() {
        // given
        val exception = mock(FirebaseMessagingException::class.java)
        `when`(exception.messagingErrorCode).thenReturn(MessagingErrorCode.UNAVAILABLE)
        `when`(firebaseMessaging.send(any(Message::class.java))).thenThrow(exception)

        // when & then
        assertThrows<RetryableFcmException> {
            firebaseAdapter.send(FCM_TOKEN, TITLE, BODY)
        }
    }

    @Test
    @DisplayName("재시도 불가능한 오류(INVALID_ARGUMENT)일 경우 BusinessException(FCM_INVALID_ARGUMENT) 발생")
    fun send_non_retryable_error() {
        // given
        val exception = mock(FirebaseMessagingException::class.java)
        `when`(exception.messagingErrorCode).thenReturn(MessagingErrorCode.INVALID_ARGUMENT)
        `when`(firebaseMessaging.send(any(Message::class.java))).thenThrow(exception)

        // when & then
        val businessException = assertThrows<BusinessException> {
            firebaseAdapter.send(FCM_TOKEN, TITLE, BODY)
        }
        assertThat(businessException.errorCode).isEqualTo(FCMErrorCode.FCM_INVALID_ARGUMENT)
    }

    @Test
    @DisplayName("QUOTA_EXCEEDED 오류일 경우 RetryableFcmException 발생")
    fun send_quota_exceeded_is_retryable() {
        // given
        val exception = mock(FirebaseMessagingException::class.java)
        `when`(exception.messagingErrorCode).thenReturn(MessagingErrorCode.QUOTA_EXCEEDED)
        `when`(firebaseMessaging.send(any(Message::class.java))).thenThrow(exception)

        // when & then
        assertThrows<RetryableFcmException> {
            firebaseAdapter.send(FCM_TOKEN, TITLE, BODY)
        }
    }

    @Test
    @DisplayName("INTERNAL 오류일 경우 RetryableFcmException 발생")
    fun send_internal_error_is_retryable() {
        // given
        val exception = mock(FirebaseMessagingException::class.java)
        `when`(exception.messagingErrorCode).thenReturn(MessagingErrorCode.INTERNAL)
        `when`(firebaseMessaging.send(any(Message::class.java))).thenThrow(exception)

        // when & then
        assertThrows<RetryableFcmException> {
            firebaseAdapter.send(FCM_TOKEN, TITLE, BODY)
        }
    }

    @Test
    @DisplayName("SENDER_ID_MISMATCH 오류일 경우 BusinessException(FCM_AUTH_ERROR) 발생")
    fun send_sender_id_mismatch_is_auth_error() {
        // given
        val exception = mock(FirebaseMessagingException::class.java)
        `when`(exception.messagingErrorCode).thenReturn(MessagingErrorCode.SENDER_ID_MISMATCH)
        `when`(firebaseMessaging.send(any(Message::class.java))).thenThrow(exception)

        // when & then
        val businessException = assertThrows<BusinessException> {
            firebaseAdapter.send(FCM_TOKEN, TITLE, BODY)
        }
        assertThat(businessException.errorCode).isEqualTo(FCMErrorCode.FCM_AUTH_ERROR)
    }

    @Test
    @DisplayName("THIRD_PARTY_AUTH_ERROR 오류일 경우 BusinessException(FCM_AUTH_ERROR) 발생")
    fun send_third_party_auth_error_is_auth_error() {
        // given
        val exception = mock(FirebaseMessagingException::class.java)
        `when`(exception.messagingErrorCode).thenReturn(MessagingErrorCode.THIRD_PARTY_AUTH_ERROR)
        `when`(firebaseMessaging.send(any(Message::class.java))).thenThrow(exception)

        // when & then
        val businessException = assertThrows<BusinessException> {
            firebaseAdapter.send(FCM_TOKEN, TITLE, BODY)
        }
        assertThat(businessException.errorCode).isEqualTo(FCMErrorCode.FCM_AUTH_ERROR)
    }

    @Test
    @DisplayName("처리되지 않는 오류 코드일 경우 BusinessException(FCM_UNKNOWN_ERROR) 발생")
    fun send_unknown_error_code_throws_unknown_error() {
        // given - null messagingErrorCode falls through all handlers to FCM_UNKNOWN_ERROR
        val exception = mock(FirebaseMessagingException::class.java)
        `when`(exception.messagingErrorCode).thenReturn(null)
        `when`(firebaseMessaging.send(any(Message::class.java))).thenThrow(exception)

        // when & then
        val businessException = assertThrows<BusinessException> {
            firebaseAdapter.send(FCM_TOKEN, TITLE, BODY)
        }
        assertThat(businessException.errorCode).isEqualTo(FCMErrorCode.FCM_UNKNOWN_ERROR)
    }

    @Test
    @DisplayName("data 맵 포함하여 메시지 전송 성공")
    fun send_with_data_map_success() {
        // given
        val data = mapOf("senderNickname" to "sender-nick", "senderEmail" to "sender@example.com")

        // when
        firebaseAdapter.send(FCM_TOKEN, TITLE, BODY, data)

        // then
        verify(firebaseMessaging).send(any(Message::class.java))
    }
}