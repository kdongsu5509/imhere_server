package com.kdongsu5509.user.testSupport

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter
import tools.jackson.databind.json.JsonMapper

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
//@Import(RestDocsConfiguration::class)
@ExtendWith(RestDocumentationExtension::class)
abstract class ControllerTestSupport : TestRedisContainer() {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var jsonMapper: JsonMapper

//    @Autowired
//    protected lateinit var restDocs: RestDocumentationResultHandler

    @BeforeEach
    fun setUp(
        webApplicationContext: WebApplicationContext,
        restDocumentationContextProvider: RestDocumentationContextProvider
    ) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//            .apply<DefaultMockMvcBuilder>(
//                MockMvcRestDocumentation.documentationConfiguration(restDocumentationContextProvider)
//            )
            .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
//            .alwaysDo<DefaultMockMvcBuilder>(restDocs)
            .addFilters<DefaultMockMvcBuilder>(CharacterEncodingFilter("UTF-8", true))
            .build()
    }
}