package com.kdongsu5509.imhereuserservice.support.config

import com.kdongsu5509.imhereuserservice.application.service.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    securedEnabled = true,  // @Secured 어노테이션 활성화
    jsr250Enabled = true    // @RolesAllowed 어노테이션 활성화
)
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }           // REST API 서버이므로 CSRF 비활성화
            formLogin { disable() }      // 기본 폼 로그인 비활성화
            httpBasic { disable() }      // HTTP Basic 인증 비활성화

            // JWT를 사용 -> 세션 미사용
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            // 2. JWT 필터 적용
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)

            // 3. 인가 설정
            authorizeHttpRequests {
                authorize("/api/*/auth/**", permitAll) //TODO : 변경 필수.
                authorize("/actuator/**", permitAll)
                authorize(anyRequest, authenticated)
            }
        }
        return http.build()
    }
}