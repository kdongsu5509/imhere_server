package com.kdongsu5509.imhere.common.exception

import com.kdongsu5509.imhere.common.alert.port.out.MessageSendPort
import com.kdongsu5509.imhere.common.exception.domain.BaseException
import com.kdongsu5509.imhere.common.exception.domain.ErrorCode
import com.kdongsu5509.imhere.common.exception.domain.ErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ImhereExceptionHandler(
    private val messageSendPort: MessageSendPort,
    sendPort: MessageSendPort
) {

    //BaseException 처리
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(ex.errorCode.code, ex.message)
        messageSendPort.sendMessage(ex.message)
        return ResponseEntity.status(ex.errorCode.status).body(errorResponse)
    }

    //알 수 없는 서버 에러 처리
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.UNKNOWN_ERROR
        ex.printStackTrace()
        val errorResponse = ErrorResponse(errorCode.code, errorCode.message)

        messageSendPort.sendDetailMessage(ex)
        return ResponseEntity.status(errorCode.status).body(errorResponse)
    }
}