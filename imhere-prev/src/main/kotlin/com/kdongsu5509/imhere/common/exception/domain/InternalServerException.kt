package com.kdongsu5509.imhere.common.exception.domain

class InternalServerException(
    errorCode: ErrorCode = ErrorCode.UNKNOWN_ERROR,
) : BaseException(
    errorCode,
    errorCode.message
)