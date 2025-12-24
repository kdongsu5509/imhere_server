package com.kdongsu5509.imhereuserservice.exception.domain

abstract class BaseException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message
) : RuntimeException(message)