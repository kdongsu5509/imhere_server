//package com.kdongsu5509.imhere.notification.application.service
//
//import com.kdongsu5509.notifications.application.port.`in`.SelfNotificationUserCasePort
//import org.springframework.stereotype.Service
//import org.springframework.transaction.annotation.Transactional
//
//
//@Service
//class SelfNotificationService(
//    private val findTokenPort: FindTokenPort,
//    private val firebasePort: FirebasePort
//) : SelfNotificationUserCasePort {
//
//    @Transactional(readOnly = true)
//    override fun sendToMe(email: String) {
//        val myFcmTokenInfo: FcmToken = findTokenPort.findByUserEmail(email)
//            ?: throw FcmTokenNotFoundException()
//
//        firebasePort.send(myFcmTokenInfo.fcmToken)
//    }
//}