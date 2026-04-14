package com.kdongsu5509.notifications.adapter.`in`.web

import com.kdongsu5509.notifications.adapter.`in`.web.dto.DlqQueueInfoResponse
import com.kdongsu5509.notifications.adapter.`in`.web.dto.DlqReplayResponse
import com.kdongsu5509.notifications.application.serivce.DlqAdminService
import com.kdongsu5509.support.response.APIResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/rabbitmq/dlq")
class DlqAdminController(
    private val dlqAdminService: DlqAdminService
) {
    /** 전체 DLQ 목록 및 메시지 수 조회 */
    @GetMapping
    fun getAllDlqInfo(): APIResponse<List<DlqQueueInfoResponse>> =
        APIResponse.success(dlqAdminService.getAllDlqInfo())

    /** 특정 DLQ 정보 조회 */
    @GetMapping("/{queueName}")
    fun getDlqInfo(@PathVariable queueName: String): APIResponse<DlqQueueInfoResponse> =
        APIResponse.success(dlqAdminService.getQueueInfo(queueName))

    /**
     * DLQ 메시지 재처리
     * @param count 재처리할 최대 메시지 수 (기본값: 전체)
     */
    @PostMapping("/{queueName}/replay")
    fun replayMessages(
        @PathVariable queueName: String,
        @RequestParam(defaultValue = "2147483647") count: Int
    ): APIResponse<DlqReplayResponse> =
        APIResponse.success(dlqAdminService.replayMessages(queueName, count))

    /** DLQ 전체 메시지 삭제 */
    @DeleteMapping("/{queueName}")
    fun purgeQueue(@PathVariable queueName: String): APIResponse<Unit> {
        dlqAdminService.purgeQueue(queueName)
        return APIResponse.success()
    }
}
