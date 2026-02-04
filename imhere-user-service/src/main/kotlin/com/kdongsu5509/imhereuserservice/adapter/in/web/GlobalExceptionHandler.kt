package com.kdongsu5509.imhereuserservice.adapter.`in`.web

import com.kdongsu5509.imhereuserservice.support.exception.BaseException
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
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException): ResponseEntity<Map<String, Any?>> {
        logger.warn("Domain exception occurred: {}", ex.message, ex)

        val response = mapOf(
            "code" to ex.errorCode.status.value(),
            "message" to ex.errorCode.status.reasonPhrase,
            "data" to mapOf(
                "code" to ex.errorCode.code,
                "message" to ex.message
            )
        )

        return ResponseEntity
            .status(ex.errorCode.status)
            .body(response)
    }

    /**
     * 일반 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<Map<String, Any?>> {
        logger.error("Unexpected exception occurred", ex)

        val response = mapOf(
            "code" to 500,
            "message" to "Internal Server Error",
            "data" to mapOf(
                "code" to "INTERNAL_SERVER_ERROR",
                "message" to "서버 내부 오류가 발생했습니다."
            )
        )

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response)
    }

    /**
     * 에러 응답 DTO
     */
    data class ErrorResponse(
        val code: String,
        val message: String
    )
}
