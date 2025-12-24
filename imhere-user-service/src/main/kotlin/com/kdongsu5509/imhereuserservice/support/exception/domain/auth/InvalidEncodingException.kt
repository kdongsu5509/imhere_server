package com.kdongsu5509.imhereuserservice.support.exception.domain.auth

import com.kdongsu5509.imhereuserservice.exception.domain.BaseException
import com.kdongsu5509.imhereuserservice.exception.domain.ErrorCode

class InvalidEncodingException(
    errorCode: ErrorCode = ErrorCode.INVALID_ENCODING,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)