package com.kdongsu5509.support.exception

import com.kdongsu5509.support.common.dto.APIResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class ImHereGlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(ImHereGlobalExceptionHandler::class.java)

    @ExceptionHandler(BusinessException::class)
    private fun handleBusinessException(e: BusinessException): ResponseEntity<APIResponse<ErrorResponse>> {
        val errorCode = e.errorCode
        logger.warn("Business exception occurred: code={}, message={}", errorCode.code, e.message)

        val errorResponse = ErrorResponse(errorCode.code, e.message)
        return ResponseEntity
            .status(errorCode.status)
            .body(
                APIResponse.fail(
                    errorCode.status.value(),
                    errorCode.status.name,
                    errorResponse
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    private fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<APIResponse<ErrorResponse>> {
        logger.warn("MethodArgumentNotValidException occurred: {}", e.message)
        val errorResponse =
            ErrorResponse(GlobalErrorCode.INVALID_INPUT_VALUE.code, GlobalErrorCode.INVALID_INPUT_VALUE.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                APIResponse.fail(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.name,
                    errorResponse
                )
            )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatchExceptions(ex: MethodArgumentTypeMismatchException): ResponseEntity<APIResponse<ErrorResponse>> {
        logger.warn("MethodArgumentTypeMismatchException occurred: {}", ex.message)
        val errorResponse =
            ErrorResponse(GlobalErrorCode.INVALID_INPUT_VALUE.code, GlobalErrorCode.INVALID_INPUT_VALUE.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                APIResponse.fail(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.name,
                    errorResponse
                )
            )
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParams(ex: MissingServletRequestParameterException): ResponseEntity<APIResponse<ErrorResponse>> {
        logger.warn("MissingServletRequestParameterException occurred: {}", ex.message)
        val errorResponse =
            ErrorResponse(GlobalErrorCode.INVALID_INPUT_VALUE.code, GlobalErrorCode.INVALID_INPUT_VALUE.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                APIResponse.fail(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.name,
                    errorResponse
                )
            )
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    private fun handleAuthorizationDeniedException(e: Exception): ResponseEntity<APIResponse<ErrorResponse>> {
        logger.error("Authorization Denied Exception occurred: ", e)
        val errorResponse =
            ErrorResponse(AuthErrorCode.IMHERE_ACCESS_DENIED.code, AuthErrorCode.IMHERE_ACCESS_DENIED.message)
        return ResponseEntity
            .status(AuthErrorCode.IMHERE_ACCESS_DENIED.status)
            .body(
                APIResponse.fail(
                    HttpStatus.FORBIDDEN.value(),
                    HttpStatus.FORBIDDEN.name,
                    errorResponse
                )
            )
    }

    @ExceptionHandler(Exception::class)
    private fun handleException(e: Exception): ResponseEntity<APIResponse<ErrorResponse>> {
        logger.error("Unexpected exception occurred: ", e)
        val errorResponse = ErrorResponse(GlobalErrorCode.UNKNOWN_ERROR.code, GlobalErrorCode.UNKNOWN_ERROR.message)
        return ResponseEntity
            .status(GlobalErrorCode.UNKNOWN_ERROR.status)
            .body(
                APIResponse.fail(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.name,
                    errorResponse
                )
            )
    }
}
