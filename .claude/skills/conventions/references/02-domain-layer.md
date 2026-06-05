# 도메인 레이어 패턴 (Entity / Value Object / Repository)

## 3. Entity 패턴

```java
@Entity
@Table(name = "my_table")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@SoftDelete  // 논리 삭제가 필요한 엔티티에만
public class MyEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MyStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "other_id", nullable = false)
    private OtherEntity other;

    // 생성 팩토리 메서드
    public static MyEntity create(String name, MyStatus status) {
        MyEntity entity = new MyEntity();
        entity.name = name;
        entity.status = status;
        return entity;
    }

    // 상태 변경 메서드 (직접 setter 금지)
    public void updateName(String name) {
        this.name = name;
    }
}
```

**BaseEntity가 제공하는 것:**

- `id` (Long, AUTO_INCREMENT)
- `createdAt` (LocalDateTime)
- `updatedAt` (LocalDateTime)
- `@SoftDelete` 필드 (deleted_at 등)

---

## 4. Embeddable Value Object 패턴

```java
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyValueObject {

    @Column(name = "field_name", nullable = false)
    private String value;

    public MyValueObject(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_VALUE);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
```

---

## 5. Repository 패턴

```java
public interface MyEntityRepository extends JpaRepository<MyEntity, Long> {

    // 단순 조회 - Spring Data 메서드명 규칙 활용
    Optional<MyEntity> findByName(String name);

    List<MyEntity> findAllByStatus(MyStatus status);

    boolean existsByName(String name);

    // 복잡한 쿼리 - @Query 사용
    @Query("SELECT m FROM MyEntity m WHERE m.status = :status AND m.other.id = :otherId")
    List<MyEntity> findByStatusAndOtherId(@Param("status") MyStatus status,
                                          @Param("otherId") Long otherId);
}
```
