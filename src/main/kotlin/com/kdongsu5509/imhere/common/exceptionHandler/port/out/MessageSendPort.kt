package com.kdongsu5509.imhere.common.exceptionHandler.port.out

interface MessageSendPort {
    fun sendMessage(content: String)
    fun sendDetailMessage(e: Throwable)
}