# 운영 규칙 (로깅 / DB 마이그레이션 / 자주 실수하는 사례)

## 13. 로깅 컨벤션

### 비즈니스 로그

```java
@BusinessLogging("방에서 픽잇 생성")  // Controller 메서드에 적용
```

### 로그 레벨 기준

| 레벨    | 사용 시점                        |
|-------|------------------------------|
| INFO  | 정상 비즈니스 흐름                   |
| WARN  | 4xx 오류 (클라이언트 실수), 예상 가능한 예외 |
| ERROR | 5xx 오류, 예상치 못한 예외            |
| DEBUG | 개발 디버깅용 (프로덕션 비활성)           |

---

## 14. 자주 실수하는 컨벤션 위반 사례

| 위반                                  | 올바른 방법                                       |
|-------------------------------------|----------------------------------------------|
| `public MyEntity()` (public 기본 생성자) | `@NoArgsConstructor(access = PROTECTED)`     |
| setter 메서드 사용 (`setName(...)`)      | 도메인 메서드 (`updateName(...)`)                  |
| Service에 `@Transactional` 없이 쓰기     | 쓰기 메서드에 `@Transactional` 추가                  |
| `throw new RuntimeException(...)`   | `throw new BusinessException(ErrorCode.XXX)` |
| DTO에 변환 없이 `new DTO(...)` 직접        | `MyResponse.from(entity)` 사용                 |
| Controller가 ApiSpec 미구현             | `implements MyEntityApiSpec` 추가              |
| 테스트 메서드명 영어                         | 한글 메서드명 사용                                   |
| ScenarioTest에 `@DirtiesContext` 누락  | 반드시 클래스에 선언                                  |
