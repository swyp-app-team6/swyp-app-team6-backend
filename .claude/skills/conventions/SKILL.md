---
name: conventions
description: 백엔드 코드 컨벤션을 확인할 때 반드시 이 스킬을 사용할 것. Entity/Service/Controller/DTO/ErrorCode/Test 작성 패턴, 패키지 구조, 어노테이션 사용법, DB 마이그레이션 규칙을 제공한다. "컨벤션 알려줘", "어떻게 작성하나요", "패턴이 뭔가요", "어노테이션 어떻게 써요", "테스트 어떻게 쓰나요" 등 컨벤션 관련 질문에 트리거된다.
---

## 코드 컨벤션 빠른 참조

이 스킬은 백엔드 코드 작성 규칙을 제공한다.

### 기술 스택

- Java 25 / Spring Boot 3.5.14
- Spring Data JPA + MySQL
- Lombok (`@Getter`, `@RequiredArgsConstructor` 등)
- SpringDoc OpenAPI 2.8.8

### 핵심 원칙 요약

| 구분          | 핵심 규칙                                                                           |
|-------------|---------------------------------------------------------------------------------|
| **패키지**     | `{domain}.domain`, `{domain}.application`, `{domain}.ui` 계층 분리                  |
| **Entity**  | `BaseEntity` 상속, `@NoArgsConstructor(PROTECTED)`, `@EqualsAndHashCode(of="id")` |
| **Service** | `@Transactional(readOnly=true)` 클래스, 쓰기만 `@Transactional` 메서드 추가                |
| **DTO**     | Record 타입, `from()` static 변환 메서드, `@Schema` 문서화                                |
| **예외**      | `throw new BusinessException(ErrorCode.XXX)`                                    |
| **응답**      | `ProblemDetail` (에러), 도메인 DTO (성공)                                              |
| **테스트**     | 한글 메서드명, RestAssured                                                            |

### 상세 컨벤션 — 질문 유형별 읽기 가이드

컨벤션은 주제별로 분리되어 있다. **질문에 해당하는 파일만 읽어라.**

| 질문 유형                                | 읽을 파일                                   |
|--------------------------------------|-----------------------------------------|
| 패키지 구조, 클래스 명명                       | `references/01-structure-and-naming.md` |
| Entity, Value Object, Repository     | `references/02-domain-layer.md`         |
| Service, 트랜잭션                        | `references/03-service-layer.md`        |
| Controller, ApiSpec, DTO             | `references/04-web-layer.md`            |
| ErrorCode, 인증 어노테이션                  | `references/05-error-and-auth.md`       |
| 테스트 작성 (Piece/Scenario/Fixture/Fake) | `references/06-test-conventions.md`     |
| 로깅, DB 마이그레이션, 자주 실수하는 사례            | `references/07-operations.md`           |

전체 목차가 필요하면 `references/code-conventions.md` (인덱스 파일)를 읽어라.

팀 철학·아키텍처 원칙·성능·보안·환경 설정이 필요하면 `references/team-conventions.md`를 읽어라. 포함 내용:

- 우아한테크코스 기반 팀 개발 철학 및 원칙
- 레이어별 책임 및 의존성 방향
- DTO 관리 규칙 (Request/Response 타입 기준)
- JPA 설정 (LAZY, OSIV, N+1 처리)
- REST API URI 설계 및 HTTP 상태 코드 기준
- 환경 프로파일 (local/dev/prod/test/stress)
- 로깅 전략 및 모니터링 요구사항

### 이 스킬이 트리거되는 상황

- "Entity는 어떻게 작성하나요?"
- "Service 트랜잭션 어떻게 설정해요?"
- "DTO Record 패턴 알려줘"
- "ErrorCode 어떻게 추가해요?"
- "테스트 어떻게 작성하는지 알려줘"
- "프로젝트 컨벤션이 뭔가요?"
