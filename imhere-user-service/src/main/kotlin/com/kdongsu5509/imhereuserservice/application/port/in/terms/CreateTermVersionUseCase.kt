package com.kdongsu5509.imhereuserservice.application.port.`in`.terms

import java.time.LocalDateTime

interface CreateTermVersionUseCase {
    fun createNewTermVersion(
        termDefinitionId: Long,
        version: String,
        content: String,
        effectiveDate: LocalDateTime
    )
}