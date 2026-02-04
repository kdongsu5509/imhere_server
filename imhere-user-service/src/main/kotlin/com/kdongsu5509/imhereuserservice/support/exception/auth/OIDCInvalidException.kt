package com.kdongsu5509.imhereuserservice.support.exception.auth

import com.kdongsu5509.imhereuserservice.support.exception.BaseException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode

class OIDCInvalidException(
    errorCode: ErrorCode = ErrorCode.OIDC_INVALID,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)