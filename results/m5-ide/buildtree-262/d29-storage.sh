#!/usr/bin/env bash
# D29 fidelity: the IDE's external project storage after a tree-mode restart vs a model-mode restart (2026.2.2).
set -u
S=/tmp/claude-1000/-home-agent-projects-gradle-work-based-sync-experiment/03bfd696-8798-4573-b4b6-36c58133e9f7/scratchpad; OUT=$S/logs/d29; BM=~/projects/gradle-wbs-benchmark
run() { local cfg=$1 vm=$2
  (cd $BM && ./gradlew -g ~/.gradle-wbs --stop -q 2>&1 | grep -v JAVA_TOOL)
  rm -rf $S/ide-system $S/ide-logs-262; mkdir -p $S/ide-system $S/ide-logs-262
  echo "== $cfg $(date -Is)"
  (export IDEA_VM_OPTIONS=$vm GRADLE_USER_HOME=$HOME/.gradle-wbs JAVA_HOME=/usr/lib/jvm/java-25-openjdk-arm64; cd $S; timeout 1800 $S/idea-patched-262/bin/idea.sh warmup --project-dir=$BM > $OUT/storage-$cfg.out 2>&1; echo "exit $?")
  grep -hE "ConsoleLog - .*Gradle Project' ended" $S/ide-logs-262/idea.log | grep -v STDOUT | sed -E 's/ \[ *[0-9]+\] +INFO - #c.i.w.u.ConsoleLog -//' | cut -c1-110
  rm -rf $OUT/storage-$cfg; mkdir -p $OUT/storage-$cfg; cp -r "$(find $S/ide-system -maxdepth 3 -type d -name external_build_system | head -1)" $OUT/storage-$cfg/
  echo "storage files: $(find $OUT/storage-$cfg -name '*.xml' | wc -l)"
}
run tree $S/ide-262-tree.vmoptions
run model $S/ide-262.vmoptions
$S/logs/diff-ide-storage.sh $OUT/storage-tree/external_build_system $OUT/storage-model/external_build_system > $OUT/storage-diff.txt 2>&1; tail -1 $OUT/storage-diff.txt; grep -c "DIFFERENT\|ONLY IN" $OUT/storage-diff.txt | sed 's/^/differing or missing files: /'
echo "== storage check done $(date -Is)"
