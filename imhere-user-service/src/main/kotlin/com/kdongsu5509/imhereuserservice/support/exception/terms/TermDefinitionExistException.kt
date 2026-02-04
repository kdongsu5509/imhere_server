package com.kdongsu5509.imhereuserservice.support.exception.terms

import com.kdongsu5509.imhereuserservice.support.exception.BaseException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode

class TermDefinitionExistException(
    errorCode: ErrorCode = ErrorCode.TERM_DEFINITION_EXIST,
    detailMessage: String? = null
) : BaseException(
    errorCode,
    detailMessage ?: errorCode.message
)