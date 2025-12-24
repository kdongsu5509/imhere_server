package com.kdongsu5509.imhereuserservice.exception.domain.auth

import com.kdongsu5509.imhereuserservice.exception.domain.BaseException
import com.kdongsu5509.imhereuserservice.exception.domain.ErrorCode


class AlgorithmNotFoundException(
    errorCode: ErrorCode = ErrorCode.ALGORITHM_NOT_FOUND,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)