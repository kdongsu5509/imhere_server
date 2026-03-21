package com.kdongsu5509.notifications.adapter.out.persistence

import com.kdongsu5509.notifications.domain.DeviceType
import com.kdongsu5509.notifications.domain.FcmToken
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class FcmOrUpdateTokenPersistenceAdapterTest {

    @Mock
    lateinit var fcmTokenMapper: FcmTokenMapper

    @Mock
    lateinit var repository: SpringDataFcmTokenRepository

    @InjectMocks
    lateinit var adapter: FcmOrUpdateTokenPersistenceAdapter

    private val email = "rati@example.com"
    private val token = "new_fcm_token"
    private val device = DeviceType.AOS

    @Test
    @DisplayName("기존 토큰이 없으면 새로운 엔티티를 저장한다")
    fun save_NewEntity() {
        var testEntity = FcmTokenJpaEntity(
            token = token,
            userEmail = email,
            deviceType = device
        )
        testEntity.apply {
            id = 999L
            updatedAt = LocalDateTime.now()
        }
        `when`(repository.save(any(FcmTokenJpaEntity::class.java))).thenReturn(testEntity)
        `when`(repository.findByUserEmail(email)).thenReturn(null)

        adapter.saveOrUpdate(email, token, device)

        verify(repository, times(1)).save(any(FcmTokenJpaEntity::class.java))
    }

    @Test
    @DisplayName("기존 토큰이 있고 값이 다르면 업데이트를 수행한다")
    fun update_ExistingEntity() {
        val existingEntity = spy(FcmTokenJpaEntity(token = "old_token", userEmail = email, deviceType = device))
        `when`(repository.findByUserEmail(email)).thenReturn(existingEntity)

        adapter.saveOrUpdate(email, token, device)

        verify(existingEntity).updateToken(token)
    }

    @Test
    @DisplayName("사용자 이메일로 도메인 엔티티를 조회한다")
    fun findByEmail_Success() {
        val entity = FcmTokenJpaEntity(token = token, userEmail = email, deviceType = device)
        val domain =
            FcmToken(id = 1L, fcmToken = token, userEmail = email, deviceType = device, updatedAt = LocalDateTime.now())

        `when`(repository.findByUserEmail(email)).thenReturn(entity)
        `when`(fcmTokenMapper.mapToDomainEntity(entity)).thenReturn(domain)

        val result = adapter.findByUserEmail(email)

        assert(result?.fcmToken == token)
        verify(fcmTokenMapper).mapToDomainEntity(entity)
    }

    @Test
    @DisplayName("ID를 통해 토큰 정보를 삭제한다")
    fun deleteById_Success() {
        val tokenId = 1L

        adapter.deleteById(tokenId)

        verify(repository).deleteById(tokenId)
    }
}