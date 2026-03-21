package com.kdongsu5509.notifications.application.serivce

import com.kdongsu5509.notifications.application.port.out.DeleteTokenPort
import com.kdongsu5509.notifications.application.port.out.FindTokenPort
import com.kdongsu5509.notifications.application.port.out.FirebasePort
import com.kdongsu5509.notifications.domain.FCMMessageTitle
import com.kdongsu5509.notifications.domain.FcmToken
import com.kdongsu5509.support.exception.BusinessException
import com.kdongsu5509.support.exception.FCMErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FCMNotificationService(
    private val findTokenPort: FindTokenPort,
    private val deleteTokenPort: DeleteTokenPort,
    private val firebasePort: FirebasePort
) {

    fun send(receiverEmail: String, title: FCMMessageTitle, body: String) {
        val fcmToken: FcmToken = findReceiverFcmToken(receiverEmail)
        try {
            firebasePort.send(receiverEmail, title, body)
        } catch (ex: BusinessException) {
            if (ex.errorCode == FCMErrorCode.FCM_TOKEN_UNREGISTERED) {
                deleteTokenPort.deleteById(fcmToken.id!!)
            }
        }
    }

    private fun findReceiverFcmToken(receiverEmail: String): FcmToken {
        return findTokenPort.findByUserEmail(receiverEmail)
            ?: throw BusinessException(FCMErrorCode.FCM_TOKEN_NOT_FOUND)
    }
}