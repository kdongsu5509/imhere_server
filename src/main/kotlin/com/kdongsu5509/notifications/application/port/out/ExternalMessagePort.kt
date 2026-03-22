package com.kdongsu5509.notifications.application.port.out

import com.kdongsu5509.notifications.domain.SMS

interface ExternalMessagePort {
    fun send(sms: SMS)
    fun sendMultiple(multiSMS: List<SMS>)
}