---
name: git-review
description: push되지 않은 git 변경사항(미커밋·미push 모두 포함)을 분석하고 코드 리뷰 포인트를 제공할 때 반드시 이 스킬을 사용할 것. "/review", "리뷰해줘", "PR 전에 확인해줘", "push 전 검토해줘", "변경사항 리뷰", "코드 검토해줘", "커밋 전 확인", "다시 리뷰해줘", "리뷰 재실행" 등에 트리거된다. 기존 analyst·reviewer 에이전트를 재활용한다.
---

## 실행 모드

**하이브리드 서브 에이전트** — 오케스트레이터(git 수집) → analyst(순차) → reviewer(순차) → 오케스트레이터(종합 보고)

모든 에이전트는 `model: "opus"` 사용.

---

## Phase 0: git 변경사항 수집 (오케스트레이터 직접 실행)

아래 순서로 Bash를 실행하여 변경 내용을 파악하고 `_workspace/review_git_info.md`에 저장한다.

```bash
# 1. 현재 브랜치 확인
git branch --show-current

# 2. 미커밋 변경 파일 (워킹 디렉토리 + 스테이징)
git status --short

# 3. 미push 커밋 목록 (upstream 기준, 없으면 origin/main 기준)
git log @{u}..HEAD --oneline 2>/dev/null || git log origin/main..HEAD --oneline

# 4. 미push 커밋에 포함된 변경 파일 목록
git diff @{u}..HEAD --name-only 2>/dev/null || git diff origin/main..HEAD --name-only

# 5. 미커밋 변경 파일 목록 (staged + unstaged)
git diff HEAD --name-only
```

수집한 정보를 `_workspace/review_git_info.md`에 저장:

```
## 현재 브랜치
[브랜치명]

## 미push 커밋
[git log 결과]

## 변경 파일 전체 목록 (미커밋 + 미push 합산, 중복 제거)
[파일 경로 목록]

## 검토 범위 판단
- 미커밋 변경: [있음/없음]
- 미push 커밋: [N개]
- 총 변경 파일: [N개]
```

변경 파일이 없으면: 사용자에게 "검토할 변경사항이 없습니다"를 전달하고 종료.

---

## Phase 1: 변경 컨텍스트 분석 (analyst, 순차)

```
Agent(
  subagent_type: "general-purpose",
  model: "opus",
  prompt: """
  당신은 Pickeat 백엔드 analyst 에이전트입니다.
  에이전트 정의를 읽으세요: .claude/agents/analyst.md

  이번 역할은 git 변경사항의 컨텍스트를 분석하는 것입니다.

  다음 파일들을 반드시 먼저 읽으세요:
  1. _workspace/review_git_info.md (변경 파일 목록)
  2. 목록에 있는 변경 파일들을 직접 읽어 내용 파악
  3. .claude/skills/conventions/references/01-structure-and-naming.md (패키지 구조, 명명 규칙)
  4. .claude/skills/conventions/references/05-error-and-auth.md (ErrorCode, 인증 방식)

  분석 항목:
  - 이 변경이 어떤 기능/버그수정/리팩토링인지 파악
  - 변경 범위가 적절한지 (너무 많은 파일이 한 커밋에 묶였는지)
  - 누락된 파일이 있는지 확인:
    * 새 Service가 추가됐는데 테스트가 없는 경우
    * 새 Controller가 추가됐는데 ApiSpec이 없는 경우
    * DB 스키마 변경인데 Flyway migration이 없는 경우
    * ErrorCode가 추가됐는데 실제 사용처가 없는 경우
  - 변경 의도가 코드에 명확히 드러나는지

  결과를 _workspace/review_01_context.md에 저장하라:

  ```

## 변경 요약

[이 변경이 무엇인지 한 줄 요약]

## 변경 유형

[기능 추가 / 버그 수정 / 리팩토링 / 설정 변경 / 기타]

## 변경 범위 평가

[적절 / 주의 필요 - 이유]

## 누락 파일 의심 목록

| 누락 파일 | 이유  |
  |-------|-----|
| ...   | ... |

## 컨텍스트 특이사항

[리뷰어가 알아야 할 배경 정보]

  ```

  완료 후 "analyst 완료"를 출력하라.
  """
)
```

---

## Phase 2: 코드 컨벤션 리뷰 (reviewer, 순차)

