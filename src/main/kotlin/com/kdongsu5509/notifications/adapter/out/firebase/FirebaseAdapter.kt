package com.kdongsu5509.notifications.adapter.out.firebase

import com.google.firebase.messaging.*
import com.kdongsu5509.notifications.application.port.out.FirebasePort
import com.kdongsu5509.notifications.domain.FCMMessageTitle
import org.slf4j.LoggerFactory
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Component

class RetryableFcmException(message: String, cause: Throwable) : RuntimeException(message, cause)

@Component
class FirebaseAdapter(
    private val firebaseMessaging: FirebaseMessaging
) : FirebasePort {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun send(fcmToken: String, title: FCMMessageTitle, body: String) {
        if (fcmToken.isBlank()) {
            log.warn("FCM 토큰이 비어 있어 메시지를 전송하지 않습니다.")
            return
        }

        val fcmMessage = createFcmMessage(fcmToken, title, body)
        sendWithRetry(fcmToken, fcmMessage)
    }

    @Retryable(
        value = [RetryableFcmException::class],
        delay = 1000,
        multiplier = 2.0
    )
    fun sendWithRetry(fcmToken: String, message: Message) {
        try {
            firebaseMessaging.send(message)
        } catch (ex: FirebaseMessagingException) {
            handleUnregisteredException(ex)
            handleRetryableException(ex)
            handleNonRetryableException(ex)
        }
    }

    private fun createFcmMessage(fcmToken: String, title: FCMMessageTitle, body: String): Message {
        return Message.builder()
            .setNotification(
                Notification.builder()
                    .setTitle(title.content)
                    .setBody(body)
                    .build()
            )
            .setToken(fcmToken)
            .build()
    }

    private fun handleUnregisteredException(ex: FirebaseMessagingException) {
        if (ex.messagingErrorCode == MessagingErrorCode.UNREGISTERED) {
            //TODO:
            log.error("등록 해제된 토큰. DB 삭제 필요.")
        }
    }

    private fun handleRetryableException(
        ex: FirebaseMessagingException
    ) {
        val errorCode = ex.messagingErrorCode

        if (errorCode == MessagingErrorCode.UNAVAILABLE) {
            log.error("[UNAVAILABLE] FCM 서버 과부하. 지수 백오프 기반 재시도를 시작.", ex)
            throw RetryableFcmException("FCM 재시도 필요: $errorCode", ex)
        }

        if (errorCode == MessagingErrorCode.QUOTA_EXCEEDED) {
            log.error("[QUOTA_EXCEEDED] 전송 할당량 초과. 지수 백오프 기반 재시도를 시작.", ex)
            throw RetryableFcmException("FCM 재시도 필요: $errorCode", ex)
        }

        if (errorCode == MessagingErrorCode.INTERNAL) {
            log.error("[INTERNAL] FCM 알 수 없는 내부 오류 발생. 지수 백오프 기반 재시도를 시작.", ex)
            throw RetryableFcmException("FCM 재시도 필요: $errorCode", ex)
        }
    }

    private fun handleNonRetryableException(
        ex: FirebaseMessagingException
    ) {
        val errorCode = ex.messagingErrorCode

        if (errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
            log.error("[INVALID_ARGUMENT] 잘못된 매개변수(토큰 형식, 페이로드 크기 등).", ex)
        }

        if (errorCode == MessagingErrorCode.SENDER_ID_MISMATCH) {
            log.error("[SENDER_ID_MISMATCH] 발신자 ID 불일치. 인증 정보 확인 필요.", ex)
        }

        if (errorCode == MessagingErrorCode.THIRD_PARTY_AUTH_ERROR) {
            log.error("[THIRD_PARTY_AUTH_ERROR] 타사 인증 오류(APNs 인증서 또는 웹 푸시 키 만료/누락).", ex)
        }
    }
}