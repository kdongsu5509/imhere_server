package com.kdongsu5509.imhereuserservice.application.service.auth.oidc.kakao

import com.kdongsu5509.imhereuserservice.application.dto.UserInformation
import com.kdongsu5509.imhereuserservice.application.port.out.token.jwt.JwtParserPort
import com.kdongsu5509.imhereuserservice.application.port.out.token.jwt.JwtVerificationPort
import com.kdongsu5509.imhereuserservice.application.port.out.token.oidc.OIDCVerificationPort
import io.jsonwebtoken.MalformedJwtException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class KakaoOIDCVerificationService(
    private val jwtParserPort: JwtParserPort,
    private val jwtVerificationPort: JwtVerificationPort
) : OIDCVerificationPort {

    override fun verifyAndReturnUserInformation(idToken: String): UserInformation {
        val payload = jwtParserPort.parse(idToken)
        jwtVerificationPort.verifyPayLoad(payload)
        payload.email ?: throw MalformedJwtException("ID 토큰에 이메일 정보가 없습니다")
        return UserInformation(payload.email, payload.nickname.orEmpty())
    }
}