package com.kdongsu5509.imhere.common.exception.domain.auth

import com.kdongsu5509.imhere.common.exception.domain.BaseException
import com.kdongsu5509.imhere.common.exception.domain.ErrorCode

class OIDCExpiredException(
    errorCode: ErrorCode = ErrorCode.OIDC_EXPIRED,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)