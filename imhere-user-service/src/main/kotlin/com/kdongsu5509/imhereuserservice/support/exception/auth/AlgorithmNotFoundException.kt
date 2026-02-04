package com.kdongsu5509.imhereuserservice.support.exception.auth

import com.kdongsu5509.imhereuserservice.support.exception.BaseException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode


class AlgorithmNotFoundException(
    errorCode: ErrorCode = ErrorCode.ALGORITHM_NOT_FOUND,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)