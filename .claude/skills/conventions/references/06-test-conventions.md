# 테스트 컨벤션

## 12. 파일 위치

```
src/test/java/org/swyp/com/backend/
├── {domain}/
│   ├── domain/{Domain}Test.java                  # 도메인 단위 테스트
│   └── application/{Domain}ServiceTest.java      # 서비스 통합 테스트
├── fixture/
│   └── {Domain}Fixture.java                      # 테스트 데이터 팩토리
└── fake/
    └── {Domain}/Fake{Class}.java                 # 외부 의존성 Fake 구현체
```

---

## 도메인 단위 테스트 (Domain Test)

`@Nested` 클래스로 시나리오 그룹화, `assertAll()`로 복합 검증, 예외는 `assertThatThrownBy()`:

```java
class {Domain}

Test {

    @Nested
    class {
        domain
    } _생성 {

        @Test
        void 유효한_정보로_ {
            domain
        } _생성() {
            // given
            String name = "테스트 이름";

            // when
            {
                Domain
            } {
                domain
            } ={
                Domain
            }.create(name);

            // then
            assertAll(
                    () -> assertThat({domain}.getName()).isEqualTo(name),
                    () -> assertThat({domain}.getIsActive()).isTrue()
            );
        }
    }

    @Nested
    class {
        domain
    } _비활성화 {

        @Test
        void 권한_없는_사용자는_비활성화_실패 () {
            assertThatThrownBy(
                    () -> {
                        domain
                    }.deactivate(unauthorizedUserId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode. {
                DOMAIN
            } _ACCESS_DENIED.getMessage());
        }
    }
}
```

---

## 서비스 통합 테스트 (Service Test)

`@DataJpaTest` + `@Import({XxxService.class})`로 최소 컨텍스트 로드.
저장 후 반드시 `flush()/clear()` 호출해야 영속성 컨텍스트 초기화로 실제 DB 재조회:

```java

@DataJpaTest
@Import({{Domain}Service.class})
class {Domain}ServiceTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private {Domain}Service {domain}Service;

    @Test
    void {domain}_조회_성공() {
        // given
        {Domain} {domain} = testEntityManager.persist({Domain}Fixture.create());
        testEntityManager.flush();
        testEntityManager.clear();   // ← 필수: 영속성 컨텍스트 초기화 후 재조회

        // when
        {Domain}Response response = {domain}Service.get{Domain}({domain}.getId());

        // then
        assertThat(response.id()).isEqualTo({domain}.getId());
    }

    // 반복 생성 패턴은 헬퍼 메서드로 추출
    private {Domain} {domain}_저장() {
        {Domain} {domain} = testEntityManager.persist({Domain}Fixture.createWithoutRoom());
        testEntityManager.flush();
        testEntityManager.clear();
        return {domain};
    }
}
```

---

## Fixture 클래스 패턴

```java
public class {Domain}Fixture {

    public static {Domain} createWithoutRoom() {
        return {Domain}.createWithoutRoom("테스트{domain}");
    }

    public static {Domain} createWithRoom(Long roomId) {
        return {Domain}.createWithRoom("테스트{domain}", roomId);
    }
}

public class UserFixture {

    // UUID로 닉네임 유니크하게 생성
    public static User create() {
        return new User(UUID.randomUUID().toString(), 1L, "kakao");
    }
}
```

- 팩토리 메서드명: `create()`, `createWith{Variant}()`
- UUID 활용으로 유니크성 보장
- 최소 정보만 설정, 테스트 불필요 데이터는 기본값

---

## Fake 구현체 패턴

외부 API 의존성은 `@Profile("test")`로 Fake 구현체로 교체:

```java
// test/java/.../fake/TestKakaoLoginConfig.java
@Profile("test")
@Configuration
public class TestKakaoLoginConfig {

    @Bean
    public LoginClient kakaoLoginClient() {
        return new FakeKakaoLoginClient(privateKey);
    }
}
```

대체 대상: 카카오 로그인, S3 이미지 업로드, SSE 서버 클라이언트 등 외부 통신 전반.

---

## SQL 초기 데이터

```java

@Sql("/init/template_data_v2.sql")  // 대용량 초기 데이터가 필요한 경우에만
@DataJpaTest
class {Domain}ServiceTest { ...
}
```

파일 위치: `src/test/resources/init/`

---

## AssertJ 주요 패턴

```java
// 단순 동일성
assertThat(response.id()).

isEqualTo(expected.getId());

// 컬렉션 필터링 + 필드 추출 검증
assertThat(restaurants)
        .

filteredOn(r ->r.

isExcluded())
        .

extracting(RestaurantResponse::id)
        .

containsAnyElementsOf(excludedIds);

// 순서 무관 컬렉션 비교
assertThat(responses)
        .

extracting( {
    Domain
}

Response::id)
        .

containsExactlyInAnyOrderElementsOf(expectedIds);

// 복합 검증
assertAll(
        () ->

assertThat(state.totalParticipants()).

isEqualTo(3),
        ()->

assertThat(state.participants())
        .

filteredOn(p ->p.

isCompleted())
        .

hasSize(2)
);

// 예외 검증
assertThatThrownBy(() ->service.

method(invalidInput))
        .

isInstanceOf(BusinessException .class)
        .

hasMessage(ErrorCode.SOME_ERROR.getMessage());

// 예외 없음 검증
assertThatCode(() ->new

Entity(validInput)).

doesNotThrowAnyException();
```

---

## 테스트 실행 명령

```bash
# 전체 테스트
./gradlew test

# 특정 도메인 테스트만
./gradlew test --tests "org.swyp.com.backend.{domain}.*"

# 특정 클래스
./gradlew test --tests "org.swyp.com.backend.{domain}.application.{Domain}ServiceTest"

# 테스트 컴파일만 확인 (빠른 검증)
./gradlew compileTestJava
```
