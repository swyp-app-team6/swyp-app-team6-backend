# 백엔드 코드 컨벤션 인덱스

컨벤션은 주제별로 분리되어 있습니다. **에이전트는 필요한 파일만 읽으세요.**

## 파일 목록

| 파일                                                       | 내용                                      | 주요 독자                                  |
|----------------------------------------------------------|-----------------------------------------|----------------------------------------|
| [01-structure-and-naming.md](01-structure-and-naming.md) | 패키지 구조, 클래스 명명 규칙                       | analyst, implementer                   |
| [02-domain-layer.md](02-domain-layer.md)                 | Entity, Value Object, Repository 패턴     | implementer, reviewer                  |
| [03-service-layer.md](03-service-layer.md)               | Service 패턴, 트랜잭션 규칙                     | implementer, reviewer                  |
| [04-web-layer.md](04-web-layer.md)                       | Controller, ApiSpec, DTO 패턴             | implementer, reviewer                  |
| [05-error-and-auth.md](05-error-and-auth.md)             | ErrorCode 패턴, 인증 어노테이션                  | analyst, implementer, tester, reviewer |
| [06-test-conventions.md](06-test-conventions.md)         | 테스트 패턴 전체 (Domain/Service/Fixture/Fake) | tester, reviewer                       |
| [07-operations.md](07-operations.md)                     | 로깅, DB 마이그레이션, 자주 실수하는 사례               | implementer, reviewer                  |

## 에이전트별 읽기 가이드

| 에이전트            | 읽어야 할 파일                                             |
|-----------------|------------------------------------------------------|
| **analyst**     | `01-structure-and-naming.md`, `05-error-and-auth.md` |
| **implementer** | `01` ~ `05` + `07` (테스트 파일 제외)                       |
| **tester**      | `06-test-conventions.md`, `05-error-and-auth.md`     |
| **reviewer**    | 검토 대상에 해당하는 파일만 선택적으로 읽기                             |
