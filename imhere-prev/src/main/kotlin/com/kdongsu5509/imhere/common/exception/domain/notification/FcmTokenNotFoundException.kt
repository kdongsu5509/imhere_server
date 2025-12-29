package com.kdongsu5509.imhere.common.exception.domain.notification

import com.kdongsu5509.imhere.common.exception.domain.BaseException
import com.kdongsu5509.imhere.common.exception.domain.ErrorCode

class FcmTokenNotFoundException(
    errorCode: ErrorCode = ErrorCode.FCM_TOKEN_NOT_FOUND,
) : BaseException(
    errorCode,
    errorCode.message
)