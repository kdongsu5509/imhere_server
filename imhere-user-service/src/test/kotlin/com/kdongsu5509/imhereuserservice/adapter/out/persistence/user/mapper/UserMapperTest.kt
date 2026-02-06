package com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.mapper

import com.kdongsu5509.imhereuserservice.adapter.out.persistence.user.jpa.UserJpaEntity
import com.kdongsu5509.imhereuserservice.domain.user.OAuth2Provider
import com.kdongsu5509.imhereuserservice.domain.user.User
import com.kdongsu5509.imhereuserservice.domain.user.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserMapperTest {

    private val userMapper = UserMapper()

    @Test
    @DisplayName("Domain 객체를 JpaEntity로 올바르게 변환해야 한다")
    fun shouldMapDomainToJpaEntity() {
        // given
        val domainUser = User(
            email = "test@example.com",
            nickname = "동수",
            oauthProvider = OAuth2Provider.KAKAO,
            role = UserRole.NORMAL
        )

        // when
        val jpaEntity = userMapper.mapToJpaEntity(domainUser)

        // then
        assertThat(jpaEntity.email).isEqualTo(domainUser.email)
        assertThat(jpaEntity.nickname).isEqualTo(domainUser.nickname)
        assertThat(jpaEntity.role).isEqualTo(domainUser.role)
        assertThat(jpaEntity.provider).isEqualTo(domainUser.oauthProvider)
    }

    @Test
    @DisplayName("JpaEntity를 Domain 객체로 올바르게 변환해야 한다")
    fun shouldMapJpaEntityToDomain() {
        // given
        val jpaEntity = UserJpaEntity(
            "test@example.com",
            "동수",
            UserRole.NORMAL,
            OAuth2Provider.KAKAO
        )

        // when
        val domainUser = userMapper.mapToDomainEntity(jpaEntity)

        // then
        assertThat(domainUser.email).isEqualTo(jpaEntity.email)
        assertThat(domainUser.nickname).isEqualTo(jpaEntity.nickname)
        assertThat(domainUser.role).isEqualTo(jpaEntity.role)
        assertThat(domainUser.oauthProvider).isEqualTo(jpaEntity.provider)
    }
}