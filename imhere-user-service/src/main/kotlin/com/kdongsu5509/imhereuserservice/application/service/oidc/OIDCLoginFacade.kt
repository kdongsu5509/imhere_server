package com.kdongsu5509.imhereuserservice.application.service.oidc

import com.kdongsu5509.imhereuserservice.application.dto.SelfSignedJWT
import com.kdongsu5509.imhereuserservice.application.dto.UserInformation
import com.kdongsu5509.imhereuserservice.application.port.`in`.HandleOIDCUseCase
import com.kdongsu5509.imhereuserservice.application.port.`in`.IssueJWTPort
import com.kdongsu5509.imhereuserservice.application.port.`in`.VerifyOIDCPort
import com.kdongsu5509.imhereuserservice.application.port.out.CheckUserPort
import com.kdongsu5509.imhereuserservice.application.port.out.LoadUserPort
import com.kdongsu5509.imhereuserservice.application.port.out.SaveUserPort
import com.kdongsu5509.imhereuserservice.application.port.out.token.oidc.OIDCVerificationPort
import com.kdongsu5509.imhereuserservice.application.service.jwt.JwtTokenProvider
import com.kdongsu5509.imhereuserservice.domain.OAuth2Provider
import com.kdongsu5509.imhereuserservice.domain.User
import com.kdongsu5509.imhereuserservice.domain.UserRole
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class OIDCLoginFacade(
    private val oidcVerificationPort: OIDCVerificationPort,
    private val checkUserPort: CheckUserPort,
    private val jwtTokenProvider: JwtTokenProvider,
    private val saveUserPort: SaveUserPort,
    private val loadUserPort: LoadUserPort,
) : HandleOIDCUseCase, VerifyOIDCPort, IssueJWTPort {
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

        val savedUser = loadUserPort.findByEmail(email)

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
        saveUserPort.save(user)
    }

    private fun isNewMember(email: String): Boolean = !checkUserPort.existsByEmail(email)
}