package com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa

import com.kdongsu5509.imhereuserservice.domain.user.OAuth2Provider
import com.kdongsu5509.imhereuserservice.domain.user.UserRole
import com.kdongsu5509.imhereuserservice.domain.user.UserStatus
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(SpringQueryDSLUserRepositoryTest.TestConfig::class)
class SpringQueryDSLUserRepositoryTest @Autowired constructor(
    private val em: EntityManager,
    private val userRepository: SpringQueryDSLUserRepository
) {

    @TestConfiguration
    class TestConfig(private val em: EntityManager) {
        @Bean
        fun jpaQueryFactory(): JPAQueryFactory = JPAQueryFactory(em)

        @Bean
        fun springQueryDSLUserRepository(jpaQueryFactory: JPAQueryFactory) =
            SpringQueryDSLUserRepository(jpaQueryFactory)
    }

    @BeforeEach
    fun setUp() {
        val users = (1..3).map { createTestUser(it, UserStatus.ACTIVE) } +
                (4..5).map { createTestUser(it, UserStatus.PENDING) }
        saveAll(users)
    }

    /**
     * findUserByEmail 테스트
     */
    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    @DisplayName("상태에 상관없이 email로 사용자를 조회한다")
    fun findUserByEmail_success(idx: Int) {

        val result = userRepository.findUserByEmail("test$idx@kakao.com")

        Assertions.assertEquals(true, result.isPresent)
        Assertions.assertEquals("test$idx@kakao.com", result.get().email)
        Assertions.assertEquals("테스터$idx", result.get().nickname)
    }

    /**
     * findActiveUserByEmail 테스트
     */

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3])
    @DisplayName("ACTIVE 상태인 사용자는 이메일로 검색된다")
    fun findActiveUserByEmail_active(idx: Int) {
        val result = userRepository.findActiveUserByEmail("test$idx@kakao.com")
        Assertions.assertTrue(result.isPresent)
        Assertions.assertEquals("test$idx@kakao.com", result.get().email)
        Assertions.assertEquals("테스터$idx", result.get().nickname)
    }

    @ParameterizedTest
    @ValueSource(ints = [4, 5])
    @DisplayName("PENDING 상태인 사용자는 이메일로 검색되지 않는다")
    fun findActiveUserByEmail_pending(idx: Int) {
        val result = userRepository.findActiveUserByEmail("test$idx@kakao.com")
        Assertions.assertTrue(result.isEmpty)
    }

    @ParameterizedTest
    @ValueSource(ints = [1001, 1002, 1003, 1004, 1005])
    @DisplayName("존재하지 않는 사용자는 검색되지 않는다")
    fun findActiveUserByEmail_not_exist(idx: Int) {
        val result = userRepository.findActiveUserByEmail("test$idx@kakao.com")
        Assertions.assertTrue(result.isEmpty)
    }

    /**
     * findActiveUserByKeyword 테스트
     */
    @ParameterizedTest
    @ValueSource(strings = ["테스터1", "테스터2", "테스터3", "test1@kakao.com", "test2@kakao.com", "test3@kakao.com"])
    @DisplayName("키워드(닉네임/이메일)로 활성 사용자를 정확히 찾는다")
    fun findActiveUserByKeyword_success(testKeyword: String) {
        val result = userRepository.findActiveUserByKeyword(testKeyword)

        Assertions.assertEquals(1, result.size)
        Assertions.assertTrue(result.any { it.nickname == testKeyword || it.email == testKeyword })
    }

    @ParameterizedTest
    @ValueSource(strings = ["테스터1", "테스터2", "테스터3"])
    @DisplayName("중복된 닉네임이 있는 경우 모두 조회된다")
    fun findActiveUserByKeyword_duplication(testKeyword: String) {
        // given
        saveAll(
            listOf(
                createTestUser(100, UserStatus.ACTIVE, "테스터1"),
                createTestUser(101, UserStatus.ACTIVE, "테스터2"),
                createTestUser(102, UserStatus.ACTIVE, "테스터3")
            )
        )

        // when
        val result = userRepository.findActiveUserByKeyword(testKeyword)

        // then
        Assertions.assertEquals(2, result.size)
        Assertions.assertTrue(result.all { it.nickname == testKeyword })
    }

    @Test
    @DisplayName("키워드가 비어 있는 경우 빈 리스트가 반환된다")
    fun findActiveUserByKeyword_empty_keyword() {
        // when
        val result = userRepository.findActiveUserByKeyword("")

        // then
        Assertions.assertEquals(0, result.size)
    }

    @Test
    @DisplayName("키워드가 비어 있는 경우 빈 리스트가 반환된다")
    fun findActiveUserByKeyword_zero_match_keyword() {
        // when
        val result = userRepository.findActiveUserByKeyword("테스트99999")

        // then
        Assertions.assertEquals(0, result.size)
    }

    private fun createTestUser(
        idx: Int,
        status: UserStatus,
        nickname: String? = null
    ): UserJpaEntity {
        return UserJpaEntity(
            email = "test$idx@kakao.com",
            nickname = nickname ?: "테스터$idx",
            role = UserRole.NORMAL,
            provider = OAuth2Provider.KAKAO,
            status = status
        )
    }

    private fun saveAll(users: List<UserJpaEntity>) {
        users.forEach { em.persist(it) }
        em.flush()
        em.clear()
    }
}