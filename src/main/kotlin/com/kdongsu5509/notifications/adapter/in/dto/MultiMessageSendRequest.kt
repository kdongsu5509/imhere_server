package com.kdongsu5509.notifications.adapter.`in`.dto

import org.jetbrains.annotations.NotNull

data class MultiMessageSendRequest(
    @param:NotNull
    val receiversNumbers: List<String>,
    @param:NotNull
    val location: String
)