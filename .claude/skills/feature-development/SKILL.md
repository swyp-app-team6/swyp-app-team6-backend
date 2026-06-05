---
name: feature-development
description: Pickeat 백엔드에 새 기능 추가, API 엔드포인트 구현, 버그 수정, 도메인 확장 요청 시 반드시 이 스킬을 사용할 것. analyst→implementer+tester(병렬)→reviewer 에이전트 팀을 조율하여 컨벤션을 준수한 코드를 생성한다. "새 기능 만들어줘", "API 추가해줘", "도메인 추가", "엔드포인트 구현", "기능 개발", "버그 수정", "다시 실행", "재실행", "업데이트", "수정해줘" 등 구현 요청에 트리거된다.
---

## 실행 모드

**하이브리드 서브 에이전트** — analyst(순차) → implementer + tester(병렬 팬아웃) → reviewer(순차)

모든 에이전트는 `model: "opus"` 사용.

---

## Phase 0: 브랜치 준비 및 컨텍스트 확인

### 0-1. Git 브랜치 준비 (신규 기능 요청 시)

먼저 현재 브랜치를 확인한다:

```bash
git branch --show-current
```

**브랜치 생성 기준:**

| 요청 유형     | 현재 브랜치                      | 조치              |
|-----------|-----------------------------|-----------------|
| 새 기능 추가   | `back/dev`, `main` 등 기반 브랜치 | 새 브랜치 생성 후 체크아웃 |
| 새 기능 추가   | `back/feat/*` 이미 피처 브랜치     | 현재 브랜치 유지       |
| 버그 수정     | 무관                          | 현재 브랜치 유지       |
| 부분 재실행/수정 | 무관                          | 현재 브랜치 유지       |

**새 브랜치 생성 방법:**

```bash
# 브랜치명: back/feat/{핵심-기능-요약-kebab-case}
# 예: "식당 메모 기능" → back/feat/restaurant-memo
# 예: "위시리스트 삭제 API" → back/feat/wishlist-delete
git checkout -b back/feat/{feature-description}
```

브랜치명은 사용자 요청의 핵심 도메인과 동작을 추출해 영어 kebab-case로 작성한다. 10자 이내로 간결하게.

### 0-2. _workspace 컨텍스트 확인

`_workspace/` 디렉토리 존재 여부를 확인한다:

- **`_workspace/` 없음**: 초기 실행 → Phase 1로 이동
- **`_workspace/` 있고 사용자가 부분 수정 요청**: 부분 재실행 → 해당 에이전트만 재호출
- **`_workspace/` 있고 새 기능 요청**: 새 실행 → `_workspace/`를 `_workspace_prev/`로 이름 변경 후 Phase 1로 이동

---

## Phase 1: 요구사항 분석 (analyst, 순차)

analyst 에이전트를 서브 에이전트로 실행한다.

```
Agent(
  subagent_type: "general-purpose",
  model: "opus",
  prompt: """
  당신은 Pickeat 백엔드 analyst 에이전트입니다.
  에이전트 정의를 읽으세요: .claude/agents/analyst.md
  
  기능 요청: {사용자 요청 내용}
  
  1. .claude/skills/conventions/references/01-structure-and-naming.md 읽기 (패키지 구조, 명명 규칙)
  2. .claude/skills/conventions/references/05-error-and-auth.md 읽기 (ErrorCode, 인증)
  3. 기존 유사 도메인 코드 탐색 (src/main/java/com/pickeat/backend/)
  4. 분석 결과를 _workspace/01_analyst_output.md에 저장
  
  저장 완료 후 "analyst 완료"를 출력하라.
  """
)
```

---

## Phase 2: 구현 + 테스트 작성 (implementer + tester, 병렬)

analyst 완료 후 두 에이전트를 **동시에** 실행한다.

### implementer (병렬 실행)

```
Agent(
  subagent_type: "general-purpose",
  model: "opus",
  run_in_background: true,
  prompt: """
  당신은 Pickeat 백엔드 implementer 에이전트입니다.
  에이전트 정의를 읽으세요: .claude/agents/implementer.md
  
  다음 파일들을 반드시 먼저 읽으세요:
  1. _workspace/01_analyst_output.md (구현 계획)
  2. 구현 레이어에 맞는 컨벤션 파일 (.claude/skills/conventions/references/ 하위):
     - 01-structure-and-naming.md (구조/명명)
     - 02-domain-layer.md (Entity/VO/Repository)
     - 03-service-layer.md (Service)
     - 04-web-layer.md (Controller/DTO)
     - 05-error-and-auth.md (ErrorCode/인증)
     - 07-operations.md (마이그레이션/위반사례)
  3. analyst가 지정한 참고 기존 코드 파일들
  
  위 정보를 바탕으로 프로덕션 코드를 구현하고,
  _workspace/02_implementer_output.md에 결과를 저장하라.
  
  구현 순서: ErrorCode → Entity → Repository → DTO → Service → ApiSpec → Controller → Migration(필요시)
  
  완료 후 "implementer 완료"를 출력하라.
  """
)
```

### tester (병렬 실행)

