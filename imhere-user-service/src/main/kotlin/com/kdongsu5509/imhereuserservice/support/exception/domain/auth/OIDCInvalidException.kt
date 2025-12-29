package com.kdongsu5509.imhereuserservice.support.exception.domain.auth

import com.kdongsu5509.imhereuserservice.support.exception.domain.BaseException
import com.kdongsu5509.imhereuserservice.support.exception.domain.ErrorCode

class OIDCInvalidException(
    errorCode: ErrorCode = ErrorCode.OIDC_INVALID,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)