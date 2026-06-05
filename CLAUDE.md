**Important: Always respond in Korean, regardless of the language used in questions.**

**목표:** 코드 컨벤션을 준수하며 새 기능(Entity/Service/Controller/Test)을 자동으로 구현한다.

**트리거:**

- 새 기능 추가, API 구현, 버그 수정, 도메인 확장 → `feature-development` 스킬
- 컨벤션 질문 → `conventions` 스킬
- OOP/설계/아키텍처/리팩토링 질문 → `design-consultant` 스킬
- push 전 코드 리뷰, 변경사항 검토 → `/review` 커맨드 또는 `git-review` 스킬
- 단순 질문은 직접 응답 가능

**에이전트:** `.claude/agents/` — analyst, implementer, tester, reviewer

**스킬:** `.claude/skills/` — feature-development, conventions, design-consultant, git-review

**커맨드:** `.claude/commands/` — `/git-review` (git 변경사항 리뷰)
