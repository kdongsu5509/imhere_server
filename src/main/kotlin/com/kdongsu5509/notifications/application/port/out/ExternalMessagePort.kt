package com.kdongsu5509.notifications.application.port.out

import com.kdongsu5509.notifications.adapter.out.solapi.SolapiResponse
import com.kdongsu5509.notifications.domain.SMS

interface ExternalMessagePort {
    fun send(sms: SMS): SolapiResponse
    fun sendMultiple(multiSMS: List<SMS>): List<SolapiResponse>
}