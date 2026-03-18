package com.kdongsu5509.support.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import tools.jackson.databind.DefaultTyping
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import tools.jackson.module.kotlin.KotlinModule
import java.time.Duration

@Configuration
class RedisConfig {

    @Value("\${spring.data.redis.host:localhost}")
    private var host: String? = null

    @Value("\${spring.data.redis.port:6379}")
    private var port: String = "6379"

    private fun getPort(): Int = port.ifBlank { "6379" }.toInt()

    // Redis 캐시용 Serializer를 Bean으로 정의
    @Bean
    fun redisJackson2JsonRedisSerializer(): GenericJacksonJsonRedisSerializer {
        val ptv = BasicPolymorphicTypeValidator.builder()
            .allowIfBaseType("com.kdongsu5509.user.")
            .allowIfBaseType("java.util.")
            .build()

        val mapper = JsonMapper.builder()
            .addModule(KotlinModule.Builder().build()) // registerModule -> addModule
            .activateDefaultTyping(ptv, DefaultTyping.NON_FINAL_AND_ENUMS, JsonTypeInfo.As.PROPERTY)
            .build() // 불변 JsonMapper 인스턴스 생성

        // 생성된 불변 매퍼를 시리얼라이저에 주입
        return GenericJacksonJsonRedisSerializer(mapper)
    }

    /**
     * @Cacheable이 사용할 RedisCacheManager를 정의합니다.
     */
    @Bean
    @Primary
    fun redisCacheManager(
        cf: RedisConnectionFactory,
        jsonRedisSerializer: GenericJacksonJsonRedisSerializer // 위에 정의한 Serializer 주입
    ): CacheManager {
        val redisCacheConfiguration =
            RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        StringRedisSerializer()
                    )
                )
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer) // 커스텀 Serializer 사용
                )
                .entryTtl(Duration.ofHours(1L))

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(cf)
            .cacheDefaults(redisCacheConfiguration)
            .build()
    }

    @Bean
    fun oidcCacheManager(
        cf: RedisConnectionFactory,
        jsonRedisSerializer: GenericJacksonJsonRedisSerializer // 위에 정의한 Serializer 주입
    ): CacheManager {
        val redisCacheConfiguration =
            RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        StringRedisSerializer()
                    )
                )
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer) // 커스텀 Serializer 사용
                )
                .entryTtl(Duration.ofDays(8L))

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(cf)
            .cacheDefaults(redisCacheConfiguration)
            .build()
    }

    // 4. CachePort 구현을 위한 RedisTemplate Bean
    @Bean("customRedisTemplate")
    fun redisTemplate(
        connectionFactory: RedisConnectionFactory,
        jsonRedisSerializer: GenericJacksonJsonRedisSerializer // 위에 정의한 Serializer 주입
    ): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = jsonRedisSerializer // 커스텀 Serializer 사용
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = jsonRedisSerializer // 커스텀 Serializer 사용

        template.afterPropertiesSet()
        return template
    }
}