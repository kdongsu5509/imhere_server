package com.kdongsu5509.notifications.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ResourceLoader
import java.io.InputStream

@Profile("!test")
@Configuration
@EnableConfigurationProperties(FcmProperties::class)
class FirebaseConfig(
    private val fcmProperties: FcmProperties,
    private val resourceLoader: ResourceLoader
) {
    @Bean
    fun firebaseApp(): FirebaseApp {
        // file:/path 또는 classpath:name 형식 모두 지원
        // 환경 변수 FIREBASE_PATH 로 런타임 주입 가능 (docker-compose.prod.yml 참고)
        val path = fcmProperties.path
        val resource = if (path.contains(":")) {
            resourceLoader.getResource(path)
        } else {
            resourceLoader.getResource("classpath:$path")
        }
        val serviceAccount: InputStream = resource.inputStream

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        return FirebaseApp.initializeApp(options)
    }

    @Bean
    fun firebaseMessaging(firebaseApp: FirebaseApp): FirebaseMessaging {
        return FirebaseMessaging.getInstance(firebaseApp)
    }
}