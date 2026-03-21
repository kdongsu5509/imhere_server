package com.kdongsu5509.support.exception

import org.springframework.http.HttpStatus

enum class FCMErrorCode(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
) : BaseErrorCode {
    FCM_TOKEN_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "FCM_001",
        "사용자의 FCM 토큰을 찾을 수 없습니다"
    ),
    FCM_TOKEN_UNREGISTERED(
        HttpStatus.NOT_FOUND,
        "FCM_002",
        "사용자의 FCM 토큰이 만료되었습니다"
    ),
    FCM_INVALID_ARGUMENT(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "FCM_003",
        "잘못된 매개변수로 인해 알람 발송 오류가 발생하였습니다"
    ),
    FCM_AUTH_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "FCM_004",
        "서비스 내부의 설정값 오류로 인해 알람 발송 오류가 발생하였습니다"
    ),
    FCM_UNKNOWN_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "FCM_005",
        "알람 발송 중 알 수 없는 오류가 발생하였습니다"
    )
}
