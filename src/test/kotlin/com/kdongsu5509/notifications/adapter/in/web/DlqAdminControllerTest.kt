package com.kdongsu5509.notifications.adapter.`in`.web

import com.common.testUtil.ControllerTestSupport
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.kdongsu5509.notifications.adapter.`in`.web.dto.DlqQueueInfoResponse
import com.kdongsu5509.notifications.adapter.`in`.web.dto.DlqReplayResponse
import com.kdongsu5509.notifications.application.serivce.DlqAdminService
import com.kdongsu5509.support.config.RabbitMQConfig
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DlqAdminControllerTest : ControllerTestSupport() {

    companion object {
        const val BASE_URL = "/api/admin/rabbitmq/dlq"
    }

    @MockitoBean
    private lateinit var dlqAdminService: DlqAdminService

    // ── GET /api/admin/rabbitmq/dlq ────────────────────────────────────────────

    @Test
    @WithMockUser(roles = ["ADMIN"])
    @DisplayName("전체 DLQ 목록을 조회한다")
    fun getAllDlqInfo_success() {
        val response = listOf(
            DlqQueueInfoResponse(RabbitMQConfig.FRIEND_DLQ, 3L, 0L),
            DlqQueueInfoResponse(RabbitMQConfig.SERVICE_DLQ, 1L, 0L)
        )
        given(dlqAdminService.getAllDlqInfo()).willReturn(response)

        mockMvc.perform(get(BASE_URL).with(csrf()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].queueName").value(RabbitMQConfig.FRIEND_DLQ))
            .andExpect(jsonPath("$.data[0].messageCount").value(3))
            .andDo(
                document(
                    "admin-dlq-list",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("관리자 - DLQ")
                            .summary("전체 DLQ 목록 조회")
                            .description("모든 Dead Letter Queue의 이름과 메시지 수를 반환합니다.")
                            .build()
                    )
                )
            )
    }

    @Test
    @WithMockUser(roles = ["USER"])
    @DisplayName("관리자 권한 없이 DLQ 목록 조회 시 403을 반환한다")
    fun getAllDlqInfo_forbidden() {
        mockMvc.perform(get(BASE_URL).with(csrf()))
            .andExpect(status().isForbidden)
    }

    // ── GET /api/admin/rabbitmq/dlq/{queueName} ────────────────────────────────

    @Test
    @WithMockUser(roles = ["ADMIN"])
    @DisplayName("특정 DLQ 정보를 조회한다")
    fun getDlqInfo_success() {
        val queueName = RabbitMQConfig.FRIEND_DLQ
        val response = DlqQueueInfoResponse(queueName, 5L, 0L)
        given(dlqAdminService.getQueueInfo(queueName)).willReturn(response)

        mockMvc.perform(get("$BASE_URL/$queueName").with(csrf()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.queueName").value(queueName))
            .andExpect(jsonPath("$.data.messageCount").value(5))
            .andDo(
                document(
                    "admin-dlq-get",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("관리자 - DLQ")
                            .summary("특정 DLQ 정보 조회")
                            .description("queueName에 해당하는 DLQ의 메시지 수 및 컨슈머 수를 반환합니다.")
                            .build()
                    )
                )
            )
    }

    // ── POST /api/admin/rabbitmq/dlq/{queueName}/replay ───────────────────────

    @Test
    @WithMockUser(roles = ["ADMIN"])
    @DisplayName("DLQ 메시지를 전체 재처리한다")
    fun replayMessages_all_success() {
        val queueName = RabbitMQConfig.FRIEND_DLQ
        val replayResponse = DlqReplayResponse(queueName, 3)
        given(dlqAdminService.replayMessages(eq(queueName), any())).willReturn(replayResponse)

        mockMvc.perform(
            post("$BASE_URL/$queueName/replay").with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.queueName").value(queueName))
            .andExpect(jsonPath("$.data.replayedCount").value(3))
            .andDo(
                document(
                    "admin-dlq-replay-all",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("관리자 - DLQ")
                            .summary("DLQ 메시지 재처리 (전체)")
                            .description("DLQ에 쌓인 메시지를 원본 큐로 재발행합니다. count 파라미터로 개수를 제한할 수 있습니다.")
                            .build()
                    )
                )
            )
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    @DisplayName("DLQ 메시지를 지정한 개수만큼 재처리한다")
    fun replayMessages_limited_success() {
        val queueName = RabbitMQConfig.FRIEND_DLQ
        val replayResponse = DlqReplayResponse(queueName, 2)
        given(dlqAdminService.replayMessages(queueName, 2)).willReturn(replayResponse)

        mockMvc.perform(
            post("$BASE_URL/$queueName/replay")
                .param("count", "2")
                .with(csrf())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.replayedCount").value(2))
            .andDo(
                document(
                    "admin-dlq-replay-limited",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("관리자 - DLQ")
                            .summary("DLQ 메시지 재처리 (개수 제한)")
                            .description("count 파라미터로 재처리할 메시지 수를 제한합니다.")
                            .build()
                    )
                )
            )
    }

    @Test
    @WithMockUser(roles = ["USER"])
    @DisplayName("관리자 권한 없이 재처리 요청 시 403을 반환한다")
    fun replayMessages_forbidden() {
        mockMvc.perform(
            post("$BASE_URL/${RabbitMQConfig.FRIEND_DLQ}/replay").with(csrf())
        ).andExpect(status().isForbidden)
    }

    // ── DELETE /api/admin/rabbitmq/dlq/{queueName} ────────────────────────────

    @Test
    @WithMockUser(roles = ["ADMIN"])
    @DisplayName("DLQ 메시지를 전체 삭제(Purge)한다")
    fun purgeQueue_success() {
        val queueName = RabbitMQConfig.FRIEND_DLQ
        willDoNothing().given(dlqAdminService).purgeQueue(queueName)

        mockMvc.perform(
            delete("$BASE_URL/$queueName").with(csrf())
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "admin-dlq-purge",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("관리자 - DLQ")
                            .summary("DLQ 전체 메시지 삭제")
                            .description("지정한 DLQ의 메시지를 모두 삭제합니다. 복구가 불가하므로 주의가 필요합니다.")
                            .build()
                    )
                )
            )
    }

    @Test
    @WithMockUser(roles = ["USER"])
    @DisplayName("관리자 권한 없이 DLQ 삭제 요청 시 403을 반환한다")
    fun purgeQueue_forbidden() {
        mockMvc.perform(
            delete("$BASE_URL/${RabbitMQConfig.FRIEND_DLQ}").with(csrf())
        ).andExpect(status().isForbidden)
    }
}
