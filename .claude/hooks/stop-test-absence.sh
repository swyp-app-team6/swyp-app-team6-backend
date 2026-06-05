#!/usr/bin/env bash
# Stop hook: warn when new production Java files have no tests

NEW_PROD=$(git status --porcelain=v1 2>/dev/null \
  | sed -n 's/^?? //p; s/^A  //p' \
  | tr '\\' '/' \
  | grep -E 'src/main/java/.*(Service|Controller|Entity)\.java$' || true)

[ -z "$NEW_PROD" ] && exit 0

HAS_TEST=$(git status --porcelain=v1 2>/dev/null \
  | sed 's/^...//' \
  | tr '\\' '/' \
  | grep -Eq 'src/test/java/.*Test\.java$'; echo $?)

[ "$HAS_TEST" -eq 0 ] && exit 0

echo "WARN: new prod Java file(s) without test changes:" >&2
echo "$NEW_PROD" | sed 's/^/- /' >&2
echo "Add tests or run tester agent." >&2
exit 0
