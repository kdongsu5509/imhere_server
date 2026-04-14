package com.kdongsu5509.notifications.application.serivce

import com.common.testUtil.TestRedisContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@SpringBootTest(
    classes = [
        MessageIdempotencyService::class,
        MessageIdempotencyServiceTest.RedisTestConfig::class
    ]
)
@ActiveProfiles("test")
class MessageIdempotencyServiceTest : TestRedisContainer() {

    @TestConfiguration
    class RedisTestConfig {
        @Bean
        fun redisConnectionFactory(
            @Value("\${spring.data.redis.host}") host: String,
            @Value("\${spring.data.redis.port}") port: Int
        ): RedisConnectionFactory =
            LettuceConnectionFactory(host, port).also { it.afterPropertiesSet() }

        @Bean
        fun stringRedisTemplate(connectionFactory: RedisConnectionFactory): StringRedisTemplate =
            StringRedisTemplate(connectionFactory)
    }

    @Autowired
    private lateinit var stringRedisTemplate: StringRedisTemplate

    @Autowired
    private lateinit var messageIdempotencyService: MessageIdempotencyService

    @BeforeEach
    fun clearRedis() {
        stringRedisTemplate.execute { connection ->
            connection.serverCommands().flushDb()
            null
        }
    }

    @Test
    fun `처음 등장한 messageId는 처리되지 않은 것으로 간주한다`() {
        val messageId = UUID.randomUUID().toString()

        val result = messageIdempotencyService.isAlreadyProcessed(messageId)

        assertThat(result).isFalse()
    }

    @Test
    fun `markAsProcessed 이후에는 이미 처리된 것으로 간주한다`() {
        val messageId = UUID.randomUUID().toString()

        messageIdempotencyService.markAsProcessed(messageId)

        assertThat(messageIdempotencyService.isAlreadyProcessed(messageId)).isTrue()
    }

    @Test
    fun `서로 다른 messageId는 독립적으로 추적된다`() {
        val processedId = UUID.randomUUID().toString()
        val newId = UUID.randomUUID().toString()

        messageIdempotencyService.markAsProcessed(processedId)

        assertThat(messageIdempotencyService.isAlreadyProcessed(processedId)).isTrue()
        assertThat(messageIdempotencyService.isAlreadyProcessed(newId)).isFalse()
    }

    @Test
    fun `동일한 messageId에 대해 markAsProcessed를 여러 번 호출해도 안전하다`() {
        val messageId = UUID.randomUUID().toString()

        messageIdempotencyService.markAsProcessed(messageId)
        messageIdempotencyService.markAsProcessed(messageId)

        assertThat(messageIdempotencyService.isAlreadyProcessed(messageId)).isTrue()
    }
}
