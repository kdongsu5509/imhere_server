package com.kdongsu5509.support.external

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import java.net.URI

@HttpExchange
interface DiscordApiClient {

    @PostExchange
    fun sendMessage(uri: URI, @RequestBody message: DiscordMessageDto)
}
