package com.kdongsu5509.notifications.adapter.out.solapi


data class SolapiResponse(
    val status: String,
    val message: String
) {
    companion object {

        const val SUCCESS_STATUS = "200"
        const val FAIL_STATUS = "400"
        const val SUCCESS_MSG = "정상적으로 문자를 발송하였습니다"

        fun success(): SolapiResponse {
            return SolapiResponse(
                SUCCESS_STATUS,
                SUCCESS_MSG
            )
        }

        fun fail(errorMessage: String): SolapiResponse {
            return SolapiResponse(
                FAIL_STATUS,
                errorMessage
            )
        }
    }
}
