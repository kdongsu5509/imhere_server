package com.kdongsu5509.imhere.common.exception.domain.auth

import com.kdongsu5509.imhere.common.exception.domain.BaseException
import com.kdongsu5509.imhere.common.exception.domain.ErrorCode

class InvalidEncodingException(
    errorCode: ErrorCode = ErrorCode.INVALID_ENCODING,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)