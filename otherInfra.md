# 외부 인프라 설정 가이드

---

## 1. Grafana Cloud 설정

Prometheus는 EC2 앱 서버에서 `127.0.0.1:9090`으로만 노출되므로, 외부 Grafana에서 직접 스크랩하는 것이 불가능합니다.  
Prometheus의 **`remote_write`** 기능을 사용해 수집된 메트릭을 Grafana Cloud로 밀어 넣는 방식을 사용합니다.

### 1-1. Grafana Cloud 계정 및 스택 생성

1. [grafana.com](https://grafana.com) 접속 → **Create free account** 로 가입
2. 로그인 후 **My Account → + Add stack** 클릭
3. 스택 이름 입력(예: `imhere`) 후 리전 선택 → **Create stack**

### 1-2. Prometheus Remote Write 엔드포인트 확인

1. Grafana Cloud 포털 → 생성한 스택 → **Details** 클릭
2. **Prometheus** 섹션에서 아래 정보를 복사합니다.
    - **Remote Write Endpoint** (예: `https://prometheus-prod-XX-prod-XX.grafana.net/api/prom/push`)
    - **Username / Instance ID** (숫자, 예: `123456`)
3. **Generate now** 버튼으로 **API Token** 발급 (권한: `MetricsPublisher`)

### 1-3. EC2의 prometheus.yml 수정

`EC2_DEPLOY_PATH/prometheus.yml` 하단에 `remote_write` 블록을 추가합니다.

```yaml
global:
  scrape_interval: 30s
  evaluation_interval: 30s

scrape_configs:
  - job_name: "imhere"
    metrics_path: "/xCJR4cpW3VSxH8esujE2maQUJaaF6fSsPVBmMgAIcDmW9VUW9wvrrUP6jq8gF8aSVuyKfE7fEvSzkI842FH3NhBFG3UN5Zns8Vw6rz75h7dRgb2R6EMBVT6yMG8a5WDb/prometheus"
    static_configs:
      - targets: [ "dsko:4861" ]

remote_write:
  - url: "https://prometheus-prod-XX-prod-XX.grafana.net/api/prom/push"
    basic_auth:
      username: "123456"          # Grafana Cloud Instance ID
      password: "<API_TOKEN>"     # MetricsPublisher 권한 토큰
```

수정 후 Prometheus를 reload합니다.

```bash
# Hot reload (재시작 없이 설정 반영)
curl -X POST http://localhost:9090/-/reload
```

### 1-4. Grafana Cloud에서 대시보드 구성

1. Grafana Cloud 포털 → 스택의 **Launch** 클릭 (Grafana UI 접속)
2. 왼쪽 메뉴 **Connections > Data sources** → **Add data source**
3. **Prometheus** 선택 후 아래 설정 입력
    - **URL**: 스택의 Prometheus Query Endpoint (Details 페이지에서 확인)
    - **Auth**: Basic Auth ON
        - User: Instance ID
        - Password: API Token (권한: `MetricsViewer`)
4. **Save & test** 클릭
5. **Dashboards → New → Import** 에서 Spring Boot 공식 대시보드 ID `19004` 또는 `12900` 입력 후 Import

### 1-5. Grafana Cloud Alert 설정 (선택)

1. Grafana UI → **Alerting > Alert rules > New alert rule**
2. 데이터 소스: Grafana Cloud Prometheus
3. 예시 조건: `jvm_memory_used_bytes > 500000000` (힙 메모리 500MB 초과 시 알림)
4. **Notifications** 탭에서 Discord/Slack 웹훅 연결

---

## 2. 인프라 서버 (Redis + RabbitMQ) EC2 설정

Redis와 RabbitMQ는 앱 서버와 분리된 전용 EC2에서 `docker-compose.infra.yml`로 관리합니다.

### 2-1. 인스턴스 사양

| 항목      | 권장값                                         |
|---------|---------------------------------------------|
| AMI     | Amazon Linux 2023                           |
| 인스턴스 유형 | `t3.small` (Redis + RabbitMQ 동시 운영 기준)      |
| 스토리지    | 20GB gp3                                    |
| 탄력적 IP  | 할당 권장 (앱 서버 `.env`의 `INFRA_HOST`에 고정 IP 사용) |

### 2-2. 보안 그룹 설정

**인프라 서버 보안 그룹**

| 유형    | 프로토콜 | 포트    | 소스            | 설명                  |
|-------|------|-------|---------------|---------------------|
| 인바운드  | TCP  | 22    | 관리자 IP        | SSH 관리용             |
| 인바운드  | TCP  | 6379  | 앱 서버 보안 그룹 ID | Redis               |
| 인바운드  | TCP  | 5672  | 앱 서버 보안 그룹 ID | RabbitMQ AMQP       |
| 인바운드  | TCP  | 15672 | 관리자 IP        | RabbitMQ 관리 UI (선택) |
| 아웃바운드 | 전체   | 전체    | 0.0.0.0/0     | 기본값                 |

> 포트 6379, 5672 의 소스를 `0.0.0.0/0`이 아닌 **앱 서버 보안 그룹 ID**로 지정해 외부 노출을 차단합니다.

### 2-3. 소프트웨어 설치

```bash
# Docker 및 Compose 설치 (앱 서버와 동일)
sudo yum update -y
sudo yum install -y docker
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker ec2-user

sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 \
  -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose
```

### 2-4. 배포 디렉토리 구성

```bash
mkdir -p /home/ec2-user/infra
cd /home/ec2-user/infra

# docker-compose.infra.yml 업로드 (프로젝트 루트에서 scp)
scp -i <pem-key>.pem docker-compose.infra.yml ec2-user@<INFRA_HOST>:/home/ec2-user/infra/
```

### 2-5. `.env` 파일 생성

```bash
cat > /home/ec2-user/infra/.env << 'EOF'
REDIS_PASSWORD=<강력한_비밀번호>
RABBITMQ_USER=imhere
RABBITMQ_PASSWORD=<강력한_비밀번호>
EOF

chmod 600 /home/ec2-user/infra/.env
```

### 2-6. 컨테이너 시작

```bash
cd /home/ec2-user/infra
docker compose -f docker-compose.infra.yml up -d

# 상태 확인
docker compose -f docker-compose.infra.yml ps
docker logs redis-container
docker logs rabbitmq-container
```

### 2-7. RabbitMQ Virtual Host 확인

RabbitMQ는 기본 가상 호스트(`/main`)를 `RABBITMQ_DEFAULT_VHOST` 환경변수로 자동 생성합니다.  
컨테이너 시작 후 아래 명령으로 확인합니다.

```bash
docker exec rabbitmq-container rabbitmqctl list_vhosts
# 출력에 /main 이 있어야 합니다.
```

관리 UI 접근(SSH 터널 이용):

```bash
# 로컬 머신에서
ssh -L 15672:localhost:15672 -i <pem-key>.pem ec2-user@<INFRA_HOST>
# 브라우저에서 http://localhost:15672 접속
```

### 2-8. 앱 서버 `.env` 업데이트

인프라 서버 구성 완료 후 앱 서버(`EC2_DEPLOY_PATH/.env`)의 `INFRA_HOST`를 인프라 서버의 **프라이빗 IP**로 설정합니다.  
같은 VPC 내에서는 프라이빗 IP 통신이 빠르고 비용이 발생하지 않습니다.

```dotenv
INFRA_HOST=10.0.x.x   # 인프라 EC2 프라이빗 IP
```

### 2-9. 데이터 영속성 확인

`docker-compose.infra.yml`은 named volume(`redis_data`, `rabbitmq_data`)을 사용합니다.  
EC2 재시작 시 `restart: unless-stopped` 옵션으로 컨테이너가 자동으로 재시작됩니다.

```bash
# 볼륨 목록 확인
docker volume ls

# 볼륨 상세 정보 (마운트 경로)
docker volume inspect infra_redis_data
docker volume inspect infra_rabbitmq_data
```

---

## 3. 전체 아키텍처 요약

```
[GitHub Actions CI/CD]
        │
        │ Docker Image Push
        ▼
[Amazon ECR]
        │
        │ docker pull (SSH 배포)
        ▼
[앱 서버 EC2]
  ├── dsko (Spring Boot :8080/:4861)
  └── prometheus (:9090, localhost only)
        │
        │ remote_write
        ▼
[Grafana Cloud]
  └── Dashboard / Alerting

[앱 서버 EC2] ──TCP 6379──► [인프라 서버 EC2]
                ──TCP 5672──►   ├── redis
                                └── rabbitmq

[앱 서버 EC2] ──JDBC──► [PostgreSQL (RDS 또는 별도 EC2)]
```
