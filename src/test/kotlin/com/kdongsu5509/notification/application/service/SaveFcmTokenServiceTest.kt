package com.kdongsu5509.imhere.notification.application.service

import com.kdongsu5509.notifications.application.port.out.SaveOrUpdateTokenPersistencePort
import com.kdongsu5509.notifications.application.serivce.ManageFcmTokenService
import com.kdongsu5509.notifications.domain.DeviceType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class SaveFcmTokenServiceTest {
    @Mock
    private lateinit var saveOrUpdateTokenPersistencePort: SaveOrUpdateTokenPersistencePort

    @InjectMocks
    private lateinit var saveFcmTokenService: ManageFcmTokenService

    //-> 해당 테스트는 그냥 중간의 전달 계층이지만, 테스트 커버리지를 위해 일단은 작성하였음
    @Test
    @DisplayName("잘 저장한다.")
    fun save_success() {
        //given
        val userEmail = "dongsu@test.com"
        val fcmToken = "testFcmToken"
        val testDeviceType = DeviceType.AOS

        //when, then
        assertDoesNotThrow {
            saveFcmTokenService.save(fcmToken, userEmail, testDeviceType)
        }
    }
}