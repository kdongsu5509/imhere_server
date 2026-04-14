package com.kdongsu5509.notifications.adapter.`in`.web.dto

data class DlqQueueInfoResponse(
    val queueName: String,
    val messageCount: Long,
    val consumerCount: Long
)
