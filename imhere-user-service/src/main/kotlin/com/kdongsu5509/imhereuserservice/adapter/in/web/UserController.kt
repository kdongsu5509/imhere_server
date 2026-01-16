package com.kdongsu5509.imhereuserservice.adapter.`in`.web

import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController {

    @GetMapping("/search/{nickname}")
    fun searchUsers(
        @PathVariable
        @NotBlank(message = "이메일 혹은 사용자 닉네임을 입력하여야 합니다")
        keyword: String
    ) {
    }
}
