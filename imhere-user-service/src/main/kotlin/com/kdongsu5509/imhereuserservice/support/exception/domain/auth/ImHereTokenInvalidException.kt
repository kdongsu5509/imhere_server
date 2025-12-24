package com.kdongsu5509.imhereuserservice.support.exception.domain.auth

import com.kdongsu5509.imhereuserservice.exception.domain.BaseException
import com.kdongsu5509.imhereuserservice.exception.domain.ErrorCode

class ImHereTokenInvalidException(
    errorCode: ErrorCode = ErrorCode.IMHERE_INVALID_TOKEN,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)