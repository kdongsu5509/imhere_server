package com.kdongsu5509.user.application.service.user

import com.kdongsu5509.user.domain.user.User
import java.util.*

data class ImHereJwtTokenElements(
    val uid: UUID,
    val userEmail: String,
    val userNickname: String,
    val role: String,
    val status: String
) {
    companion object {
        fun from(user: User): ImHereJwtTokenElements {
            return ImHereJwtTokenElements(
                uid = user.id!!,
                userEmail = user.email,
                userNickname = user.nickname,
                role = user.role.name,
                status = user.status.name
            )
        }
    }
}