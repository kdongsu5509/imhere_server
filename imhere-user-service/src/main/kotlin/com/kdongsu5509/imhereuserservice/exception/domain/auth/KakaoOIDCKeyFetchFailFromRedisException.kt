package com.kdongsu5509.imhereuserservice.exception.domain.auth

import com.kdongsu5509.imhereuserservice.exception.domain.BaseException
import com.kdongsu5509.imhereuserservice.exception.domain.ErrorCode

class KakaoOIDCKeyFetchFailFromRedisException(
    errorCode: ErrorCode = ErrorCode.KAKAO_OIDC_PUBLIC_KEY_FETCH_FROM_REDIS_FAILED,
) : BaseException(
    errorCode,
    errorCode.message
)