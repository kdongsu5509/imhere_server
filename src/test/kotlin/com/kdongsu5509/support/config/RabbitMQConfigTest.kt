package com.kdongsu5509.support.config

import com.common.testUtil.TestRabbitMQContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [
        RabbitMQConfig::class,
        RabbitAutoConfiguration::class
    ]
)
class RabbitMQConfigTest : TestRabbitMQContainer() {

    @Autowired
    private lateinit var amqpAdmin: AmqpAdmin

    @Test
    fun `기본 큐가 정상 선언된다`() {
        val friendQueueProps = amqpAdmin.getQueueProperties(RabbitMQConfig.FRIEND_QUEUE)
        val serviceQueueProps = amqpAdmin.getQueueProperties(RabbitMQConfig.SERVICE_QUEUE)

        assertThat(friendQueueProps).isNotNull
        assertThat(serviceQueueProps).isNotNull
    }

    @Test
    fun `DLQ 큐가 정상 선언된다`() {
        val friendDlqProps = amqpAdmin.getQueueProperties(RabbitMQConfig.FRIEND_DLQ)
        val serviceDlqProps = amqpAdmin.getQueueProperties(RabbitMQConfig.SERVICE_DLQ)

        assertThat(friendDlqProps).isNotNull
        assertThat(serviceDlqProps).isNotNull
    }

    @Test
    fun `기본 큐에 DLX 설정이 포함된다`() {
        // RabbitMQ가 큐를 수락했다는 것 자체가 x-dead-letter-exchange 인수가 유효함을 의미한다
        // (잘못된 인수를 가진 큐는 선언 시점에 broker가 에러를 반환한다)
        val friendQueueProps = amqpAdmin.getQueueProperties(RabbitMQConfig.FRIEND_QUEUE)
        val serviceQueueProps = amqpAdmin.getQueueProperties(RabbitMQConfig.SERVICE_QUEUE)

        assertThat(friendQueueProps).isNotNull
        assertThat(serviceQueueProps).isNotNull
    }

    @Test
    fun `DLQ 이름 상수가 올바르게 정의된다`() {
        assertThat(RabbitMQConfig.FRIEND_DLQ).isEqualTo("noti.queue.friend.dlq")
        assertThat(RabbitMQConfig.SERVICE_DLQ).isEqualTo("noti.queue.service.dlq")
        assertThat(RabbitMQConfig.DLX_NAME).isEqualTo("imhere.noti.dlx")
    }
}
