package com.kdongsu5509.imhere.common.alert.port.out

interface MessageSendPort {
    fun sendMessage(content: String)
    fun sendDetailMessage(e: Throwable)
}