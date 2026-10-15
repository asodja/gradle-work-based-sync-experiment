#!/usr/bin/env bash
# Builds the prototype jar and the harness install, failing loudly (a `grep | ... || true` once hid a compile error
# for two measurement rounds). Verifies that the harness embeds the freshly built prototype jar.
set -euo pipefail
HERE=$(cd "$(dirname "$0")" && pwd)
IH=${IDEA_HOME:-$(ls -d "$HOME"/tools/ideaIC/idea-* | head -1)}
(cd "$HERE/prototype" && ./gradlew jar -PideaHome="$IH" -q --console=plain 2>&1 | grep -v JAVA_TOOL_OPTIONS || true; test "${PIPESTATUS[0]}" -eq 0)
(cd "$HERE/harness" && ./gradlew installDist -PideaHome="$IH" -q --console=plain 2>&1 | grep -v JAVA_TOOL_OPTIONS || true; test "${PIPESTATUS[0]}" -eq 0)
(cd "$HERE/harness" && ./gradlew --stop -q >/dev/null 2>&1 || true)
cmp -s "$HERE/prototype/build/libs/ide-model-tasks.jar" "$HERE/harness/build/install/harness/lib/ide-model-tasks.jar" || { echo "harness has a stale prototype jar" >&2; exit 1; }
echo "built: $(unzip -l "$HERE/prototype/build/libs/ide-model-tasks.jar" | grep -c '\.class$') prototype classes, ported models: $(java -cp "$HERE/harness/build/install/harness/lib/*" wbs.harness.PortedModelNames 2>/dev/null | tr '\n' ' ')"
