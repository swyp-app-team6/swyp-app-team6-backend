# 프로젝트 컨벤션 및 아키텍처 규칙

이 문서는 팀 고유의 컨벤션과 아키텍처 결정을 담는다.

## 기술 스택

- **프레임워크**: Spring Boot 3+
- **언어**: Java 25
- **빌드 도구**: Gradle
- **데이터베이스**: MySQL
- **테스트**: JUnit 5, RestAssured

## 팀 개발 철학

### 기본 원칙

- 기본 컨벤션은 **우아한테크코스 컨벤션**을 따른다
- 팀 커스텀 컨벤션은 **페어 프로그래밍 및 PR 코드 리뷰 합의**를 통해 점진적으로 도입한다
- 기술은 **단순하고 최소한으로** 적용하며, 도입 전 **팀 논의 및 코치 리뷰**를 거친다
- 빠른 개발을 우선하고, **리팩토링 데이**를 통해 품질을 개선한다
- 객체지향 생활 체조 원칙을 지향한다

## 패키지 구조

```
org.swyp.com.backend
├── {domain}
│   ├── service    // Service + DTO (request/response)
│   ├── controller             // Controller + ApiSpec 인터페이스
│   ├── domain         // Entity + Repository
│   └── infrastructure // 외부 시스템 연동 (선택)
└── global             // 공통 기능
```

## 아키텍처 규칙

### 의존성 방향

```
     UI Layer (Controller)
            ↓
    Application Layer (Service + DTO)
            ↓
      Domain Layer (Entity + Repository)
            ↑
  Infrastructure Layer (외부 시스템 연동)
```

### 계층별 책임

- **UI Layer**: HTTP 요청/응답 처리, 검증, DTO 변환
- **Application Layer**: 비즈니스 워크플로우 조율, 트랜잭션 관리
- **Domain Layer**: 핵심 비즈니스 로직, 도메인 규칙, 엔티티 관리
- **Infrastructure Layer**: 외부 시스템 연동, 영속성 구현

## 네이밍 컨벤션

### 기본 규칙

- 변수/메서드: `camelCase`
- 클래스: `PascalCase`
- 상수: `UPPER_SNAKE_CASE`
- 패키지: `lowercase`

### 세부 규칙

- Boolean 변수: `isSomething` 형태
- JPA 네이밍과 도메인 네이밍 분리: `find` (DB 작업), `get` (도메인 작업)
- 예외 클래스: `SomethingException`
- DTO 클래스: `SomethingRequest`, `SomethingResponse`

## DTO 관리 규칙

### 위치 및 구조

- **DTO는 application 패키지에 위치**
- **DTO 변환은 Service → Controller 구간에서 수행**
- 일급 컬렉션 미사용 (복잡도 증가 방지)
- 변환 로직은 정적 팩토리 메서드 사용 (`of()`, `from()`)

### 타입 규칙

- **Request DTO**: 래퍼 타입 사용 (`Long`, `Integer`) — null로 필수/선택 필드 구분
- **Response DTO**: 원시 타입 사용 (`long`, `int`) — null 안전성 보장
- **내부 DTO**: null 안전성 요구에 따라 선택

### 검증 규칙

- Bean Validation 어노테이션 사용 (`@NotNull`, `@Valid` 등)
- 복잡한 비즈니스 규칙은 커스텀 검증기 사용

## 테스트 기준

### 테스트 범위

- 기본적으로 도메인 및 서비스 레이어를 테스트한다
- 직접 쿼리를 작성한 경우에만 Repository 테스트
- 빠른 개발을 위해 인수/E2E 테스트는 RestAssured 사용 (Piece + Scenario 패턴)
- **테스트 우선 설계 고려**: 항상 테스트 관점에서 설계 결정을 평가한다

### 테스트 원칙

- **의존성 주입 원칙**: 테스트를 복잡하게 만드는 불필요한 의존성 생성 지양
- **private 메서드 선호**: 단순한 재사용 로직은 별도 클래스보다 private 메서드 선호
- **Mock 최소화**: 가능하면 Mock보다 실제 객체 사용
- **테스트 가독성**: 테스트는 의도를 명확하게 표현하고 이해하기 쉬워야 한다

