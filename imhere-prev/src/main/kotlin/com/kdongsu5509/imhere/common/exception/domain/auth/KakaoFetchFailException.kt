package com.kdongsu5509.imhere.common.exception.domain.auth

import com.kdongsu5509.imhere.common.exception.domain.BaseException
import com.kdongsu5509.imhere.common.exception.domain.ErrorCode

class KakaoFetchFailException(
    errorCode: ErrorCode = ErrorCode.KAKAO_OIDC_PUBLIC_KEY_FETCH_FAILED,
) : BaseException(
    errorCode,
    errorCode.message
)