# Pickeat 백엔드 하네스 구조 문서

## 디렉토리 구조

```
.claude/
├── HARNESS.md                              ← 이 파일 (하네스 전체 구조 설명)
├── settings.json                           ← 훅 설정 (PostToolUse + Stop)
├── commands/
│   └── review.md                          ← /review 슬래시 커맨드
├── agents/
│   ├── analyst.md                          ← 요구사항 분석 에이전트
│   ├── implementer.md                      ← 프로덕션 코드 구현 에이전트
│   ├── tester.md                           ← 인수 테스트 작성 + 실행 검증 에이전트
│   └── reviewer.md                         ← 코드 리뷰 에이전트
├── hooks/
│   ├── post-test-compile.sh               ← PostToolUse: 테스트 파일 변경 시 컴파일/실행
│   └── stop-test-absence.sh               ← Stop: 테스트 누락 감지 경고
└── skills/
    ├── feature-development/
    │   └── SKILL.md                        ← 기능 개발 오케스트레이터 (메인 스킬)
    ├── git-review/
    │   └── SKILL.md                        ← 미push 변경사항 리뷰 오케스트레이터
    ├── conventions/
    │   ├── SKILL.md                        ← 컨벤션 빠른 참조
    │   └── references/
    │       ├── code-conventions.md         ← Pickeat 코드 패턴 상세 + 테스트 가이드
    │       └── team-conventions.md         ← 팀 철학·아키텍처 원칙
    └── design-consultant/
        └── SKILL.md                        ← OOP/설계 상담
```

---

## 에이전트 정의

> **원칙**: 에이전트 = "누가 하는가" (역할, 순서, 입출력 프로토콜)

| 에이전트            | 파일                      | 핵심 역할                                                                   | 입력                  | 출력                                    |
|-----------------|-------------------------|-------------------------------------------------------------------------|---------------------|---------------------------------------|
| **analyst**     | `agents/analyst.md`     | 기능 요청 분석, 영향 도메인 파악, API 설계                                             | 사용자 요청              | `_workspace/01_analyst_output.md`     |
| **implementer** | `agents/implementer.md` | 프로덕션 코드 구현 (ErrorCode→Entity→Repository→DTO→Service→ApiSpec→Controller) | 01 파일 + 기존 코드       | `_workspace/02_implementer_output.md` |
| **tester**      | `agents/tester.md`      | Domain Test + Service Test 작성 **+ 실제 테스트 실행 검증**                        | 01 파일 + 기존 테스트 파일   | `_workspace/03_tester_output.md`      |
| **reviewer**    | `agents/reviewer.md`    | 컨벤션 준수 코드 리뷰 (CRITICAL/MAJOR/MINOR)                                     | 02·03 파일 + 변경 소스 파일 | `_workspace/04_reviewer_output.md`    |

---

## 스킬 정의

> **원칙**: 스킬 = "어떻게 하는가" (실행 방법, 지식, 참조 자료)

| 스킬                      | 파일                                    | 역할                                           | 트리거                          |
|-------------------------|---------------------------------------|----------------------------------------------|------------------------------|
| **feature-development** | `skills/feature-development/SKILL.md` | 4개 에이전트 조율 오케스트레이터 + **git 브랜치 분리**          | 기능 추가·API 구현·버그 수정·도메인 확장 요청 |
| **git-review**          | `skills/git-review/SKILL.md`          | 미push 변경사항 리뷰 오케스트레이터 (analyst+reviewer 재활용) | `/review`, 리뷰·검토·PR 전 확인 요청  |
| **conventions**         | `skills/conventions/SKILL.md`         | 코드 컨벤션 빠른 참조 + 상세 references 포인터             | 컨벤션·패턴·어노테이션 관련 질문           |
| **design-consultant**   | `skills/design-consultant/SKILL.md`   | 편향 없는 OOP/설계 상담, Claude+Gemini 협업 프레임워크      | 설계·리팩토링·아키텍처 결정 질문           |

## 커맨드 정의

| 커맨드       | 파일                   | 역할                             |
|-----------|----------------------|--------------------------------|
| `/review` | `commands/review.md` | git-review 스킬을 직접 호출하는 슬래시 커맨드 |

