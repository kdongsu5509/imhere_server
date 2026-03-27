package com.kdongsu5509.support.exception

import org.springframework.http.HttpStatus

enum class ExternalSMSErrorCode(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
) : BaseErrorCode {
    NOT_ALLOW_EMPTY(
        HttpStatus.BAD_REQUEST,
        "EXTERNAL_SMS_001",
        "빈 문자는 허용되지 않습니다."
    )
}
