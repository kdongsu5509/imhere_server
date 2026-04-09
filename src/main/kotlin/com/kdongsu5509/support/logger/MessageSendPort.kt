package com.kdongsu5509.support.logger

interface MessageSendPort {
    fun sendMessage(webHookUrl: String, content: String)
}
