package com.kdongsu5509.notifications.application.port.out

interface FirebasePort {
    fun send(fcmToken: String)
}