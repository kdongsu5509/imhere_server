package com.kdongsu5509.notifications.application.port.out

import com.kdongsu5509.notifications.domain.FcmToken

interface FindTokenPort {
    fun findByUserEmail(userEmail: String): FcmToken?
}