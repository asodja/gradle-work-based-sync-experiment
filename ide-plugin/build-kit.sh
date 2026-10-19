#!/usr/bin/env bash
# Builds the Mac kit: prototype jar + IDE plugin + patched IDE classes + kit sources + profiler scripts into
# dist/wbs-ide-kit-<build>.tar.gz.
#
#   ide-plugin/build-kit.sh [<IDE home>]      default: ~/tools/ideaIC/idea-IU-262.10315.125
#
# The IDE home's build.txt names the build (IU-262.10315.125); its first three digits select the patched sources
# (src/patched-<branch>/java, src/patched/java for 253).
set -euo pipefail
HERE=$(cd "$(dirname "$0")" && pwd)
REPO=$(dirname "$HERE")
IDEA=${1:-$HOME/tools/ideaIC/idea-IU-262.10315.125}
BUILD=$(sed -E 's/^[A-Z]+-//' "$IDEA/build.txt")
BRANCH=${BUILD%%.*}
KIT=wbs-ide-kit-$BUILD
OUT=$HERE/build/kit/$KIT

(cd "$REPO/prototype" && ./gradlew -q jar -PideaHome="$IDEA")
(cd "$HERE" && ./gradlew -q assemble -PideaHome="$IDEA" -PideaBranch="$BRANCH")

rm -rf "$OUT"; mkdir -p "$OUT"
cp -R "$HERE/kit/." "$OUT/"
cp "$REPO"/profiler/{compare.sh,merge-report.py,run-idea-sync.sh,stock.scenarios,wbs.scenarios} "$OUT/"
cp -R "$HERE/build/plugin" "$HERE/build/patched-classes" "$OUT/"
mkdir -p "$REPO/dist"
tar czf "$REPO/dist/$KIT.tar.gz" -C "$HERE/build/kit" "$KIT"
echo "$REPO/dist/$KIT.tar.gz ($(wc -c < "$REPO/dist/$KIT.tar.gz") bytes, prototype jar $(sha256sum "$HERE/build/plugin/wbs-task-transport/lib/ide-model-tasks.jar" | cut -c1-16))"
