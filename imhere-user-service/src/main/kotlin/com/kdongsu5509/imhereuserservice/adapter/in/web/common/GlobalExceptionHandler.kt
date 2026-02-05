package com.kdongsu5509.imhereuserservice.adapter.`in`.web.common

import com.kdongsu5509.imhereuserservice.support.exception.BusinessException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Global Exception Handler for User Service
 *
 * MSA 환경에서 각 마이크로서비스는 자체 Exception Handler를 가져야 합니다.
 * API Gateway는 라우팅/인증 레벨의 오류를 처리하고,
 * 각 서비스는 비즈니스 로직 예외를 처리합니다.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * 도메인 예외 처리 (BaseException을 상속한 모든 예외)
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<APIResponse<ErrorResponse>> {
        logger.warn("Domain exception occurred: {}", ex.message, ex)

        val errorResponse = ErrorResponse(
            code = ex.errorCode.code,
            message = ex.message
        )

        return ResponseEntity
            .status(ex.errorCode.status)
            .body(APIResponse.fail(
                ex.errorCode.status.value(),
                ex.errorCode.status.reasonPhrase,
                errorResponse
            ))
    }

    /**
     * 일반 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<APIResponse<ErrorResponse>> {
        logger.error("Unexpected exception occurred", ex)

        val errorResponse = ErrorResponse(
            code = "INTERNAL_SERVER_ERROR",
            message = "서버 내부 오류가 발생했습니다."
        )

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(APIResponse.fail(
                500,
                "Internal Server Error",
                errorResponse
            ))
    }


}
