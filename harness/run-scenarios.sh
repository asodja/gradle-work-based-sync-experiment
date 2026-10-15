#!/usr/bin/env bash
# Runs the measurement scenarios of design/measurement.md with the harness.
#
#   run-scenarios.sh <label> [scenario...]        scenarios: 1 2 3 4 5 (default: 1 2 3 4 5)
#
# Environment (defaults in brackets):
#   WBS_PROJECT   build to sync            [~/projects/gradle-wbs-benchmark]
#   WBS_GUH       Gradle user home         [~/.gradle-wbs]
#   WBS_IDEA_HOME IntelliJ installation    [~/tools/ideaIC/idea-*]
#   WBS_RUNS      measured runs/scenario   [3]
#   WBS_EXTRA     extra harness args       []
#
# Each measured run writes results/<label>/<scenario>-<run>.txt; run 0 is the warm-up and is not reported.
# Scenarios 2-5 run all their runs inside one harness JVM (the CC key depends on the client classloader, see
# LOG.md 2026-10-15). Each scenario also gets one traced profiling run in a fresh JVM (not timed, a CC miss):
# results/<label>/<scenario>-trace-log.txt and <scenario>-models.md.
set -euo pipefail

LABEL=${1:?label}; shift || true
SCENARIOS=("$@"); [ ${#SCENARIOS[@]} -eq 0 ] && SCENARIOS=(1 2 3 4 5)

HERE=$(cd "$(dirname "$0")" && pwd)
REPO=$(dirname "$HERE")
PROJECT=${WBS_PROJECT:-$HOME/projects/gradle-wbs-benchmark}
GUH=${WBS_GUH:-$HOME/.gradle-wbs}
IDEA_HOME=${WBS_IDEA_HOME:-$(ls -d "$HOME"/tools/ideaIC/idea-* | head -1)}
RUNS=${WBS_RUNS:-3}
OUT=$REPO/results/$LABEL
mkdir -p "$OUT"

HARNESS=$HERE/build/install/harness/bin/harness
if [ ! -x "$HARNESS" ]; then
  (cd "$HERE" && ./gradlew -q installDist -PideaHome="$IDEA_HOME")
fi
export IDEA_HOME

# Arguments every run shares with an IDE sync of gradle/gradle: no build scan upload, no remote cache.
COMMON=(--project "$PROJECT" --gradle-user-home "$GUH" --idea-home "$IDEA_HOME" --arg --no-scan --arg --no-build-cache)
PLAIN=(--arg --no-configuration-cache --arg -Dorg.gradle.isolated-projects=false)
MODEL_CACHE=(--arg -Dorg.gradle.internal.isolated-projects.caching=tooling)
EDIT_FILE=$PROJECT/subprojects/core/build.gradle.kts

stop_daemons() { (cd "$PROJECT" && ./gradlew -g "$GUH" --stop -q >/dev/null 2>&1 || true); }

run() { # run <name> <args...>: one run in a fresh JVM
  local name=$1; shift
  echo "== $LABEL $name $(date -Is)"
  "$HARNESS" "${COMMON[@]}" "$@" ${WBS_EXTRA:-} --out "$OUT/$name.txt" 2>&1 | grep "^run " || true
}

repeat() { # repeat <scenario> <args...>: warm-up run 0 plus RUNS measured runs in one JVM
  local s=$1; shift
  echo "== $LABEL $s x$((RUNS + 1)) $(date -Is)"
  "$HARNESS" "${COMMON[@]}" "$@" ${WBS_EXTRA:-} --repeat $((RUNS + 1)) --first-run 0 --out "$OUT/$s-{run}.txt" 2>&1 | grep "^run " || true
}

traced_run() { # traced_run <scenario> <args...>
  local s=$1; shift
  run "$s-trace" "$@" --arg "-Dorg.gradle.internal.operations.trace=$OUT/$s-trace"
  python3 -I "$HERE/aggregate_trace.py" "$OUT/$s-trace-log.txt" --md "$OUT/$s-models.md" >/dev/null
}

restore_edit() { (cd "$PROJECT" && git checkout -q -- "${EDIT_FILE#$PROJECT/}"); }
trap restore_edit EXIT

for s in "${SCENARIOS[@]}"; do
  case $s in
    1) # cold: daemon stopped before every run, CC off, IP off
       for i in $(seq 1 "$RUNS"); do stop_daemons; run "1-$i" "${PLAIN[@]}"; done
       stop_daemons; traced_run 1 "${PLAIN[@]}" ;;
    2) # warm, plain: daemon alive, CC off, IP off
       repeat 2 "${PLAIN[@]}"
       traced_run 2 "${PLAIN[@]}" ;;
    3) # warm, IP: project defaults (CC + IP on), entry present after run 0
       repeat 3
       traced_run 3 ;;
    4) # incremental, IP: a unique comment appended to one build script before every measured run
       repeat 4 --before-run "[ \"\$RUN\" -gt 0 ] && echo \"// wbs incremental edit $LABEL \$RUN \$(date +%s)\" >> '$EDIT_FILE' || true"
       echo "// wbs incremental edit $LABEL trace $(date +%s)" >> "$EDIT_FILE"
       traced_run 4
       restore_edit ;;
    5) # warm, IP + opt-in model cache (builders only)
       repeat 5 "${MODEL_CACHE[@]}"
       traced_run 5 "${MODEL_CACHE[@]}" ;;
    *) echo "unknown scenario $s" >&2; exit 2 ;;
  esac
done
echo "done: $OUT"
