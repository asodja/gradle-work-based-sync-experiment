#!/usr/bin/env bash
# Field-by-field comparison of builder-mode and task-mode model dumps written by the harness (--dump-models).
#
#   compare-models.sh <builders-dump-dir> <tasks-dump-dir> [<model class simple name>...]
#
# Default models: the ported ones. Prints identical / different / missing per project file; for differences,
# the canonical Ion text diff (struct fields sorted, map entries sorted by key). Exit 1 if anything differs.
set -euo pipefail
B=$1; T=$2; shift 2
MODELS=("$@"); [ ${#MODELS[@]} -eq 0 ] && MODELS=(GradleSourceSetModel)
HERE=$(cd "$(dirname "$0")" && pwd)
LIB=$HERE/../harness/build/install/harness/lib
ion() { java -cp "$LIB/*" wbs.harness.IonText --canonical "$1" 2>/dev/null; }
rc=0
for m in "${MODELS[@]}"; do
  for bdir in "$B"/"$m"*/; do
    cls=$(basename "$bdir"); tdir=$T/$cls
    same=0; diffn=0; missing=0
    while IFS= read -r -d '' f; do
      rel=${f#$bdir}
      if [ ! -f "$tdir/$rel" ]; then missing=$((missing+1)); echo "MISSING in tasks: $cls/$rel"; continue; fi
      if cmp -s "$f" "$tdir/$rel" || diff -q <(ion "$f") <(ion "$tdir/$rel") >/dev/null; then same=$((same+1)); else
        diffn=$((diffn+1)); echo "DIFFERENT: $cls/$rel"; diff <(ion "$f") <(ion "$tdir/$rel") | head -40; fi
    done < <(find "$bdir" -type f -name '*.bin' -print0 | sort -z)
    extra=$(find "$tdir" -type f -name '*.bin' 2>/dev/null | while read -r f; do [ -f "$bdir/${f#$tdir/}" ] || echo "$f"; done | wc -l)
    echo "$cls: identical=$same different=$diffn missing=$missing extra-in-tasks=$extra"
    [ "$diffn" -eq 0 ] && [ "$missing" -eq 0 ] || rc=1
  done
done
exit $rc
