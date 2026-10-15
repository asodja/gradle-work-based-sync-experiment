#!/usr/bin/env bash
# Compare one model class between two dumps the way the IDE reads it: all documents of the class deserialized in
# consumption order with one serializer, then canonical reflective text per project, diffed.
#   compare-ordered.sh <builders-dump> <tasks-dump> <ClassSimpleName> <fully.qualified.ClassName>
set -uo pipefail
B=$1; T=$2; SIMPLE=$3; FQN=$4
LIB=$(cd "$(dirname "$0")" && pwd)/../harness/build/install/harness/lib
for d in "$B" "$T"; do dir=$(ls -d "$d"/"$SIMPLE"*/ | head -1); java -cp "$LIB/*" wbs.harness.OrderedModelText "$d" "$(basename "$dir")" "$FQN" 2>/dev/null | tail -1; done
bdir=$(ls -d "$B"/"$SIMPLE"*/ | head -1); tdir=$T/$(basename "$bdir")
same=0; diffn=0; missing=0
while IFS= read -r -d '' f; do
  rel=${f#$bdir}
  if [ ! -f "$tdir/$rel" ]; then missing=$((missing+1)); echo "MISSING in tasks: $rel"; continue; fi
  if diff -q "$f" "$tdir/$rel" >/dev/null; then same=$((same+1)); else diffn=$((diffn+1)); echo "DIFFERENT: $rel"; diff "$f" "$tdir/$rel" | head -30; fi
done < <(find "$bdir" -type f -name '*.bin.txt' -print0 | sort -z)
echo "$SIMPLE: identical=$same different=$diffn missing=$missing"
