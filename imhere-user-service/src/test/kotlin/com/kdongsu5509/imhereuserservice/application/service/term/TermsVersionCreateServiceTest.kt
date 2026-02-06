package com.kdongsu5509.imhereuserservice.application.service.term

import com.kdongsu5509.imhereuserservice.adapter.`in`.web.terms.dto.NewTermVersionRequest
import com.kdongsu5509.imhereuserservice.application.port.out.term.TermsVersionSavePort
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class TermsVersionCreateServiceTest {

    @Mock
    lateinit var termsVersionSavePort: TermsVersionSavePort

    @InjectMocks
    lateinit var termsVersionCreateService: TermsVersionCreateService

    @Test
    @DisplayName("약관 버전 생성 요청을 받으면 SavePort를 호출하여 저장한다")
    fun createNewTermVersion_success() {
        // given
        val request = NewTermVersionRequest(
            termDefinitionId = 1L,
            version = "v1.0",
            content = "약관 내용입니다.",
            effectiveDate = LocalDateTime.now()
        )

        // when
        termsVersionCreateService.createNewTermVersion(request)

        // then
        // 서비스가 받은 인자들을 Port에 그대로 잘 전달했는지 확인
        verify(termsVersionSavePort).saveTermVersion(
            request.termDefinitionId,
            request.version,
            request.content,
            request.effectiveDate
        )
    }
}