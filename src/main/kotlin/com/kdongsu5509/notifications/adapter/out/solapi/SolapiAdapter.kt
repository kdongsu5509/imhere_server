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


@Component
class SolapiAdapter(
    private val externalSMSProperties: ExternalSMSProperties,
    private val solapiService: DefaultMessageService,
) : ExternalMessagePort {

    companion object {
        const val MSG_FORMAT = "[도착 알림 서비스, ImHere]\n" +
                "보낸 사람: %s\n" +
                "내용:%s에 도착하였습니다"
    }

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun send(sms: SMS): SolapiResponse {
        val message = buildExternalSMSMessage(sms)

        try {
            var result = solapiService.send(message, null)
            log.info(result.toString())
            return SolapiResponse.success()
        } catch (e: SolapiBadRequestException) {
            log.error("잘못된 요청: {}", e.message)
            return SolapiResponse.fail(e.message!!)
        } catch (e: SolapiInvalidApiKeyException) {
            log.error("잘못된 API 키: {}", e.message)
            return SolapiResponse.fail(e.message!!)
        } catch (e: SolapiMessageNotReceivedException) {
            log.error("발송 실패: {}", e.message)
            return SolapiResponse.fail(e.message!!)
        }
    }

    override fun sendMultiple(multiSMS: List<SMS>): List<SolapiResponse> {
        val messages: MutableList<Message> = ArrayList()
        for (sms in multiSMS) {
            val msg = Message(
                from = externalSMSProperties.sender,
                to = sms.receiverNumber,
                text = String.format(
                    MSG_FORMAT, sms.senderNickname, sms.location
                )
            )
            messages.add(msg)
        }

        var result: MultipleDetailMessageSentResponse = solapiService.send(messages, null)
        var results = result.messageList.stream()
            .map { it ->
                if (it.statusCode == "200") SolapiResponse.success() else SolapiResponse.fail(it.statusMessage!!)
            }.toList()

        return results
    }

    private fun buildExternalSMSMessage(sms: SMS): Message {
        val message = Message(
            from = externalSMSProperties.sender,
            to = sms.receiverNumber,
            text = String.format(
                MSG_FORMAT, sms.senderNickname, sms.location
            )
        )
        return message
    }
}