#!/usr/bin/env bash
# Compares the Gradle-owned build models (IdeaProject modules, KotlinDslScriptsModel entries) of a builder dump with
# the per-project task payloads of a task dump, both written as text by the harness under <dump>/gradle-owned/.
#   compare-gradle-owned.sh <builders-dump> <tasks-dump>
set -uo pipefail
B=$1/gradle-owned; T=$2/gradle-owned
for kind in ideaModule kotlinDslScript; do
  same=0; diffn=0; missing=0
  while IFS= read -r -d '' f; do
    rel=${f#$T/$kind/}
    if [ ! -f "$B/$kind/$rel" ]; then missing=$((missing+1)); echo "NOT IN BUILDERS: $kind/$rel"; continue; fi
    if diff -q "$B/$kind/$rel" "$f" >/dev/null; then same=$((same+1)); else diffn=$((diffn+1)); echo "DIFFERENT: $kind/$rel"; diff "$B/$kind/$rel" "$f" | head -20; fi
  done < <(find "$T/$kind" -type f -name '*.txt' -print0 2>/dev/null | sort -z)
  only_builders=$(comm -23 <(cd "$B/$kind" 2>/dev/null && find . -type f ! -name _error.txt | sort) <(cd "$T/$kind" 2>/dev/null && find . -type f | sort) | wc -l)
  errors=$(find "$B/$kind" -name _error.txt 2>/dev/null | wc -l)
  echo "$kind: identical=$same different=$diffn not-in-builders=$missing builders-only=$only_builders builder-read-errors=$errors"
done
