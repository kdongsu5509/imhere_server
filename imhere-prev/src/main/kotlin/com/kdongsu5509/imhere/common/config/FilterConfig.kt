package com.kdongsu5509.imhere.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.kdongsu5509.imhere.common.alert.port.out.MessageSendPort
import com.kdongsu5509.imhere.common.logging.LoggingFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

@Configuration
class FilterConfig(
    private val messageSendPort: MessageSendPort,
    private val objectMapper: ObjectMapper
) {

    @Bean
    fun loggingFilterRegistration(): FilterRegistrationBean<LoggingFilter> {
        val registration = FilterRegistrationBean(
            LoggingFilter(messageSendPort, objectMapper)
        )

        registration.order = Ordered.LOWEST_PRECEDENCE
        registration.addUrlPatterns("/*")

        return registration
    }
}