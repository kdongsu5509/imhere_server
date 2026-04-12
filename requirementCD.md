# CD 파이프라인 설정 요구사항

CD 워크플로우(`.github/workflows/cd.yml`)가 정상 동작하려면 아래 AWS 인프라 및 GitHub 설정이 사전 완료되어야 합니다.

---

## 1. GitHub Repository Secrets

GitHub 저장소 → **Settings > Secrets and variables > Actions** 에서 아래 시크릿을 등록합니다.

### AWS 자격증명

| Secret 이름 | 설명 | 예시 값 |
|---|---|---|
| `AWS_REGION` | ECR/EC2가 위치한 AWS 리전 | `ap-northeast-2` |
| `AWS_ACCESS_KEY_ID` | IAM 사용자 액세스 키 ID | `AKIA...` |
| `AWS_SECRET_ACCESS_KEY` | IAM 사용자 시크릿 액세스 키 | `wJalr...` |
| `AWS_ECR_REGISTRY` | ECR 레지스트리 URL | `518033442106.dkr.ecr.ap-northeast-2.amazonaws.com` |
| `AWS_ECR_REPOSITORY` | ECR 리포지토리 이름 | `imhere` |

### EC2 배포 정보

| Secret 이름 | 설명 | 예시 값 |
|---|---|---|
| `EC2_HOST` | EC2 퍼블릭 IP 또는 도메인 | `13.124.xxx.xxx` |
| `EC2_USER` | EC2 SSH 사용자명 | `ec2-user` |
| `EC2_SSH_PRIVATE_KEY` | EC2 접속용 PEM 키 전체 내용 | `-----BEGIN RSA PRIVATE KEY-----\n...` |
| `EC2_DEPLOY_PATH` | EC2의 배포 디렉토리 절대경로 | `/home/ec2-user/imhere` |
| `EC2_SECURITY_GROUP_ID` | EC2에 연결된 보안 그룹 ID | `sg-0abc12345def67890` |

### 애플리케이션 시크릿 파일

빌드 전에 `src/main/resources/` 에 주입되는 파일들입니다. 파일 내용 전체를 Secret 값으로 등록합니다.

| Secret 이름 | 주입 위치 |
|---|---|
| `APP_DATASOURCE_YAML` | `src/main/resources/application-datasource.yaml` |
| `APP_MONITORING_YAML` | `src/main/resources/application-monitoring.yaml` |
| `APP_SECRET_YAML` | `src/main/resources/application-secret.yaml` |
| `FIREBASE_KEY_JSON` | `src/main/resources/imhereFirebaseKey.json` |

---

## 2. AWS IAM 사용자 권한

CD 워크플로우에서 사용할 IAM 사용자(또는 역할)에 아래 권한이 필요합니다.

### ECR 권한 (이미지 빌드·푸시)

```json
{
  "Effect": "Allow",
  "Action": [
    "ecr:GetAuthorizationToken",
    "ecr:BatchCheckLayerAvailability",
    "ecr:GetDownloadUrlForLayer",
    "ecr:BatchGetImage",
    "ecr:PutImage",
    "ecr:InitiateLayerUpload",
    "ecr:UploadLayerPart",
    "ecr:CompleteLayerUpload"
  ],
  "Resource": "*"
}
```

### EC2 보안 그룹 IP 관리 권한

```json
{
  "Effect": "Allow",
  "Action": [
    "ec2:AuthorizeSecurityGroupIngress",
    "ec2:RevokeSecurityGroupIngress",
    "ec2:DescribeSecurityGroups"
  ],
  "Resource": "*"
}
```

> **권장**: `Resource`를 특정 보안 그룹 ARN으로 제한하면 더 안전합니다.  
> 예: `"arn:aws:ec2:ap-northeast-2:518033442106:security-group/sg-0abc12345"`

---

## 3. Amazon ECR 설정

