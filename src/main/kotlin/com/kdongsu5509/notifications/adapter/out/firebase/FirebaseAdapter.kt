package com.kdongsu5509.notifications.adapter.out.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.kdongsu5509.notifications.application.port.out.FirebasePort
import org.springframework.stereotype.Component

@Component
class FirebaseAdapter : FirebasePort {

    private val firebaseMessaging: FirebaseMessaging

    constructor(firebaseMessaging: FirebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging
    }

    private val title = "전송 완료"
    private val body = "문자 메시지 발송에 성공하였습니다"

    override fun send(fcmToken: String) {
        val fcmMessage = createFcmMessage(fcmToken)
        firebaseMessaging.send(fcmMessage)
    }

    private fun createFcmMessage(
        fcmToken: String
    ): Message? {
        return Message.builder().setNotification(
            Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build()
        )
            .setToken(fcmToken)
            .build()
    }
}


///오류 코드	설명 및 해결 단계
//UNSPECIFIED_ERROR이 오류에 대한 추가 정보는 없습니다.	없음.
//INVALID_ARGUMENT(HTTP 오류 코드 = 400) 요청 매개변수가 유효하지 않습니다. 어떤 필드가 유효하지 않은지 지정하는 유형의 확장 정보가 반환됩니다.google.rpc.BadRequest	잠재적 원인으로는 잘못된 등록, 잘못된 패키지 이름, 메시지 크기 초과, 잘못된 데이터 키, 잘못된 TTL 또는 기타 잘못된 매개변수가 있습니다.
//잘못된 등록 : 서버에 전달하는 등록 토큰의 형식을 확인하세요. 클라이언트 앱이 FCM에 등록할 때 받는 등록 토큰과 일치하는지 확인하세요. 토큰을 자르거나 추가 문자를 넣지 마세요.
//잘못된 패키지 이름 : 메시지가 요청에 전달된 값과 일치하는 패키지 이름을 가진 등록 토큰으로 전송되었는지 확인하세요.
//메시지 크기 초과 : 메시지에 포함된 페이로드 데이터의 총 크기가 FCM 제한(대부분의 메시지는 4096바이트, 토픽 메시지는 2048바이트)을 초과하지 않는지 확인하세요. 여기에는 키와 값이 모두 포함됩니다.
//잘못된 데이터 키 : ​​페이로드 데이터에 FCM에서 내부적으로 사용하는 키(예: from, gcm 또는 google로 시작하는 값)가 포함되어 있지 않은지 확인하세요. 일부 단어(예: collapse_key)는 FCM에서도 사용되지만 페이로드에 포함될 수 있습니다. 이 경우 페이로드 값은 FCM 값으로 덮어쓰여집니다.
//잘못된 TTL : ttl에 사용된 값이 0에서 2,419,200(4주) 사이의 기간을 나타내는 정수인지 확인하십시오.
//잘못된 매개변수 : 제공된 매개변수의 이름과 유형이 올바른지 확인하십시오.
//UNREGISTERED(HTTP 오류 코드 = 404) 앱 인스턴스가 FCM에서 등록 해제되었습니다. 이는 일반적으로 사용 중인 토큰이 더 이상 유효하지 않으므로 새 토큰을 사용해야 함을 의미합니다.	이 오류는 등록 토큰이 누락되었거나 등록되지 않은 토큰으로 인해 발생할 수 있습니다.
//등록 누락 : 메시지의 대상이 값인 경우 token요청에 등록 토큰이 포함되어 있는지 확인하십시오.
//등록되지 않음 : 기존 등록 토큰은 다음과 같은 여러 시나리오에서 더 이상 유효하지 않을 수 있습니다
//. - 클라이언트 앱이 FCM에 등록을 해제한 경우
//- 사용자가 애플리케이션을 제거하는 경우처럼 클라이언트 앱이 자동으로 등록 해제된 경우 (예: iOS에서 APNs 피드백 서비스가 APNs 토큰이 유효하지 않다고 보고한 경우)
//- 등록 토큰이 만료된 경우 (예: Google에서 등록 토큰을 갱신하거나 iOS 기기의 APNs 토큰이 만료된 경우)
//- 클라이언트 앱이 업데이트되었지만 새 버전이 메시지를 수신하도록 구성되지 않은 경우
//이러한 모든 경우에 앱 서버에서 해당 등록 토큰을 제거하고 메시지 전송에 사용하지 마십시오.
//SENDER_ID_MISMATCH(HTTP 오류 코드 = 403) 인증된 발신자 ID가 등록 토큰의 발신자 ID와 다릅니다.	등록 토큰은 특정 발신자 그룹에 연결되어 있습니다. 클라이언트 앱이 FCM에 등록할 때 메시지를 보낼 수 있는 발신자를 지정해야 합니다. 클라이언트 앱으로 메시지를 보낼 때는 이러한 발신자 ID 중 하나를 사용해야 합니다. 다른 발신자로 변경하면 기존 등록 토큰은 더 이상 작동하지 않습니다.
//QUOTA_EXCEEDED(HTTP 오류 코드 = 429) 메시지 대상에 대한 전송 제한을 초과했습니다. 초과된 할당량을 지정하는 유형의 확장 정보가 반환됩니다.google.rpc.QuotaFailure	이 오류는 메시지 전송률 할당량 초과, 장치 메시지 전송률 할당량 초과 또는 토픽 메시지 전송률 할당량 초과로 인해 발생할 수 있습니다.
//메시지 전송률 초과 : 메시지 전송률이 너무 높습니다. 전체 메시지 전송률을 줄여야 합니다. 거부된 메시지를 재시도하려면 최소 1분의 초기 지연 시간을 두고 지수 백오프를 사용하십시오.
//장치 메시지 전송률 초과 : 특정 장치로 전송되는 메시지 전송률이 너무 높습니다. 단일 장치에 대한 메시지 전송률 제한을 참조하십시오 . 해당 장치로 전송되는 메시지 수를 줄이고 지수 백오프를 사용하여 재시도하십시오.
//토픽 메시지 전송률 초과 : 특정 토픽 구독자에게 전송되는 메시지 전송률이 너무 높습니다. 해당 토픽으로 전송되는 메시지 수를 줄이고 최소 1분의 초기 지연 시간을 두고 지수 백오프를 사용하여 재시도하십시오.
//UNAVAILABLE(HTTP 오류 코드 = 503) 서버에 과부하가 걸렸습니다.	서버가 요청을 제때 처리하지 못했습니다. 동일한 요청을 다시 시도하되, 다음 사항을 준수해야 합니다.
//- FCM 연결 서버의 응답에 Retry-After 헤더가 포함된 경우 이를 준수하십시오.
//- 재시도 메커니즘에 지수 백오프를 구현하십시오. (예: 첫 번째 재시도 전에 1초를 기다렸다면 다음 재시도 전에는 최소 2초, 그 다음에는 4초 등으로 대기 시간을 늘려가십시오.) 여러 메시지를 전송하는 경우 지터링을 적용하는 것을 고려하십시오. 자세한 내용은 재시도 처리(Handling retries)를 참조 하거나 FCM 상태 대시보드에서 FCM에 영향을 미치는 서비스 중단이 있는지 확인하십시오. 문제를 일으키는 발신자는 차단 목록에 추가될 수 있습니다.
//INTERNAL(HTTP 오류 코드 = 500) 알 수 없는 내부 오류가 발생했습니다.	서버에서 요청을 처리하는 동안 오류가 발생했습니다. 재시도 처리 방법 의 안내에 따라 동일한 요청을 다시 시도하거나 FCM 상태 대시보드를 확인하여 FCM에 영향을 미치는 서비스 중단이 있는지 확인해 보세요. 오류가 계속 발생하는 경우 Firebase 지원팀에 문의하십시오.
//THIRD_PARTY_AUTH_ERROR(HTTP 오류 코드 = 401) APNs 인증서 또는 웹 푸시 인증 키가 유효하지 않거나 누락되었습니다.	iOS 기기 대상 메시지 또는 웹 푸시 알림 등록을 보낼 수 없습니다. 개발 및 운영 계정 자격 증명의 유효성을 확인하십시오.