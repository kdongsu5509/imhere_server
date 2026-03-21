package com.kdongsu5509.notifications.application.port.out

interface DeleteTokenPort {
    fun deleteById(fcmTokenId: Long)
}