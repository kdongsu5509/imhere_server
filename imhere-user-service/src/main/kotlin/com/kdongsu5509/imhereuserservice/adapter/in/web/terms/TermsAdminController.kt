package com.kdongsu5509.imhereuserservice.adapter.`in`.web.terms

import com.kdongsu5509.imhereuserservice.adapter.`in`.web.common.APIResponse
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.terms.dto.NewTermDefinitionRequest
import com.kdongsu5509.imhereuserservice.adapter.`in`.web.terms.dto.NewTermVersionRequest
import com.kdongsu5509.imhereuserservice.application.port.`in`.terms.CreateTermVersionUseCase
import com.kdongsu5509.imhereuserservice.application.port.`in`.terms.CreateTermsDefinitionUseCase
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/user/terms")
class TermsAdminController(
    private val createTermsDefinitionUseCase: CreateTermsDefinitionUseCase,
    private val createTermVersionUseCase: CreateTermVersionUseCase
) {
    /**
     * Create
     */
    // 약관 종류
    @PostMapping("/definition")
    fun createNewTermDefinition(
        @Validated @RequestBody newTermDefinitionRequest: NewTermDefinitionRequest
    ): APIResponse<Unit> {
        createTermsDefinitionUseCase.createNewTermsDefinition(
            newTermDefinitionRequest.termsName,
            newTermDefinitionRequest.termsType,
            newTermDefinitionRequest.isRequired,
        )

        return APIResponse.success()
    }

    // 약관 세부 내용
    @PostMapping("/version")
    fun createNewTermVersion(
        @Validated @RequestBody newTermVersionRequest: NewTermVersionRequest
    ): APIResponse<Unit> {
        createTermVersionUseCase.createNewTermVersion(
            newTermVersionRequest.termDefinitionId,
            newTermVersionRequest.version,
            newTermVersionRequest.content,
            newTermVersionRequest.effectiveDate
        )

        return APIResponse.success()
    }
}