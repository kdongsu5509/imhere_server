package com.kdongsu5509.user.adapter.`in`.web.user

import com.kdongsu5509.support.common.dto.APIResponse
import com.kdongsu5509.user.adapter.`in`.web.user.dto.AuthenticationResponse
import com.kdongsu5509.user.adapter.`in`.web.user.dto.UserTermsConsentRequest
import com.kdongsu5509.user.application.port.`in`.user.AgreementTermUseCase
import com.kdongsu5509.user.application.port.`in`.user.ReissueJWTUseCase
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user/terms", version = "1")
class UserAgreementController(
    private val agreementTermUseCase: AgreementTermUseCase,
    private val reissueJWTUseCase: ReissueJWTUseCase
) {
    @PostMapping("/consent")
    fun consentAll(
        @AuthenticationPrincipal userDetail: UserDetails,
        @Validated @RequestBody userTermsConsentRequest: UserTermsConsentRequest
    ): APIResponse<AuthenticationResponse?> {
        agreementTermUseCase.consentAll(userDetail.username, userTermsConsentRequest)
        val jwt = reissueJwtUseCase.reissue(reAuthenticationRequest.refreshToken)
        return APIResponse.success(
            AuthenticationResponse(jwt.accessToken, jwt.refreshToken)
        )
    }

    @PostMapping("/consent/{termDefinitionId}")
    fun consentSingle(
        @AuthenticationPrincipal userDetail: UserDetails,
        @PathVariable @Validated @NotNull @Positive(message = "약관 ID는 양의 정수입니다") termDefinitionId: Long
    ): APIResponse<Unit> {
        agreementTermUseCase.consent(userDetail.username, termDefinitionId)
        return APIResponse.success()
    }
}
