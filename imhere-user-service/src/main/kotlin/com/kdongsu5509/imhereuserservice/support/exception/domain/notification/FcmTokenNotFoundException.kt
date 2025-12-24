package com.kdongsu5509.imhereuserservice.support.exception.domain.notification
import com.kdongsu5509.imhereuserservice.exception.domain.BaseException
import com.kdongsu5509.imhereuserservice.exception.domain.ErrorCode

class FcmTokenNotFoundException(
    errorCode: ErrorCode = ErrorCode.FCM_TOKEN_NOT_FOUND,
) : BaseException(
    errorCode,
    errorCode.message
)