1. AWS 콘솔 → **ECR > Repositories > Create repository**
2. **Repository name**: `imhere` (또는 `AWS_ECR_REPOSITORY` 에 등록한 이름)
3. **Image tag mutability**: Mutable (latest 태그 덮어쓰기를 위해)
4. **Scan on push**: 선택사항 (권장: 활성화)

---

## 4. EC2 인스턴스 설정

### 4-1. 인스턴스 요구사항

- **AMI**: Amazon Linux 2023 (또는 Amazon Linux 2)
- **인스턴스 유형**: 최소 `t3.small` 이상 권장
- **스토리지**: 20GB 이상 (Docker 이미지 저장 공간 포함)

### 4-2. 필수 소프트웨어 설치

EC2에 SSH 접속 후 아래 명령어를 실행합니다.

```bash
# Docker 설치
sudo yum update -y
sudo yum install -y docker
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker ec2-user

# Docker Compose v2 설치
sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 \
  -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

# AWS CLI 설치 (ECR 로그인에 필요)
sudo yum install -y aws-cli
```

### 4-3. 배포 디렉토리 구성

```bash
mkdir -p /home/ec2-user/imhere
cd /home/ec2-user/imhere

# 필요한 파일 업로드 (로컬에서 scp 또는 직접 생성)
# - docker-compose.prod.yml
# - prometheus.yml
# - .env
```

### 4-4. `.env` 파일 구성

`EC2_DEPLOY_PATH/.env` 파일을 아래 내용으로 생성합니다.

```dotenv
# ECR
ECR_REGISTRY=518033442106.dkr.ecr.ap-northeast-2.amazonaws.com
ECR_REPOSITORY=imhere

# 인프라 서버 (Redis/RabbitMQ)
INFRA_HOST=<인프라 EC2 프라이빗 IP>

# PostgreSQL (RDS 또는 DB 전용 EC2)
DB_HOST=<DB 호스트>
DB_NAME=imhere
DB_USER=<DB 사용자>
DB_PASSWORD=<DB 비밀번호>

# RabbitMQ
RABBITMQ_USER=<RabbitMQ 사용자>
RABBITMQ_PASSWORD=<RabbitMQ 비밀번호>
```

### 4-5. EC2 IAM 역할 (ECR 풀 권한)

EC2 인스턴스에 아래 정책이 포함된 IAM 역할을 연결합니다.

```json
{
  "Effect": "Allow",
  "Action": [
    "ecr:GetAuthorizationToken",
    "ecr:BatchGetImage",
    "ecr:GetDownloadUrlForLayer",
    "ecr:BatchCheckLayerAvailability"
  ],
  "Resource": "*"
}
```

### 4-6. 보안 그룹 규칙

**앱 서버 EC2 보안 그룹 (`EC2_SECURITY_GROUP_ID`)**

| 유형 | 프로토콜 | 포트 | 소스 | 설명 |
|---|---|---|---|---|
| 인바운드 | TCP | 80 | 0.0.0.0/0 | HTTP (앱) |
| 인바운드 | TCP | 443 | 0.0.0.0/0 | HTTPS (선택) |
| 인바운드 | TCP | 22 | (CD가 자동 관리) | SSH — CD 실행 중에만 임시 허용 |
| 인바운드 | TCP | 9090 | 인프라 서버 SG 또는 IP | Prometheus (선택, Grafana Agent 사용 시 불필요) |
| 아웃바운드 | 전체 | 전체 | 0.0.0.0/0 | 기본값 |

> SSH(22) 포트는 CD 워크플로우가 배포 시작 전에 자동으로 추가하고 완료 후 제거합니다.  
> `if: always()` 조건으로 배포 실패 시에도 IP가 반드시 제거됩니다.

---

## 5. CI 워크플로우 연동 확인

CD는 `IMHERE_GITHUB_ACTION_CI` 워크플로우가 **성공(`success`)** 으로 완료될 때만 트리거됩니다.  
CI 워크플로우 파일(`.github/workflows/ci.yml`)의 `name` 필드가 정확히 `IMHERE_GITHUB_ACTION_CI` 인지 확인하세요.
