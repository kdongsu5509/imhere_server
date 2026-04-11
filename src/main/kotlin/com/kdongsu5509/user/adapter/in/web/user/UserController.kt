package com.kdongsu5509.user.adapter.`in`.web.user

import com.kdongsu5509.support.response.APIResponse
import com.kdongsu5509.user.adapter.`in`.web.user.dto.NicknameChangeRequest
import com.kdongsu5509.user.adapter.`in`.web.user.dto.UserInfoResponse
import com.kdongsu5509.user.adapter.`in`.web.user.dto.UserSearchResponse
import com.kdongsu5509.user.application.port.`in`.user.ReadUserUseCase
import com.kdongsu5509.user.application.port.`in`.user.UpdateUserUseCase
import jakarta.validation.constraints.NotBlank
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user/info", version = "1")
class UserController(
    private val readUserUseCase: ReadUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) {

    /**
     * 개인 정보 조회
     */
    @GetMapping("/me")
    fun searchMyInfo(
        @AuthenticationPrincipal user: UserDetails
    ): APIResponse<UserInfoResponse> {
        val myInfo = readUserUseCase.searchMe(user.username)

        return APIResponse.success(
            UserInfoResponse(
                myInfo.email, myInfo.nickname
            )
        )
    }

    /**
     * 개인 닉네임 변경
     */
    @PostMapping("/nickname")
    fun changeNickName(
        @AuthenticationPrincipal user: UserDetails,
        @Validated @RequestBody newNicknameChangeRequest: NicknameChangeRequest
    ): APIResponse<UserInfoResponse> {
        val updatedMyInfo = updateUserUseCase.changeNickName(
            user.username,
            newNicknameChangeRequest.newNickname
        )

        return APIResponse.success(
            UserInfoResponse(
                updatedMyInfo.email, updatedMyInfo.nickname
            )
        )
    }

    /**
     * 사용자 조회
     */
    @GetMapping("/{keyword}")
    fun searchUsers(
        @AuthenticationPrincipal user: UserDetails,
        @PathVariable @NotBlank(message = "이메일 또는 사용자 닉네임을 입력하여야 합니다")
        keyword: String
    ): APIResponse<List<UserSearchResponse>> {
        val findingUsers = readUserUseCase.searchPotentialFriendsUser(user.username, keyword)

        return APIResponse.success(
            findingUsers.map { user ->
                UserSearchResponse(user.id, user.email, user.nickname)
            }
        )
    }
}
