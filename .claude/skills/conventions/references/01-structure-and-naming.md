# 패키지 구조 및 명명 규칙

## 1. 패키지 구조

### 도메인별 계층화

```
com.Meeting.backend.{domain}
├── domain/          # 엔티티, 값 객체, Repository 인터페이스
├── Service/     # Service, DTO (request/response)
├── dto/
│  ├── request/
│  └── response/
├── infrastructure/  # 외부 API 통합 (선택)
└── Controller/             # Controller, API Spec 인터페이스
    └── api/        # {Domain}ApiSpec.java
```

### 전역 패키지

```
org.swyp.com.global
├── auth/           # 인증 어노테이션, ArgumentResolver, Principal
├── config/         # WebConfig, SwaggerConfig, MetricsConfig
├── exception/      # ErrorCode, BusinessException, GlobalExceptionHandler
└── log/            # @BusinessLogging AOP, LogFilter, DTO
```

---

## 2. 클래스 명명 규칙

| 유형            | 패턴                        | 예시                                         |
|---------------|---------------------------|--------------------------------------------|
| Entity        | `{도메인}`                   | `Meeting`, `Rotation`                      |
| Embeddable VO | `{역할}`                    | `MeetingCode`, `RotationInfo`              |
| Repository    | `{Entity}Repository`      | `MeetingRepository`                        |
| Service       | `{Domain}Service`         | `MeetingService`                           |
| Controller    | `{Domain}Controller`      | `MeetingController`                        |
| API Spec      | `{Domain}ApiSpec`         | `MeetingApiSpec`                           |
| Request DTO   | `{Action}{Domain}Request` | `CreateMeetingRequest` 또는 `MeetingRequest` |
| Response DTO  | `{Domain}Response`        | `MeetingResponse`                          |
| Client (외부)   | `{Service}Client`         | `KakaoLoginClient`                         |
| Properties    | `{Domain}Properties`      | `KakaoLoginApiProperties`                  |
