# Docker 파일 역할 정리

이 프로젝트의 Docker 관련 파일은 **빌드용 Dockerfile 2개**와 **용도별 Compose 파일 5개**로 구성됩니다.

---

## Dockerfile 계열

### `Dockerfile`
**용도: 로컬 개발 / 테스트용 올인원 빌드**

```
amazoncorretto:21-alpine-jdk (builder)
  └── gradlew build -x test
        └── amazoncorretto:21-alpine-jdk
              └── app.jar → ENTRYPOINT
```

Gradle 빌드를 Docker 내부에서 직접 수행하는 멀티스테이지 빌드입니다.  
GitHub Actions 없이 로컬에서 `docker build .` 한 번으로 이미지를 만들 때 사용합니다.  
빌드 환경을 별도로 구성하지 않아도 되지만, 빌드 캐시를 활용하기 어려워 CI/CD에는 적합하지 않습니다.

---

### `Dockerfile.release`
**용도: CI/CD 배포용 경량 이미지 빌드**

```
(GitHub Actions에서 ./gradlew bootJar 로 JAR 사전 빌드)
  └── amazoncorretto:21-alpine-jdk
        └── build/libs/*.jar → app.jar → ENTRYPOINT
```

GitHub Actions가 `./gradlew bootJar`로 JAR를 먼저 만들고, 이 Dockerfile은 그 결과물만 이미지에 복사합니다.  
빌드 도구(Gradle, JDK source)가 최종 이미지에 포함되지 않아 이미지 크기가 작습니다.  
**CD 워크플로우(`.github/workflows/cd.yml`)에서만 사용합니다.**

---

## docker-compose 계열

| 파일 | 대상 서버 | 포함 서비스 | 주 사용자 |
|---|---|---|---|
| `docker-compose.yml` | 로컬 | App + Prometheus + Grafana | 개발자 로컬 |
| `docker-compose.prod.yml` | 앱 서버 EC2 | App + Prometheus | CD 자동 배포 |
| `docker-compose.infra.yml` | 인프라 서버 EC2 | Redis + RabbitMQ | 인프라 수동 관리 |
| `docker-compose.db.yml` | DB 서버 EC2 | PostgreSQL | 인프라 수동 관리 |

---

### `docker-compose.yml`
**용도: 로컬 개발 환경 전체 구동**

포함 서비스: `app` · `prometheus` · `grafana`

로컬에서 Spring 앱, Prometheus, Grafana를 한 번에 띄워 전체 스택을 확인할 때 사용합니다.  
Grafana가 포함되어 있어 `localhost:3000`에서 대시보드를 바로 확인할 수 있습니다.  
운영 서버에는 배포하지 않습니다.

```bash
# 사용법
docker compose up -d
```

---

### `docker-compose.prod.yml`
**용도: 운영 배포 (앱 서버 EC2 전용)**

포함 서비스: `dsko`(Spring) · `prometheus`

CD 파이프라인(`.github/workflows/cd.yml`)이 SSH로 EC2에 접속해 이 파일을 실행합니다.  
이미지는 ECR에서 풀링하며, Grafana는 **Grafana Cloud**로 대체되므로 포함되지 않습니다.  
서비스명 `dsko`는 `prometheus.yml`의 스크랩 타겟(`dsko:4861`)과 일치시킨 이름입니다.

```bash
# CD에서 실행하는 명령 (참고용)
docker compose -f docker-compose.prod.yml pull dsko
docker compose -f docker-compose.prod.yml up -d --no-deps dsko
```

---

### `docker-compose.infra.yml`
**용도: 인프라 서버 EC2 — Redis + RabbitMQ 관리**

포함 서비스: `redis` · `rabbitmq`

앱 서버와 분리된 전용 EC2에서 수동으로 관리합니다.  
Redis는 비밀번호 인증, RabbitMQ는 `RABBITMQ_DEFAULT_VHOST=/main` 으로 가상 호스트를 자동 생성합니다.  
RabbitMQ 관리 UI(`15672`)는 `127.0.0.1`에만 바인딩되어 외부 직접 접근이 차단됩니다.

```bash
# 사용법 (인프라 서버에서)
docker compose -f docker-compose.infra.yml up -d
```

---

### `docker-compose.db.yml`
**용도: DB 서버 EC2 — PostgreSQL 관리**

포함 서비스: `postgres`

PostgreSQL을 독립 EC2에서 운영할 때 사용합니다.  
포트 `5432`는 보안 그룹에서 앱 서버 IP만 허용해 외부 노출을 차단합니다.  
RDS를 사용한다면 이 파일은 불필요합니다.

```bash
# 사용법 (DB 서버에서)
docker compose -f docker-compose.db.yml up -d
```

---

## 서버 간 구성 요약

```
[로컬 개발]
  docker-compose.yml
  └── app + prometheus + grafana (로컬 전체 스택)

[운영 — 앱 서버 EC2]
  docker-compose.prod.yml
  └── dsko(Spring) + prometheus
        └── prometheus → remote_write → Grafana Cloud

[운영 — 인프라 서버 EC2]
  docker-compose.infra.yml
  └── redis + rabbitmq

[운영 — DB 서버 EC2 / RDS]
  docker-compose.db.yml (EC2 직접 운영 시만 사용)
  └── postgres
```

---

## 파일별 `.env` 변수 요구사항

### `docker-compose.yml` (로컬)
```dotenv
INFRA_HOST=localhost
DB_HOST=localhost
DB_NAME=imhere
DB_USER=postgres
DB_PASSWORD=...
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest
GRAFANA_USER=admin
GRAFANA_PASSWORD=admin
```

### `docker-compose.prod.yml` (앱 서버 EC2)
```dotenv
ECR_REGISTRY=<계정ID>.dkr.ecr.ap-northeast-2.amazonaws.com
ECR_REPOSITORY=imhere
INFRA_HOST=<인프라 서버 프라이빗 IP>
DB_HOST=<DB 호스트>
DB_NAME=imhere
DB_USER=...
DB_PASSWORD=...
RABBITMQ_USER=...
RABBITMQ_PASSWORD=...
```

### `docker-compose.infra.yml` (인프라 서버 EC2)
```dotenv
REDIS_PASSWORD=...
RABBITMQ_USER=imhere
RABBITMQ_PASSWORD=...
```

### `docker-compose.db.yml` (DB 서버 EC2)
```dotenv
DB_NAME=imhere
DB_USER=...
DB_PASSWORD=...
```
