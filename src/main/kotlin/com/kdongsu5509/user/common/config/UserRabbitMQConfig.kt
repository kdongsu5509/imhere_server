package com.kdongsu5509.user.common.config

import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.kotlinModule

@Configuration
class UserRabbitMQConfig {

    companion object {
        const val EXCHANGE_NAME: String = "imhere.noti.topic"

        const val FRIEND_ROUTING_KEY: String = "noti.user.friend.#"
        const val SERVICE_ROUTING_KEY: String = "noti.service.#"
    }

    @Bean(name = ["userJsonMessageConverter"])
    fun jsonMessageConverter(): JacksonJsonMessageConverter {
        val jsonMapper = JsonMapper.builder()
            .addModule(kotlinModule())
            .build()

        return JacksonJsonMessageConverter(jsonMapper)
    }

    @Bean
    fun notiExchange(): TopicExchange {
        return TopicExchange(EXCHANGE_NAME)
    }
}
