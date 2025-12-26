package com.kdongsu5509.imhereuserservice.support.exception.domain.auth

import com.kdongsu5509.imhereuserservice.support.exception.domain.BaseException
import com.kdongsu5509.imhereuserservice.support.exception.domain.ErrorCode

class InvalidKeyException(
    errorCode: ErrorCode = ErrorCode.INVALID_KEY,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)