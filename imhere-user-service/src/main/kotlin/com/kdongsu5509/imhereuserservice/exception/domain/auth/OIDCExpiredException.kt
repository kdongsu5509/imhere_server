package com.kdongsu5509.imhereuserservice.exception.domain.auth

import com.kdongsu5509.imhereuserservice.exception.domain.BaseException
import com.kdongsu5509.imhereuserservice.exception.domain.ErrorCode

class OIDCExpiredException(
    errorCode: ErrorCode = ErrorCode.OIDC_EXPIRED,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)