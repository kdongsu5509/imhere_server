package com.kdongsu5509.imhereuserservice.adapter.out.auth

import com.kdongsu5509.imhereuserservice.adapter.out.auth.oauth.KakaoOIDCProperties
import com.kdongsu5509.imhereuserservice.adapter.out.auth.oauth.dto.OIDCPublicKey
import com.kdongsu5509.imhereuserservice.application.dto.OIDCDecodePayload
import com.kdongsu5509.imhereuserservice.application.port.out.user.JwtParserPort
import com.kdongsu5509.imhereuserservice.application.port.out.user.JwtVerificationPort
import com.kdongsu5509.imhereuserservice.application.port.out.user.oauth.PublicKeyLoadPort
import com.kdongsu5509.imhereuserservice.support.exception.BusinessException
import com.kdongsu5509.imhereuserservice.support.exception.ErrorCode
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component

@Component
class JjwtParserAdapter(
    private val publicKeyLoadPort: PublicKeyLoadPort,
    private val kakaoOIDCProperties: KakaoOIDCProperties,
    private val jwtVerificationPort: JwtVerificationPort
) : JwtParserPort {
    override fun parse(idToken: String): OIDCDecodePayload {
        val oidcPublicKey: OIDCPublicKey = findProperOIDCPublicKey(idToken)

        val jws = jwtVerificationPort.verifySignature(
            idToken,
            oidcPublicKey.n,
            oidcPublicKey.e
        )

        return extractPayloadFromJws(jws)
    }

    private fun findProperOIDCPublicKey(idToken: String): OIDCPublicKey {
        val kidFromOriginTokenHeader = getKidFromOriginTokenHeader(idToken)
        return publicKeyLoadPort.loadPublicKey(kidFromOriginTokenHeader)
    }

    fun getKidFromOriginTokenHeader(token: String): String {
        val kakaoIss = kakaoOIDCProperties.issuer
        val kakaoAud = kakaoOIDCProperties.audience
        val parseClaimsJwt = Jwts.parserBuilder()
            .requireAudience(kakaoAud)
            .requireIssuer(kakaoIss)
            .build()
            .parseClaimsJwt(getUnsignedToken(token))
        return parseClaimsJwt.header["kid"] as String
    }

    private fun getUnsignedToken(token: String): String {
        val splitToken = token.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (splitToken.size != 3) {
            throw BusinessException(ErrorCode.OIDC_INVALID)
        }
        return "${splitToken[0]}.${splitToken[1]}."
    }

    private fun extractPayloadFromJws(jws: Jws<Claims>): OIDCDecodePayload {
        val body = jws.body
        return OIDCDecodePayload(
            iss = body.issuer,
            aud = body.audience,
            sub = body.subject,
            email = body.get("email", String::class.java),
            nickname = body.get("nickname", String::class.java)
        )
    }
}