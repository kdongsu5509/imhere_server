package com.kdongsu5509.notifications.domain

enum class FCMMessageTitle(
    val content: String,
) {
    FRIEND_REQUEST("새로운 친구 요청"),
    LOCATION_SHARE_RECIPIENT("위치 공유 수신자 지정"),
    ARRIVAL_CONFIRMATION("목적지 도착 안내"),
    DELIVERY_RESULT_NOTICE("전송 결과 안내"),
    DEFAULT_NOTICE("IMHERE 안내")
}