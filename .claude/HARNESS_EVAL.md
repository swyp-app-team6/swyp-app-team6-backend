# 하네스 평가 프레임워크

하네스 파일만 분석하는 **정적 평가** — 실행 비용 없이 언제든 측정 가능.

---

## 카테고리 구성

| ID | 카테고리        | 만점       | 가중치  |
|----|-------------|----------|------|
| A  | 프롬프트 완전성    | 20점      | 20%  |
| B  | 컨텍스트 체인 완전성 | 20점      | 20%  |
| C  | 일관성         | 25점      | 25%  |
| D  | 가드레일 커버리지   | 20점      | 20%  |
| E  | 컨벤션 참조 커버리지 | 15점      | 15%  |
|    | **합계**      | **100점** | 100% |

---

## A. 프롬프트 완전성 (20점)

> 각 에이전트 파일(agent.md)이 필수 4요소를 갖추는가.

**채점 단위: 에이전트 1개 = 5점, 4개 에이전트 × 5점 = 20점**

에이전트 1개 기준 세부 항목 (각 1.25점):

| 항목       | 체크 기준                           |
|----------|---------------------------------|
| 역할 정의    | "핵심 역할" 섹션이 있고 한 문장으로 역할이 서술되는가 |
| 입력 소스 명시 | 읽어야 할 파일 목록이 구체적 경로로 명시되는가      |
| 출력 형식 명시 | 출력 파일 경로와 내부 구조(섹션명)가 명시되는가     |
| 에러 핸들링   | 실패 시 동작 방침이 명시되는가               |

**체크리스트:**

| 에이전트        | 역할 정의 | 입력 명시 | 출력 형식 | 에러 핸들링 | 소계      |
|-------------|-------|-------|-------|--------|---------|
| analyst     | ✅     | ✅     | ✅     | ✅      | 5점      |
| implementer | ✅     | ✅     | ✅     | ✅      | 5점      |
| tester      | ✅     | ✅     | ✅     | ✅      | 5점      |
| reviewer    | ✅     | ✅     | ✅     | ✅      | 5점      |
| **A 합계**    |       |       |       |        | **20점** |

---

## B. 컨텍스트 체인 완전성 (20점)

> 에이전트 A의 출력 파일이 에이전트 B의 입력으로 정확히 연결되는가.
> 끊어진 링크 1개당 **-5점**.

**체크 대상 링크 (4개):**

| 링크                     | 출력 측                                     | 입력 측                                     | 파일명 일치 | 점수      |
|------------------------|------------------------------------------|------------------------------------------|--------|---------|
| analyst → implementer  | `_workspace/01_analyst_output.md` 생성     | `_workspace/01_analyst_output.md` 읽기     | ✅      | 5점      |
| analyst → tester       | `_workspace/01_analyst_output.md` 생성     | `_workspace/01_analyst_output.md` 읽기     | ✅      | 5점      |
| implementer → reviewer | `_workspace/02_implementer_output.md` 생성 | `_workspace/02_implementer_output.md` 읽기 | ✅      | 5점      |
| tester → reviewer      | `_workspace/03_tester_output.md` 생성      | `_workspace/03_tester_output.md` 읽기      | ✅      | 5점      |
| **B 합계**               |                                          |                                          |        | **20점** |

---

## C. 일관성 (25점)

> 하네스 내 파일들 사이에 모순·불일치가 없는가.
> 세부 4개 항목으로 구성.

### C-1. 에이전트 정의 ↔ 스킬 프롬프트 일관성 (10점)

agent.md에 정의된 작업 순서·규칙이 SKILL.md 프롬프트 지시와 충돌하지 않는가.

| 비교 쌍                                                      | 체크 항목                               | 기준                              | 점수   |
|-----------------------------------------------------------|-------------------------------------|---------------------------------|------|
| `analyst.md` ↔ `feature-development/SKILL.md` Phase 1     | analyst가 읽도록 정의된 파일이 프롬프트에도 포함되는가   | agent.md 탐색 순서 ⊆ SKILL.md 프롬프트  | 2.5점 |
| `implementer.md` ↔ `feature-development/SKILL.md` Phase 2 | implementer 구현 순서가 프롬프트와 일치하는가      | agent.md 구현 순서 = SKILL.md 구현 순서 | 2.5점 |
| `tester.md` ↔ `feature-development/SKILL.md` Phase 2      | tester 테스트 작성 순서가 프롬프트와 충돌하지 않는가    | 불일치 항목 수                        | 2.5점 |
| `reviewer.md` ↔ `feature-development/SKILL.md` Phase 3    | reviewer 체크리스트 기준 파일이 프롬프트 지시와 동일한가 | 레이어별 선택적 읽기 일치 여부               | 2.5점 |

### C-2. _workspace 파일명 일관성 (5점)

`_workspace/` 내 파일명이 모든 파일(agent.md, SKILL.md, HARNESS.md)에서 동일하게 사용되는가.

| 파일명                        | 생성 위치          | 참조 위치들                                                              | 일치 여부 |
|----------------------------|----------------|---------------------------------------------------------------------|-------|
| `01_analyst_output.md`     | analyst.md     | implementer.md, tester.md, feature-development/SKILL.md, HARNESS.md | ✅     |
| `02_implementer_output.md` | implementer.md | reviewer.md, feature-development/SKILL.md, HARNESS.md               | ✅     |
| `03_tester_output.md`      | tester.md      | reviewer.md, feature-development/SKILL.md, HARNESS.md               | ✅     |
| `04_reviewer_output.md`    | reviewer.md    | feature-development/SKILL.md, HARNESS.md                            | ✅     |

불일치 파일명 수: 0개 → **5점**

### C-3. 모델 설정 일관성 (5점)

