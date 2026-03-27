package com.kdongsu5509.support.config

import com.kdongsu5509.support.exception.AuthErrorCode
import com.kdongsu5509.support.exception.BusinessException
import com.kdongsu5509.user.application.service.user.JwtAuthenticationFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true
)
@EnableConfigurationProperties(SecurityConstants::class)
class SecurityConfig(
    private val securityConstants: SecurityConstants,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { email ->
            throw BusinessException(AuthErrorCode.IMHERE_INVALID_TOKEN)
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            formLogin { disable() }
            httpBasic { disable() }

            headers {
                frameOptions { sameOrigin }
            }

            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            authorizeHttpRequests {
                securityConstants.whitelist.forEach { authorize(it, permitAll) }
                authorize(anyRequest, authenticated)
            }

            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
        }
        return http.build()
    }
}