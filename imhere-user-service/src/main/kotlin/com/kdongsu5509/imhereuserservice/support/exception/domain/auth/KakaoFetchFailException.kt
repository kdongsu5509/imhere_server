package com.kdongsu5509.imhereuserservice.support.exception.domain.auth

import com.kdongsu5509.imhereuserservice.support.exception.domain.BaseException
import com.kdongsu5509.imhereuserservice.support.exception.domain.ErrorCode

class KakaoFetchFailException(
    errorCode: ErrorCode = ErrorCode.KAKAO_OIDC_PUBLIC_KEY_FETCH_FAILED,
) : BaseException(
    errorCode,
    errorCode.message
)