package com.kdongsu5509.imhereuserservice.application.service.user

import com.kdongsu5509.imhereuserservice.application.dto.SelfSignedJWT
import com.kdongsu5509.imhereuserservice.application.dto.UserInformation
import com.kdongsu5509.imhereuserservice.application.port.`in`.user.AuthenticateWithOidcUseCase
import com.kdongsu5509.imhereuserservice.application.port.`in`.user.IssueJWTUseCase
import com.kdongsu5509.imhereuserservice.application.port.`in`.user.VerifyOIDCUseCase
import com.kdongsu5509.imhereuserservice.application.port.out.user.UserLoadPort
import com.kdongsu5509.imhereuserservice.application.port.out.user.UserSavePort
import com.kdongsu5509.imhereuserservice.application.port.out.user.oauth.OIDCVerificationPort
import com.kdongsu5509.imhereuserservice.domain.user.OAuth2Provider
import com.kdongsu5509.imhereuserservice.domain.user.User
import com.kdongsu5509.imhereuserservice.domain.user.UserRole
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class OIDCLoginFacade(
    private val oidcVerificationPort: OIDCVerificationPort,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userSavePort: UserSavePort,
    private val userLoadPort: UserLoadPort,
) : AuthenticateWithOidcUseCase, VerifyOIDCUseCase, IssueJWTUseCase {
    override fun verifyIdTokenAndReturnJwt(idToken: String, oAuth2Provider: OAuth2Provider): SelfSignedJWT {
        val userInformation = verify(idToken, oAuth2Provider)
        return issue(userInformation.email, userInformation.nickname, oAuth2Provider)
    }

    override fun verify(oidc: String, oAuth2Provider: OAuth2Provider): UserInformation {
        return oidcVerificationPort.verifyAndReturnUserInformation(oidc)
    }

    override fun issue(email: String, nickname: String, oauth2Provider: OAuth2Provider): SelfSignedJWT {
        if (isNewMember(email)) {
            saveNewMembersInformation(email, nickname, oauth2Provider)
        }

        val savedUser = userLoadPort.findByEmail(email)

        return createJWTWithUserInfo(savedUser)
    }

    private fun createJWTWithUserInfo(savedUser: User): SelfSignedJWT {
        return jwtTokenProvider.issueJwtAuth(
            savedUser.email,
            savedUser.role.toString()
        )
    }

    private fun saveNewMembersInformation(email: String, nickname: String, oauth2Provider: OAuth2Provider) {
        val user = User(email, nickname, oauth2Provider, UserRole.NORMAL)
        userSavePort.save(user)
    }

    private fun isNewMember(email: String): Boolean = !userLoadPort.existsByEmail(email)
}