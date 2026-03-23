package com.kdongsu5509.notifications.adapter.out.solapi

import com.kdongsu5509.notifications.application.port.out.ExternalMessagePort
import com.kdongsu5509.notifications.config.ExternalSMSProperties
import com.kdongsu5509.notifications.domain.SMS
import com.solapi.sdk.message.dto.response.MultipleDetailMessageSentResponse
import com.solapi.sdk.message.exception.SolapiBadRequestException
import com.solapi.sdk.message.exception.SolapiInvalidApiKeyException
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException
import com.solapi.sdk.message.model.Message
import com.solapi.sdk.message.service.DefaultMessageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class SolapiAdapter(
    private val externalSMSProperties: ExternalSMSProperties,
    private val solapiService: DefaultMessageService,
) : ExternalMessagePort {

    companion object {
        const val MSG_FORMAT = "안전하게 도착하였습니다.\n\n" +
                "보낸 분 : %s\n" +
                "장소: %s\n" +
                "시간: %s\n\n" +
                "Service by ImHere"
    }

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun send(sms: SMS): SolapiResponse {
        val message = buildExternalSMSMessage(sms)
        return try {
            solapiService.send(message, null)
            SolapiResponse.success()
        } catch (e: Exception) {
            handleException("단일 발송", e)
        }
    }

    override fun sendMultiple(multiSMS: List<SMS>): List<SolapiResponse> {
        if (multiSMS.isEmpty()) {
            log.warn("[Solapi 다중 발송] 발송할 대상이 없습니다.")
            return emptyList()
        }

        val messages = multiSMS.map { buildExternalSMSMessage(it) }
        return try {
            val result: MultipleDetailMessageSentResponse = solapiService.send(messages, null)

            val detailList = result.messageList
            if (detailList.isNullOrEmpty()) {
                return List(multiSMS.size) { SolapiResponse.fail("Response list is empty") }
            }

            detailList.map { detail ->
                if (detail.statusCode == "200" || detail.statusCode == "4000") {
                    SolapiResponse.success()
                } else {
                    SolapiResponse.fail(detail.statusMessage ?: "Unknown Error")
                }
            }
        } catch (e: Exception) {
            val failResponse = handleException("다중 발송", e)
            List(multiSMS.size) { failResponse }
        }
    }

    private fun buildExternalSMSMessage(sms: SMS): Message {
        return Message(
            from = externalSMSProperties.sender,
            to = sms.receiverNumber,
            text = String.format(
                MSG_FORMAT,
                sms.senderNickname,
                sms.location,
                getCurrentTimeInformation()
            )
        )
    }

    private fun getCurrentTimeInformation(): String {
        return LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("a h시 m분").withLocale(Locale.KOREAN)
        )
    }

    private fun handleException(type: String, e: Exception): SolapiResponse {
        when (e) {
            is SolapiBadRequestException -> "잘못된 요청: ${e.message}"
            is SolapiInvalidApiKeyException -> "잘못된 API 키: ${e.message}"
            is SolapiMessageNotReceivedException -> "발송 미접수: ${e.message}"
            else -> "시스템 오류: ${e.message}"
        }
        return SolapiResponse.fail(e.message ?: "Internal Error")
    }
}