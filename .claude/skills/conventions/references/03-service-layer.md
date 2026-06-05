# 서비스 레이어 패턴 (Service)

## 6. Service 패턴

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // 클래스 레벨: 기본 readOnly
public class MyEntityService {

    private final MyEntityRepository myEntityRepository;
    private final OtherService otherService;

    // 조회 메서드 - readOnly 상속
    public MyEntityResponse getMyEntity(Long id) {
        MyEntity entity = myEntityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MY_ENTITY_NOT_FOUND));
        return MyEntityResponse.from(entity);
    }

    // 생성/수정/삭제 메서드 - 별도 @Transactional
    @Transactional
    public MyEntityResponse createMyEntity(MyEntityRequest request) {
        validateUnique(request.name());
        MyEntity entity = MyEntity.create(request.name(), MyStatus.ACTIVE);
        return MyEntityResponse.from(myEntityRepository.save(entity));
    }

    @Transactional
    public void deleteMyEntity(Long id) {
        MyEntity entity = myEntityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MY_ENTITY_NOT_FOUND));
        myEntityRepository.delete(entity);
    }

    // private 검증 메서드
    private void validateUnique(String name) {
        if (myEntityRepository.existsByName(name)) {
            throw new BusinessException(ErrorCode.ALREADY_MY_ENTITY_EXISTS);
        }
    }
}
```

**핵심 규칙:**

- 클래스 레벨: `@Transactional(readOnly = true)` 필수
- 쓰기 메서드에만 `@Transactional` 별도 추가
- 예외: 반드시 `BusinessException(ErrorCode.XXX)` 사용 (RuntimeException 직접 금지)
- 생성자 주입: `@RequiredArgsConstructor` 사용
