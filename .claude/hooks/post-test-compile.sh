#!/bin/bash
# PostToolUse(Write|Edit|MultiEdit) hook: 테스트 파일 변경 시 컴파일 확인
# 종료 코드: 0 = 통과, 2 = Claude에게 피드백 전달

INPUT=$(cat)

# Python 폴백 (Windows Git Bash 대응)
PYTHON_CMD=""
if command -v python3 &> /dev/null; then
  PYTHON_CMD="python3"
elif command -v python &> /dev/null; then
  PYTHON_CMD="python"
else
  echo "Python을 찾을 수 없습니다" >&2
  exit 0  # Python 없으면 조용히 통과 (hook이 워크플로우 망치면 안 됨)
fi

# tool_input.file_path 추출 (중첩 구조 주의)
FILE_PATH=$(echo "$INPUT" | $PYTHON_CMD -c "
import sys, json
try:
    d = json.load(sys.stdin)
    print(d.get('tool_input', {}).get('file_path', ''))
except Exception:
    print('')
" 2>/dev/null)

# 파일 경로가 없으면 (다른 도구 호출) 조용히 통과
if [ -z "$FILE_PATH" ]; then
  exit 0
fi

# Windows 경로 정규화
FILE_PATH=$(echo "$FILE_PATH" | tr '\\' '/')
FILE_NAME=$(basename "$FILE_PATH" .java)

# Java 테스트 파일이 아니면 통과
if [[ "$FILE_PATH" != *".java" ]] || [[ "$FILE_PATH" != *"src/test"* ]]; then
  exit 0
fi

echo "🔍 컴파일 확인: ${FILE_NAME}" >&2

COMPILE_OUTPUT=$(./gradlew compileTestJava -q --console=plain 2>&1)
COMPILE_EXIT=$?

if [ $COMPILE_EXIT -eq 0 ]; then
  echo "✅ 컴파일 성공" >&2
  exit 0
else
  echo "❌ 컴파일 실패: ${FILE_NAME}" >&2
  echo "$COMPILE_OUTPUT" | tail -20 >&2
  echo "" >&2
  echo "수정 가이드: 컴파일 오류를 확인하고 수정하세요." >&2
  exit 2
fi
