package com.kdongsu5509.imhereuserservice.support.exception.domain

class InternalServerException(
    errorCode: ErrorCode = ErrorCode.UNKNOWN_ERROR,
) : BaseException(
    errorCode,
    errorCode.message
)