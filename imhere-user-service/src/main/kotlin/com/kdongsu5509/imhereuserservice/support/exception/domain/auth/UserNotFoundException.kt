package com.kdongsu5509.imhereuserservice.support.exception.domain.auth

import com.kdongsu5509.imhereuserservice.exception.domain.BaseException
import com.kdongsu5509.imhereuserservice.exception.domain.ErrorCode

class UserNotFoundException(
    errorCode: ErrorCode = ErrorCode.USER_NOT_FOUND,
) : BaseException(
    errorCode,
    errorCode.message
)