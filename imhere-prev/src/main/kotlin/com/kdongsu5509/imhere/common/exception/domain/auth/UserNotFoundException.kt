package com.kdongsu5509.imhere.common.exception.domain.auth

import com.kdongsu5509.imhere.common.exception.domain.BaseException
import com.kdongsu5509.imhere.common.exception.domain.ErrorCode

class UserNotFoundException(
    errorCode: ErrorCode = ErrorCode.USER_NOT_FOUND,
) : BaseException(
    errorCode,
    errorCode.message
)