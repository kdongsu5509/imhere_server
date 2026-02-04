package com.kdongsu5509.imhereuserservice.support.exception.notification

import com.kdongsu5509.imhereuserservice.support.exception.BaseException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode

class FcmTokenNotFoundException(
    errorCode: ErrorCode = ErrorCode.FCM_TOKEN_NOT_FOUND,
) : BaseException(
    errorCode,
    errorCode.message
)