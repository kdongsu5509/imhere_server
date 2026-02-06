package com.kdongsu5509.imhereuserservice.application.port.`in`.terms

import com.kdongsu5509.imhereuserservice.adapter.`in`.web.terms.dto.NewTermVersionRequest

interface CreateTermVersionUseCase {
    fun createNewTermVersion(newTermVersionRequest: NewTermVersionRequest)
}