### 테스트 도구 및 패턴

- 통합 테스트: `@SpringBootTest`
- 컨트롤러 테스트: `@WebMvcTest`
- 서비스/레포지토리 테스트: `@DataJpaTest` + `@Import({XxxService.class})`

## 데이터베이스 및 JPA 설정

### JPA 설정

- 모든 연관관계 기본값: `FetchType.LAZY`
- OSIV 비활성화 (`spring.jpa.open-in-view: false`)
- 페치 최적화는 `@EntityGraph` 사용
- N+1 문제는 발생 지점에서 해결

### 엔티티 규칙

- ID: `@GeneratedValue(strategy = GenerationType.IDENTITY)` 사용
- 감사 필드: `@CreatedDate`, `@LastModifiedDate` 사용
- 엔티티 관계: 상속보다 합성 선호
- 논리 삭제: `@SoftDelete` 사용

### Repository 패턴

- 기본 CRUD: `JpaRepository<Entity, ID>` 상속
- 복잡한 쿼리: `@Query` 사용, native SQL보다 JPQL 선호

## REST API 기준

### URI 설계

- RESTful URI: `/users/{id}`, `/posts/{id}/comments`
- 컬렉션은 복수 명사: `/users`, `/orders`
- 파괴적 변경 시 버전 관리: `/api/v1/users`, `/api/v2/users`

### HTTP 메서드 및 상태 코드

- `GET`: 리소스 조회 (200, 404)
- `POST`: 리소스 생성 (201, 400, 409)
- `PUT`: 리소스 전체 수정 (200, 404)
- `PATCH`: 부분 수정 (200, 404)
- `DELETE`: 리소스 삭제 (204, 404)

### 응답 처리

- 오류 응답: Spring `ProblemDetail` 사용 (RFC 7807)
- 적절한 HTTP 상태 코드 포함
- `@RestControllerAdvice` + `GlobalExceptionHandler`
- 비즈니스 오류: `BusinessException` 계층 사용

## 보안 고려사항

- 모든 입력값 검증
- XSS 방지를 위한 출력 이스케이프
- SQL 인젝션 방지를 위한 파라미터화 쿼리
- 적절한 CORS 설정
- 커스텀 JWT 기반 인증 (Spring Security 미사용)

## 성능 가이드라인

### 데이터베이스 성능

- 적절한 DB 인덱스 활용
- 느린 쿼리 모니터링 및 최적화
- N+1 문제는 발생 지점에서 해결

### 일반 성능

- 지연 로딩 적절히 활용
- 대용량 데이터는 페이지네이션 적용
- Prometheus + Actuator로 애플리케이션 메트릭 모니터링

## 코드 품질 기준

### 코드 스타일

- Google Java Style Guide 기준
- 의미 있는 변수명 및 메서드명 사용
- 메서드와 클래스는 작고 집중되게 유지

### 코드 리뷰 프로세스

- 모든 코드 변경은 PR 리뷰 필수
- 설계, 가독성, 정확성에 집중
- 새 기능에는 반드시 테스트 포함

## 환경 설정

### 프로필

- `local`: 로컬 개발 환경
- `dev`: 개발 서버 환경
- `prod`: 프로덕션 환경
- `test`: 테스트 환경
- `stress`: 부하 테스트 환경

### 설정 관리

- `application.properties`로 설정 관리
- 환경별 값은 Spring 프로필로 외부화

## 모니터링 및 로깅

### 로깅 기준

- SLF4J + Logback + Logstash JSON 인코더 사용
- 로그 레벨: ERROR (5xx), WARN (4xx 인증), INFO (비즈니스 이벤트)
- 프로덕션: 구조화된 JSON 로깅
- `@BusinessLogging` AOP로 비즈니스 행동 추적

### 모니터링 요구사항

- Spring Actuator 헬스 체크
- Micrometer + Prometheus 메트릭 수집
- `@BusinessLogging` AOP로 비즈니스 메트릭 수집
