package com.kdongsu5509.support.external

interface DiscordMessageSendPort {
    fun sendMessage(webHookUrl: String, content: DiscordMessageDto)
}
