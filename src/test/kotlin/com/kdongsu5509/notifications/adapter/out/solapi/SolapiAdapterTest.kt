package com.kdongsu5509.notifications.adapter.out.solapi


import com.kdongsu5509.notifications.adapter.out.solapi.SolapiAdapter.Companion.MSG_FORMAT
import com.kdongsu5509.notifications.config.ExternalSMSProperties
import com.kdongsu5509.notifications.domain.SMS
import com.solapi.sdk.message.model.Message
import com.solapi.sdk.message.service.DefaultMessageService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyList
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class SolapiAdapterTest {

    companion object {
        const val TEST_SENDER = "01012345678"
        const val TEST_NICKNAME = "라티"
        const val TEST_REC_NUM = "01098765432"
        const val TEST_LOC = "강남역"
    }

    @Mock
    private lateinit var externalSMSProperties: ExternalSMSProperties

    @Mock
    private lateinit var messageService: DefaultMessageService

    @InjectMocks
    private lateinit var solapiAdapter: SolapiAdapter

    @BeforeEach
    fun setUp() {
        `when`(externalSMSProperties.sender).thenReturn(TEST_SENDER)
    }

    @Test
    fun `단일 메시지 발송 테스트`() {
        val sms = SMS(
            senderNickname = TEST_NICKNAME,
            receiverNumber = TEST_REC_NUM,
            location = TEST_LOC
        )

        val message = Message(
            from = TEST_SENDER,
            to = TEST_REC_NUM,
            text = String.format(
                MSG_FORMAT, sms.senderNickname, sms.location
            )
        )

        solapiAdapter.send(sms)

        verify(messageService, times(1)).send(message)
    }

    @Test
    fun `다중 메시지 발송 테스트`() {
        val smsList = listOf(
            SMS("${TEST_NICKNAME}1", "01011112222", "서울역"),
            SMS("${TEST_NICKNAME}2", "01033334444", "판교역")
        )

        solapiAdapter.sendMultiple(smsList)

        verify(messageService).send(anyList(), any())
    }
}