HARNESS.md에 선언된 모델 기준과 각 SKILL.md 프롬프트의 `model:` 파라미터가 일치하는가.

| 에이전트 호출 위치                                | HARNESS.md 기준 | 실제 model 값 | 일치 |
|-------------------------------------------|---------------|------------|----|
| feature-development Phase 1 (analyst)     | opus          | opus       | ✅  |
| feature-development Phase 2 (implementer) | opus          | opus       | ✅  |
| feature-development Phase 2 (tester)      | sonnet        | sonnet     | ✅  |
| feature-development Phase 3 (reviewer)    | sonnet        | sonnet     | ✅  |
| git-review Phase 1 (analyst)              | sonnet        | sonnet     | ✅  |
| git-review Phase 2 (reviewer)             | opus          | opus       | ✅  |

불일치 수: 0개 → **5점**

### C-4. 컨벤션 참조 대칭성 (5점)

implementer가 읽도록 지시된 컨벤션 파일이 reviewer의 검토 기준 파일과 대칭되는가.
(구현 레이어 → 동일 컨벤션 파일로 검토해야 의미 있음)

| 레이어        | implementer 참조           | reviewer 참조              | 대칭 |
|------------|--------------------------|--------------------------|----|
| Domain     | `02-domain-layer.md`     | `02-domain-layer.md`     | ✅  |
| Service    | `03-service-layer.md`    | `03-service-layer.md`    | ✅  |
| Web        | `04-web-layer.md`        | `04-web-layer.md`        | ✅  |
| Error/Auth | `05-error-and-auth.md`   | `05-error-and-auth.md`   | ✅  |
| Test       | `06-test-conventions.md` | `06-test-conventions.md` | ✅  |
| Operations | `07-operations.md`       | `07-operations.md`       | ✅  |

비대칭 레이어 수: 0개 → **5점**

**C 합계: 25점**

---

## D. 가드레일 커버리지 (20점)

> 핵심 이벤트에 대한 자동 감시 비율.

**감시 대상 이벤트 4개 기준 (이벤트 1개 = 5점):**

| 이벤트                   | 감시 방법                                  | 존재 여부 | 점수 |
|-----------------------|----------------------------------------|-------|----|
| 테스트 파일 작성 후 컴파일 오류    | `post-test-compile.sh` (PostToolUse)   | ✅     | 5점 |
| 프로덕션 코드 추가 후 테스트 누락   | `stop-test-absence.sh` (Stop)          | ✅     | 5점 |
| 잘못된 브랜치에서 기능 개발       | feature-development Phase 0-1 (git 체크) | ✅     | 5점 |
| 에이전트 실패 시 중단 없이 계속 진행 | feature-development 에러 핸들링 섹션          | ✅     | 5점 |

**D 합계: 20점**

---

## E. 컨벤션 참조 커버리지 (15점)

> 각 에이전트가 자신의 역할에 필요한 컨벤션 파일을 참조하도록 지시받는가.

**체크 기준: 7개 컨벤션 파일 중 역할에 맞는 파일을 참조하는가**

| 에이전트        | 필수 참조 파일                                                                       | 실제 참조 | 누락 |
|-------------|--------------------------------------------------------------------------------|-------|----|
| analyst     | `01-structure-and-naming.md`, `05-error-and-auth.md`, `00-domain-knowledge.md` | 3/3   | 0개 |
| implementer | `01~05.md`, `07-operations.md` (6개)                                            | 6/6   | 0개 |
| tester      | `06-test-conventions.md`, `05-error-and-auth.md`                               | 2/2   | 0개 |
| reviewer    | 레이어별 해당 파일 (선택적)                                                               | 정의됨   | 0개 |

누락 파일 수: 0개 → **15점**

---

## 최종 점수 집계 공식

```
최종 점수 = A × 0.20 + B × 0.20 + C × 0.25 + D × 0.20 + E × 0.15
```

| 카테고리           | 원점수     | 가중치 | 환산점수       |
|----------------|---------|-----|------------|
| A. 프롬프트 완전성    | 20 / 20 | 20% | 20.0점      |
| B. 컨텍스트 체인 완전성 | 20 / 20 | 20% | 20.0점      |
| C. 일관성         | 25 / 25 | 25% | 25.0점      |
| D. 가드레일 커버리지   | 20 / 20 | 20% | 20.0점      |
| E. 컨벤션 참조 커버리지 | 15 / 15 | 15% | 15.0점      |
| **최종**         |         |     | **100.0점** |

### 등급 기준

| 점수     | 등급 | 해석                       |
|--------|----|--------------------------|
| 90~100 | A  | 프로덕션 수준 — 신뢰하고 사용 가능     |
| 75~89  | B  | 안정적 — 일부 엣지케이스 수동 보완 필요  |
| 60~74  | C  | 주의 필요 — 특정 카테고리 집중 개선 권장 |
| 60 미만  | D  | 재설계 권장                   |

---

## 감점 규칙 요약

| 유형            | 감점 기준                          |
|---------------|--------------------------------|
| 컨텍스트 링크 단절    | 끊어진 링크 1개당 -5점 (B 카테고리)        |
| 파일명 불일치       | 불일치 1개당 -1.25점 (C-2)           |
| 모델 불일치        | 불일치 1개당 -0.83점 (C-3, 6개 호출 기준) |
| 컨벤션 참조 누락     | 누락 파일 1개당 -1점 (E)              |
| 에이전트 필수 요소 누락 | 요소 1개당 -1.25점 (A)              |

---

## 이력

| 날짜         | 최종 점수 | 주요 변경 사항       |
|------------|-------|----------------|
| 2026-06-05 | 100점  | 초기 baseline 측정 |
