package com.kdongsu5509.imhereuserservice.adapter.`in`.web.friends.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length
import java.util.*

data class UpdateFriendAliasRequest(
    @field:NotNull(message = "친구 관계 ID는 필수입니다.")
    val friendRelationshipId: UUID,

    @field:NotBlank(message = "새로운 친구 별명은 필수입니다.")
    @field:Length(min = 1, max = 20, message = "친구 별명은 1 ~ 20자까지만 입력 가능합니다.")
    val newFriendAlias: String
)