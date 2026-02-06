package com.kdongsu5509.imhereuserservice.adapter.`in`.web.common

import com.kdongsu5509.imhereuserservice.support.exception.BusinessException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode
import com.kdongsu5509.imhereuserservice.support.exception.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * 1. 도메인 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<APIResponse<ErrorResponse>> {
        logger.warn("Business exception occurred: code={}, message={}", ex.errorCode.code, ex.message)

        val errorResponse = ErrorResponse(
            code = ex.errorCode.code,
            message = ex.message
        )

        return ResponseEntity
            .status(ex.errorCode.status)
            .body(
                APIResponse.fail(
                    ex.errorCode.status.value(),
                    ex.errorCode.status.reasonPhrase,
                    errorResponse
                )
            )
    }

    /**
     * 2. 권한 거부 예외 처리
     */
    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(ex: AuthorizationDeniedException): ResponseEntity<APIResponse<ErrorResponse>> {
        logger.warn("Authorization denied: {}", ex.message)

        val errorResponse = ErrorResponse(
            code = ErrorCode.FORBIDDEN.code,
            message = ErrorCode.FORBIDDEN.message
        )

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(
                APIResponse.fail(
                    HttpStatus.FORBIDDEN.value(),
                    HttpStatus.FORBIDDEN.reasonPhrase,
                    errorResponse
                )
            )
    }

    /**
     * 3. 일반 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<APIResponse<ErrorResponse>> {
        logger.error("Unexpected exception occurred: ", ex)

        val errorResponse = ErrorResponse(
            code = "INTERNAL_SERVER_ERROR",
            message = "서버 내부 오류가 발생했습니다."
        )

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                APIResponse.fail(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
                    errorResponse
                )
            )
    }
}