---

## 훅 (Hooks)

### 훅 구성 요약

| 파일                     | 이벤트                        | 조건            | 동작                                       |
|------------------------|----------------------------|---------------|------------------------------------------|
| `post-test-compile.sh` | `PostToolUse[Write\|Edit]` | `src/test/**` | 컴파일 확인 (`compileTestJava`)               |
| `stop-test-absence.sh` | `Stop`                     | 항상            | 새 Service/Controller 파일에 Test 파일이 없으면 경고 |

### post-test-compile.sh — 테스트 컴파일 확인 (PostToolUse)

```
Write/Edit 도구 사용 완료
        │
        ▼
파일 경로 확인 (stdin JSON 파싱)
        │
        └── src/test/**/*.java
                  ↓
            compileTestJava -q
            실패 → exit 2 (Claude가 오류 인지)
```

### stop-test-absence.sh — 테스트 누락 감지 (Stop)

```
Claude 응답 종료
        │
        ▼
git status --short 분석
        │
        ├── 신규 Service/Controller/Entity 파일 존재
        │   AND 신규 Test 파일 없음
        │         ↓
        │   ⚠️ 경고 메시지 출력 (비차단)
        │
        └── 조건 미충족 → 무음 종료
```

---

## 실행 흐름 (feature-development)

```
사용자 요청
    │
    ▼
[Phase 0-1] 브랜치 준비
    ├── 신규 기능 + 기반 브랜치(back/dev, main) → back/feat/{설명} 브랜치 생성
    └── 재실행/버그수정/이미 피처 브랜치 → 현재 브랜치 유지
    │
    ▼
[Phase 0-2] _workspace/ 컨텍스트 확인
    ├── 없음 → 초기 실행
    ├── 있음 + 부분 수정 요청 → 해당 에이전트만 재실행
    └── 있음 + 새 기능 요청 → _workspace_prev/ 로 이동 후 재실행
    │
    ▼
[Phase 1] analyst (순차)
    └── _workspace/01_analyst_output.md 생성
    │
    ▼
[Phase 2] implementer ──┐ (병렬)
          tester        ┘
    ├── implementer → _workspace/02_implementer_output.md
    └── tester     → _workspace/03_tester_output.md + 실제 테스트 실행
    │
    ▼
[Phase 3] reviewer (순차)
    └── _workspace/04_reviewer_output.md 생성
    │
    ▼
[Phase 4] 결과 종합 및 사용자 보고
```

**실행 모드**: 하이브리드 서브 에이전트 (모든 에이전트 `model: "opus"`)

---

## 실행 흐름 (git-review)

```
/review 또는 "리뷰해줘"
    │
    ▼
[Phase 0] 오케스트레이터 — git 변경사항 수집 (Bash)
    └── git status / git log / git diff → _workspace/review_git_info.md
    │
    ▼
[Phase 1] analyst (순차)          ← 기존 에이전트 재활용
    └── 변경 컨텍스트 분석, 누락 파일 탐지 → _workspace/review_01_context.md
    │
    ▼
[Phase 2] reviewer (순차)         ← 기존 에이전트 재활용
    └── 컨벤션 체크리스트 실행 → _workspace/review_02_conventions.md
    │
    ▼
[Phase 3] 오케스트레이터 — 커밋 메시지·구성 점검 (직접)
    └── _workspace/review_03_git.md
    │
    ▼
[Phase 4] 종합 보고 (✅ PR 준비 완료 / ⚠️ 수정 권장 / 🚫 수정 필요)
```

---

## 에이전트-스킬 분리 원칙

| 에이전트 파일에 담는 것 | 스킬 파일에 담는 것          |
|---------------|----------------------|
| 역할 정의 (누가)    | 실행 흐름 (오케스트레이터)      |
| 작업 고유 순서·절차   | 지식·컨벤션·참조 자료         |
| 입출력 프로토콜      | 에이전트 호출 방법           |
| 에러 핸들링 방침     | 트리거 기준 (description) |

### 분리 적절성 최종 상태

