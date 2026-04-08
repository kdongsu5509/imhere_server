# ImHere API 명세서 (v1)

ImHere 서버에서 제공하는 RESTful API에 대한 명세서입니다.

## 일반 정보

- **기본 URL**: `http://localhost:8080`
- **기본 포트**: 8080
- **데이터 형식**: JSON
- **API 버전**: 모든 엔드포인트는 현재 버전 `1` (v1)을 따릅니다.

## 공통 응답 형식

모든 API 응답은 표준 래퍼 형식을 따릅니다.

### 성공 응답 (Success)
```json
{
  "code": 200,
  "message": "OK",
  "data": { ... }
}
```

### 오류 응답 (Error)
```json
{
  "code": 400,
  "message": "BAD_REQUEST",
  "data": {
    "errorCode": "G001",
    "errorMessage": "잘못된 입력 값입니다."
  }
}
```

---

## 1. 인증 (Authentication)

ID 토근을 이용한 로그인 및 JWT 토큰 재발급을 관리합니다.

### 1-1. OIDC 로그인
OAuth2 제공자(예: KAKAO, APPLE)로부터 받은 ID 토큰으로 로그인을 시도합니다.

- **URL**: `/api/v1/user/auth/login`
- **Method**: `POST`
- **요청 본문 (Request Body)**:
  ```json
  {
    "provider": "KAKAO",
    "idToken": "ey..."
  }
  ```
- **응답**: `accessToken`과 `refreshToken`이 포함된 `AuthenticationResponse`를 반환합니다.

### 1-2. 토큰 재발급
유효한 Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.

- **URL**: `/api/v1/user/auth/reissue`
- **Method**: `POST`
- **요청 본문 (Request Body)**:
  ```json
  {
    "refreshToken": "ey..."
  }
  ```

---

## 2. 사용자 정보 (User Information)

사용자 프로필 관리 및 검색 관련 API입니다.

### 2-1. 내 정보 조회
- **URL**: `/api/v1/user/info/me`
- **Method**: `GET`
- **인증 필요**: 예

### 2-2. 닉네임 변경
- **URL**: `/api/v1/user/info/nickname`
- **Method**: `POST`
- **요청 본문 (Request Body)**:
  ```json
  {
    "newNickname": "고동수"
  }
  ```

### 2-3. 사용자 검색
닉네임 또는 이메일로 다른 사용자를 검색합니다.

- **URL**: `/api/v1/user/info/{keyword}`
- **Method**: `GET`
- **URL 파라미터**: `keyword` (String)

---

## 3. 친구 관리 (Friends Management)

### 3-1. 친구 목록 조회
- **URL**: `/api/v1/user/friends`
- **Method**: `GET`

### 3-2. 친구 별칭 수정
- **URL**: `/api/v1/user/friends/alias`
- **Method**: `POST`
- **요청 본문**:
  ```json
  {
    "friendRelationshipId": "uuidv4",
    "newFriendAlias": "별칭이름"
  }
  ```

### 3-3. 친구 차단
- **URL**: `/api/v1/v1/user/friends/block/{friendRelationshipId}`
- **Method**: `POST`

### 3-4. 친구 삭제
- **URL**: `/api/v1/user/friends/{friendRelationshipId}`
- **Method**: `DELETE`

---

## 4. 친구 요청 (Friend Requests)

### 4-1. 받은 친구 요청 조회
- **URL**: `/api/v1/user/friends/request`
- **Method**: `GET`

### 4-2. 요청 상세 조회
- **URL**: `/api/v1/user/friends/request/{requestId}`
- **Method**: `GET`

### 4-3. 친구 요청 보내기
- **URL**: `/api/v1/user/friends/request`
- **Method**: `POST`
- **요청 본문**:
  ```json
  {
    "receiverId": "uuid",
    "receiverEmail": "user@example.com",
    "message": "친구 해요!"
  }
  ```

### 4-4. 친구 요청 수락
- **URL**: `/api/v1/user/friends/request/accept/{requestId}`
- **Method**: `POST`

### 4-5. 친구 요청 거절
- **URL**: `/api/v1/user/friends/request/reject/{requestId}`
- **Method**: `POST`

---

## 5. 알림 (FCM Push)

### 5-1. FCM 토큰 등록
기기의 FCM 토큰을 서버에 등록합니다.

- **URL**: `/api/v1/notification/fcmToken`
- **Method**: `POST`
- **요청 본문**:
  ```json
  {
    "fcmToken": "token-value",
    "deviceType": "ANDROID/IOS"
  }
  ```

### 5-2. 일반 알림 전송
- **URL**: `/api/v1/notification/fcm/send`
- **Method**: `POST`
- **요청 본문**:
  ```json
  {
    "receiverEmail": "receiver@example.com",
    "type": "MESSAGE",
    "body": "알림 내용"
  }
  ```

---

## 6. 약관 및 동의 (Terms)

### 6-1. 약관 종류 조회
- **URL**: `/api/v1/user/terms`
- **Method**: `GET`

### 6-2. 특정 약관 내용 조회
- **URL**: `/api/v1/user/terms/version/{termDefinitionId}`
- **Method**: `GET`

### 6-3. 약관 전체 동의
- **URL**: `/api/v1/user/terms/consent`
- **Method**: `POST`
- **요청 본문**:
  ```json
  {
    "termVersionIds": [1, 2, 3]
  }
  ```
