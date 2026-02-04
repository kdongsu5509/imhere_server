package com.kdongsu5509.imhereuserservice.support.exception.auth

import com.kdongsu5509.imhereuserservice.support.exception.BaseException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode

class UserNotFoundException(
    errorCode: ErrorCode = ErrorCode.USER_NOT_FOUND,
) : BaseException(
    errorCode,
    errorCode.message
)