package com.kdongsu5509.imhereuserservice.support.exception.auth

import com.kdongsu5509.imhereuserservice.support.exception.BaseException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode

class ImHereTokenExpiredException(
    errorCode: ErrorCode = ErrorCode.IMHERE_EXPIRED_TOKEN,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)