# 웹 레이어 패턴 (Controller / ApiSpec / DTO)

## 7. Controller 패턴

```java
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MyEntityController implements MyEntityApiSpec {

    private final MyEntityService myEntityService;

    @Override
    @BusinessLogging("엔티티 생성")
    @PostMapping("/my-entities")
    public ResponseEntity<MyEntityResponse> createMyEntity(
            @Valid @RequestBody MyEntityRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(myEntityService.createMyEntity(request));
    }

    @Override
    @GetMapping("/my-entities/{id}")
    public ResponseEntity<MyEntityResponse> getMyEntity(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(myEntityService.getMyEntity(id));
    }

    // 인증 필요 시
    @Override
    @BusinessLogging("인증 필요 작업")
    @PostMapping("/rooms/{roomId}/my-entities")
    public ResponseEntity<MyEntityResponse> createWithRoom(
            @PathVariable Long roomId,
            @LoginUserId Long userId,
            @Valid @RequestBody MyEntityRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(myEntityService.createWithRoom(roomId, userId, request));
    }
}
```

---

## 8. API Spec (OpenAPI 인터페이스) 패턴

```java
@Tag(name = "MyEntity API", description = "엔티티 관련 API")
public interface MyEntityApiSpec {

    @Operation(summary = "엔티티 생성", description = "새로운 엔티티를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<MyEntityResponse> createMyEntity(MyEntityRequest request);

    @Operation(summary = "엔티티 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<MyEntityResponse> getMyEntity(Long id);
}
```

---

## 9. DTO 패턴

### Request DTO

```java
@Schema(description = "엔티티 생성 요청")
public record MyEntityRequest(

        @Schema(description = "이름", example = "테스트 이름")
        @NotBlank(message = "이름은 공백일 수 없습니다.")
        String name,

        @Schema(description = "설명", example = "설명 텍스트")
        @Size(max = 100, message = "설명은 100자를 초과할 수 없습니다.")
        String description
) {

}
```

### Response DTO

```java
@Schema(description = "엔티티 응답")
public record MyEntityResponse(

        @Schema(description = "ID", example = "1")
        long id, // id 응답은 null 허용 x

        @Schema(description = "이름", example = "테스트 이름")
        String name

) {

    // 단일 변환
    public static MyEntityResponse from(MyEntity entity) {
        return new MyEntityResponse(entity.getId(), entity.getName());
    }

    // 리스트 변환
    public static List<MyEntityResponse> from(List<MyEntity> entities) {
        return entities.stream().map(MyEntityResponse::from).toList();
    }
}
```