```
Agent(
  subagent_type: "general-purpose",
  model: "opus",
  run_in_background: true,
  prompt: """
  당신은 Pickeat 백엔드 tester 에이전트입니다.
  에이전트 정의를 읽으세요: .claude/agents/tester.md
  
  다음 파일들을 반드시 먼저 읽으세요:
  1. _workspace/01_analyst_output.md (도메인 설계)
  2. .claude/skills/conventions/references/06-test-conventions.md (테스트 패턴)
  3. src/test/java/ 내 해당 도메인 기존 테스트 파일 (패턴 파악)
  4. src/test/java/fixture/ 내 기존 Fixture 파일 (재사용 가능한 팩토리 파악)
  
  Domain Test와 Service Test를 작성하고,
  _workspace/03_tester_output.md에 결과를 저장하라.
  
  완료 후 "tester 완료"를 출력하라.
  """
)
```

---

## Phase 3: 코드 리뷰 (reviewer, 순차)

implementer와 tester 모두 완료 후 reviewer를 실행한다.

```
Agent(
  subagent_type: "general-purpose",
  model: "opus",
  prompt: """
  당신은 Pickeat 백엔드 reviewer 에이전트입니다. 
  에이전트 정의를 읽으세요: .claude/agents/reviewer.md
  
  다음 파일들을 반드시 먼저 읽으세요:
  1. _workspace/02_implementer_output.md (구현된 파일 목록)
  2. _workspace/03_tester_output.md (작성된 테스트 파일 목록)
  3. 변경 파일 레이어에 해당하는 컨벤션 파일만 선택적으로 읽기:
     - Entity/VO/Repository → .claude/skills/conventions/references/02-domain-layer.md
     - Service → .claude/skills/conventions/references/03-service-layer.md
     - Controller/DTO → .claude/skills/conventions/references/04-web-layer.md
     - ErrorCode/인증 → .claude/skills/conventions/references/05-error-and-auth.md
     - 테스트 파일 → .claude/skills/conventions/references/06-test-conventions.md
     - 마이그레이션 → .claude/skills/conventions/references/07-operations.md
  
  위 목록에 있는 모든 변경 파일을 읽고 컨벤션 리뷰를 수행하라.
  결과를 _workspace/04_reviewer_output.md에 저장하라.
  
  완료 후 "reviewer 완료"를 출력하라.
  """
)
```

---

## Phase 3.5: 도메인 지식 업데이트

`_workspace/02_implementer_output.md`를 읽고, **신규 생성된 Entity가 있는 경우**에만
`.claude/skills/conventions/references/00-domain-knowledge.md`를 업데이트한다.

업데이트 항목:

- **도메인 목록** — 새 도메인명, 패키지 경로, 핵심 역할, 추가일(오늘 날짜) 행 추가
- **도메인 관계** — 기존 도메인과의 연관 관계 추가 (있는 경우)
- **주요 비즈니스 규칙** — analyst가 분석 중 파악한 핵심 규칙 추가 (있는 경우)

기존 Entity 수정이나 Service/Controller만 변경된 경우에는 업데이트하지 않는다.

---

## Phase 4: 결과 종합 및 보고

`_workspace/04_reviewer_output.md`를 읽고 사용자에게 아래 형식으로 보고한다:

```
## 기능 개발 완료

### 구현된 파일
[implementer_output에서 파일 목록]

### 작성된 테스트
[tester_output에서 테스트 목록]

### 리뷰 결과
- CRITICAL: N건
- MAJOR: N건
- MINOR: N건
[이슈가 있으면 CRITICAL/MAJOR 목록 표시]

### 다음 단계
- CRITICAL/MAJOR 이슈가 있으면: 이슈 수정 후 재실행 권장
- 이슈 없으면: 빌드 후 테스트 실행 권장
  ./gradlew test --tests "*.acceptance_test.*"
```

---

## 에러 핸들링

- **analyst 실패 시**: 에러 메시지를 사용자에게 전달하고 중단. Phase 1만 재실행 가능.
- **implementer 실패 시**: tester 완료 후 implementer만 재실행. tester 결과는 유지.
- **tester 실패 시**: implementer 완료 후 tester만 재실행. implementer 결과는 유지.
- **reviewer 실패 시**: `_workspace/02_implementer_output.md`와 `_workspace/03_tester_output.md`를 직접 사용자에게 제공.

---

## 테스트 시나리오

### 정상 흐름: 새 기능 추가

```
입력: "식당에 메모를 남길 수 있는 기능을 추가해줘"
기대:
1. analyst: RestaurantMemo 도메인 분석, API 설계 (_workspace/01_analyst_output.md 생성)
2. implementer: RestaurantMemo Entity/Service/Controller/DTO/ErrorCode 구현
3. tester: RestaurantMemoPieceTest + RestaurantMemoScenarioTest 작성
4. reviewer: 컨벤션 점검 보고서
5. 최종: 생성/수정 파일 목록 + 리뷰 결과 보고
```

### 에러 흐름: 분석 실패

```
입력: 모호한 요청 ("뭔가 추가해줘")
기대:
1. analyst: 요구사항 불명확으로 명확화 질문 반환
2. 오케스트레이터: 사용자에게 질문 전달하고 중단
3. 사용자 답변 후 재시작
```

### 부분 재실행: 리뷰 이후 수정

```
입력: "리뷰에서 지적된 @Transactional 누락 수정해줘"
기대:
1. Phase 0: _workspace/ 존재 확인
2. implementer만 재실행 (해당 파일 수정)
3. reviewer 재실행 (수정 검증)
```
