package com.kdongsu5509.user.adapter.out.auth.oauth

import com.kdongsu5509.user.adapter.out.auth.oauth.dto.OIDCPublicKey
import com.kdongsu5509.user.adapter.out.auth.oauth.dto.OIDCPublicKeyResponse
import com.kdongsu5509.user.application.port.out.user.oauth.OauthClientPort
import com.kdongsu5509.user.application.service.user.KakaoPublicKeyScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.test.web.servlet.client.RestTestClient
import tools.jackson.databind.json.JsonMapper

@SpringBootTest(
    properties = [
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
    ]
)
@AutoConfigureMockRestServiceServer
class KakaoOauthClientTest {

    companion object {
        const val KAKAO_URL = "https://kauth.kakao.com/.well-known/jwks.json"
        val mockResponseJson = createMockKakaoPublicKeyResp()

        private fun createMockKakaoPublicKeyResp(): String {
            val mockResponse = OIDCPublicKeyResponse(
                keys = listOf(
                    OIDCPublicKey("kid1", "RSA", "sig", "n_value", "e_value")
                )
            )
            return JsonMapper().writeValueAsString(mockResponse)
        }
    }

    private lateinit var restTestClient: RestTestClient;

    @Autowired
    private lateinit var oauthClientPort: OauthClientPort

    @Autowired
    private lateinit var mockServer: MockRestServiceServer

    @MockitoBean
    private lateinit var kakaoPublicKeyScheduler: KakaoPublicKeyScheduler

    @MockitoBean(name = "oidcCacheManager")
    private lateinit var oidcCacheManager: CacheManager

//    @TestConfiguration
//    class TestConfig {
//        @Bean("oidcCacheManager")
//        fun testCacheManager(): CacheManager {
//            return ConcurrentMapCacheManager()
//        }
//    }

    @BeforeEach
    fun setUp() {
        restTestClient = RestTestClient.bindToServer().baseUrl(KAKAO_URL).build()
    }

    @Test
    @DisplayName("카카오 공개키 요청 시 정상적으로 데이터를 파싱해서 반환한다")
    fun getPublicKeyFromProvider_success() {
        // given
        mockServer.expect(requestTo(KAKAO_URL))
            .andRespond(withSuccess(mockResponseJson, MediaType.APPLICATION_JSON))

        // when
        val result = oauthClientPort.getPublicKeyFromProvider()

        // then
        assertThat(result).isNotNull
        assertThat(result?.keys).hasSize(1)
        assertThat(result?.keys?.get(0)?.kid).isEqualTo("kid1")

        mockServer.verify()
    }
}