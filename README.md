# storage-service-api
운영자용 파일관리 스토리지 서비스입니다.

## 요약
> 24.01.15 ~ 24.02.13 (4주)

> 운영자용 버전 관리 서비스 구축

>키오스크 기기 정보 관리, 사업자 정보 관리, apk 파일 업로드 및 다운로드, apk 버전 관리

## 사용 기술
Kotlin, SpringBoot, Spring Data JPA\
MariaDB, Gradle

## 주요 기능
- 파일 버전 관리
- apk 업로드 및 다운로드 기능, 버전 목록 조회
- 로그인 시도 10회 제한, 이메일 발송 기능
- 키오스크 정보, 비지니스 정보 검색
## 아키텍쳐
![003.jpg](docs%2F003.jpg)
![004.jpg](docs%2F004.jpg)
## 배포 방법
커멘드에서 다음 명령어를 실행
```bash
.\deploy.sh
```
### 로컬 서버 실행 시
`kiosk-update-api/`에서 아래 커맨드 입력
```bash
bash local-server-start.sh
```

