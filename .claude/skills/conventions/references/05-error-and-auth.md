# ErrorCode 패턴 및 인증 어노테이션

## 10. ErrorCode 패턴

```java
public enum ErrorCode {
    // 기존 코드들...

    // 새 도메인 추가 시 그룹 단위로 추가
    // MyEntity
    MY_ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "엔티티를 찾을 수 없습니다."),
    ALREADY_MY_ENTITY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 엔티티입니다."),
    MY_ENTITY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 엔티티에 접근 권한이 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
```

**HttpStatus 기준:**

| 상태 코드                         | 사용 시점           |
|-------------------------------|-----------------|
| `NOT_FOUND` (404)             | 리소스가 존재하지 않을 때  |
| `BAD_REQUEST` (400)           | 잘못된 입력, 중복 등    |
| `FORBIDDEN` (403)             | 접근 권한 없음        |
| `UNAUTHORIZED` (401)          | 인증 필요 (토큰 만료 등) |
| `CONFLICT` (409)              | 상태 충돌           |
| `INTERNAL_SERVER_ERROR` (500) | 서버 내부 오류        |

---

## 11. 인증 어노테이션 사용법

```java
// 로그인한 사용자 ID 추출 (User JWT)
@LoginUserId
Long userId

// OAuth 공급자 정보 (회원가입 전 Provider JWT)
@Provider
ProviderPrincipal principal
```

**인증 헤더:**

| 헤더                              | 대상      |
|---------------------------------|---------|
| `Authorization: Bearer {token}` | 가입된 사용자 |
