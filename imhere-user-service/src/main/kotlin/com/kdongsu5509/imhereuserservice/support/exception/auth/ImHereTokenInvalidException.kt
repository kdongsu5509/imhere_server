package com.kdongsu5509.imhereuserservice.support.exception.auth

import com.kdongsu5509.imhereuserservice.support.exception.BaseException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode

class ImHereTokenInvalidException(
    errorCode: ErrorCode = ErrorCode.IMHERE_INVALID_TOKEN,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)