```
Agent(
  subagent_type: "general-purpose",
  model: "opus",
  prompt: """
  당신은 Pickeat 백엔드 reviewer 에이전트입니다.
  에이전트 정의를 읽으세요: .claude/agents/reviewer.md

  이번 역할은 git 변경 파일들의 코드 컨벤션을 검토하는 것입니다.

  다음 파일들을 반드시 먼저 읽으세요:
  1. _workspace/review_git_info.md (변경 파일 목록)
  2. _workspace/review_01_context.md (변경 컨텍스트)
  3. 변경 파일의 레이어에 해당하는 컨벤션 파일만 선택적으로 읽기:
     - Entity/VO/Repository → .claude/skills/conventions/references/02-domain-layer.md
     - Service → .claude/skills/conventions/references/03-service-layer.md
     - Controller/DTO → .claude/skills/conventions/references/04-web-layer.md
     - ErrorCode/인증 → .claude/skills/conventions/references/05-error-and-auth.md
     - 테스트 파일 → .claude/skills/conventions/references/06-test-conventions.md
     - 마이그레이션 → .claude/skills/conventions/references/07-operations.md

  review_git_info.md의 변경 파일 목록에 있는 소스 파일들을 모두 직접 읽고,
  reviewer.md의 체크리스트를 기반으로 CRITICAL/MAJOR/MINOR 이슈를 분류하라.

  추가로 다음 git-review 전용 항목도 점검하라:
  - [ ] Domain Test 또는 Service Test 파일이 변경/추가되었는가 (새 기능인데 테스트가 없으면 MAJOR)
  - [ ] Service Test에 `@DataJpaTest` + `@Import` + `flush()/clear()` 패턴이 지켜졌는가
  - [ ] ApiSpec 인터페이스가 Controller와 함께 변경되었는가
  - [ ] 공개 API 변경 시 하위 호환성 고려가 있는가

  결과를 _workspace/review_02_conventions.md에 저장하라.
  (기존 reviewer.md 출력 프로토콜 형식 그대로 사용)

  완료 후 "reviewer 완료"를 출력하라.
  """
)
```

---

## Phase 3: git 관점 추가 점검 (오케스트레이터 직접 실행)

`_workspace/review_git_info.md`의 커밋 목록을 읽고 아래 항목을 직접 점검한다.

**커밋 메시지 품질 기준:**

- 유형 prefix 사용 여부 (`feat:`, `fix:`, `refactor:`, `env:`, `test:` 등)
- 내용이 구체적인지 (예: "fix: 수정" → 불량 / "fix: 픽잇 코드 파싱 오류 수정" → 양호)
- 한 커밋에 너무 많은 변경이 섞이지 않았는지

**변경 파일 구성 점검:**

- 프로덕션 코드만 있고 테스트가 없는지
- 설정 파일(application.properties 등) 변경 시 민감 정보 포함 여부

점검 결과를 `_workspace/review_03_git.md`에 저장:

```
## 커밋 메시지 점검
| 커밋 해시 | 메시지 | 평가 | 개선 제안 |
|----------|--------|------|----------|
| ... | ... | 양호/주의/불량 | ... |

## 변경 구성 점검
- 테스트 포함 여부: [예/아니오]
- 민감 정보 위험: [없음/주의 - 이유]
- 기타 특이사항: [...]
```

---

## Phase 4: 종합 리뷰 보고서

`_workspace/review_01_context.md`, `review_02_conventions.md`, `review_03_git.md`를 읽고 사용자에게 아래 형식으로 보고한다.

```
## 리뷰 결과 — {브랜치명}

### 변경 요약
[변경 유형 + 한 줄 설명]

### 코드 컨벤션
- CRITICAL: N건
- MAJOR: N건
- MINOR: N건

[CRITICAL/MAJOR 이슈가 있으면 상세 목록 표시]
| 파일 | 문제 | 수정 방법 |
|------|------|----------|

### 누락 파일
[analyst가 발견한 누락 파일 목록 - 없으면 "없음"]

### 커밋 메시지
[양호/주의 사항 있음 + 개선 필요 커밋 목록]

### 최종 판정
- ✅ PR 준비 완료: 이슈 없음
- ⚠️ 수정 권장: MAJOR 이슈 해결 후 PR 권장
- 🚫 수정 필요: CRITICAL 이슈 해결 후 재검토 필요
```

---

## 에러 핸들링

- **git upstream 미설정**: `@{u}` 실패 시 `origin/main`으로 자동 대체
- **변경사항 없음**: "검토할 변경사항이 없습니다. git status를 확인하세요." 출력 후 종료
- **analyst 실패**: context 없이 reviewer만 실행, 누락 파일 점검 생략
- **reviewer 실패**: context 결과만 보고, 컨벤션 점검 미완료로 명시

---

## 테스트 시나리오

### 정상 흐름: 미커밋 변경사항 존재

```
입력: "/review" 또는 "리뷰해줘"
기대:
1. git diff HEAD --name-only로 변경 파일 수집
2. analyst: DeprecationInterceptor.java 변경 내용 분석
3. reviewer: 컨벤션 점검 (CRITICAL/MAJOR/MINOR)
4. 커밋 메시지 점검 (미커밋이면 해당 없음 표시)
5. 종합 보고: 최종 판정 포함
```

### 에러 흐름: 변경사항 없음

```
입력: "/review"
기대:
1. git status 실행 → 변경 없음 확인
2. "검토할 변경사항이 없습니다" 출력 후 종료
```

### 부분 흐름: 미push 커밋만 존재

```
입력: "/review"
기대:
1. git log @{u}..HEAD로 미push 커밋 감지
2. git diff @{u}..HEAD --name-only로 변경 파일 수집
3. 커밋 메시지 품질까지 포함하여 전체 리뷰
```