| 파일                             | 담고 있는 것                                            | 판정        |
|--------------------------------|----------------------------------------------------|-----------|
| `analyst.md`                   | 역할, 탐색 순서 (code + team conventions), 출력 형식         | ✅ 에이전트 고유 |
| `implementer.md`               | 역할, 구현 의존성 순서, 출력 형식                               | ✅ 에이전트 고유 |
| `tester.md`                    | 역할, Domain/Service 테스트 작성 순서, **실행 검증 3단계**, 출력 형식 | ✅ 에이전트 고유 |
| `reviewer.md`                  | 역할, 리뷰 체크리스트(작업 방식), 심각도 분류, 출력 형식                 | ✅ 에이전트 고유 |
| `feature-development/SKILL.md` | 오케스트레이션 흐름, **브랜치 생성 규칙**, 에이전트 호출 방법, 에러 핸들링      | ✅ 스킬      |
| `conventions/SKILL.md`         | 컨벤션 빠른 참조 + references 포인터                         | ✅ 스킬      |
| `design-consultant/SKILL.md`   | 설계 상담 원칙과 절차                                       | ✅ 스킬      |

---

## 데이터 흐름 (_workspace/)

```
_workspace/
├── 01_analyst_output.md      ← analyst 출력 (API 설계, 영향 파일 목록)
├── 02_implementer_output.md  ← implementer 출력 (구현 완료 파일 목록)
├── 03_tester_output.md       ← tester 출력 (테스트 파일 목록 + 실행 결과)
└── 04_reviewer_output.md     ← reviewer 출력 (CRITICAL/MAJOR/MINOR 이슈)
```

- 중간 산출물은 `_workspace/`에 보존 (재실행·감사 추적용)
- 이전 실행 결과는 `_workspace_prev/`로 이동

---

## 스킬 트리거 기준

| 사용자 입력 예시                                      | 트리거 스킬/커맨드               |
|------------------------------------------------|--------------------------|
| "새 기능 추가해줘", "API 만들어줘", "버그 수정해줘"             | `feature-development`    |
| "다시 실행해줘", "재실행", "수정해줘"                       | `feature-development`    |
| `/review`, "리뷰해줘", "PR 전에 확인해줘", "push 전 검토해줘" | `/review` → `git-review` |
| "컨벤션 알려줘", "Entity 어떻게 써요", "DTO 패턴이 뭔가요"      | `conventions`            |
| "설계 어떻게 해요", "이 구조 괜찮나요", "리팩토링 방향"            | `design-consultant`      |
| 단순 질문, 코드 설명, 파일 탐색                            | 직접 응답 (스킬 불필요)           |

---

## 원본 파일 이전 이력

| 원본 파일                       | 이전 위치                                               | 이전일        |
|-----------------------------|-----------------------------------------------------|------------|
| `CLAUDE.md` 본문 (설계 컨설턴트 내용) | `skills/design-consultant/SKILL.md`                 | 2026-05-27 |
| `CONVENTIONS.md` 전체         | `skills/conventions/references/team-conventions.md` | 2026-05-27 |

## 변경 이력

| 날짜         | 변경 내용                                          | 대상                                  |
|------------|------------------------------------------------|-------------------------------------|
| 2026-05-27 | 초기 하네스 구축                                      | 전체                                  |
| 2026-05-27 | git-review 스킬 + /review 커맨드 추가                 | skills/git-review, commands/review  |
| 2026-05-27 | team-conventions.md 한국어 전환                     | references/team-conventions.md      |
| 2026-05-27 | code-conventions.md 테스트 섹션 대폭 보강 (실제 코드 패턴 기반) | references/code-conventions.md      |
| 2026-05-27 | tester 에이전트에 테스트 실행 검증 3단계 추가                  | agents/tester.md                    |
| 2026-05-27 | PostToolUse 훅 추가 (컴파일 + ScenarioTest 자동 실행)    | hooks/post-test-compile.sh          |
| 2026-05-27 | Stop 훅 추가 (테스트 누락 감지)                          | hooks/stop-test-absence.sh          |
| 2026-05-27 | feature-development Phase 0에 git 브랜치 분리 로직 추가  | skills/feature-development/SKILL.md |
