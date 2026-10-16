#!/usr/bin/env bash
# D29 functional check on 2026.2.2: restart run in tree mode (no GradleBuild request), then in model mode; the IDE's
# external storage of both is kept for comparison.
set -u
S=/tmp/claude-1000/-home-agent-projects-gradle-work-based-sync-experiment/03bfd696-8798-4573-b4b6-36c58133e9f7/scratchpad; OUT=$S/logs/d29; BM=~/projects/gradle-wbs-benchmark
run() { local cfg=$1 vm=$2
  (cd $BM && ./gradlew -g ~/.gradle-wbs --stop -q 2>&1 | grep -v JAVA_TOOL)
  rm -rf $S/ide-system $S/ide-logs-262; mkdir -p $S/ide-system $S/ide-logs-262
  echo "== $cfg $(date -Is)"
  (export IDEA_VM_OPTIONS=$vm GRADLE_USER_HOME=$HOME/.gradle-wbs JAVA_HOME=/usr/lib/jvm/java-25-openjdk-arm64; cd $S; timeout 1800 $S/idea-patched-262/bin/idea.sh warmup --project-dir=$BM > $OUT/$cfg.out 2>&1; echo "exit $?")
  cp $S/ide-logs-262/idea.log $OUT/$cfg.log
  grep -hE "ConsoleLog - (\[Gradle\]: Gradle (resolve|data import) stage|.*Gradle Project' ended)" $OUT/$cfg.log | grep -v STDOUT | sed -E 's/ \[ *[0-9]+\] +INFO - #c.i.w.u.ConsoleLog -//' | cut -c1-110
  grep -h "\[wbs\]" $OUT/$cfg.log | sed -E 's/^.*STDERR - //' | grep -v "dropping provider" | sort -u | cut -c1-160
  grep -c "IllegalState\|Exception.*wbs\|ideModels\|Isolated.*problem" $OUT/$cfg.log | sed 's/^/suspicious log lines: /'
  rm -rf $OUT/storage-$cfg; cp -r $S/ide-system/external_build_system $OUT/storage-$cfg 2>/dev/null; echo "storage files: $(find $OUT/storage-$cfg -name '*.xml' 2>/dev/null | wc -l)"
}
run tree-store $S/ide-262-tree.vmoptions   # first run after the prototype rebuild: the new tasks exist only in a fresh entry
run tree $S/ide-262-tree.vmoptions
run model $S/ide-262.vmoptions
$S/logs/diff-ide-storage.sh $OUT/storage-tree $OUT/storage-model | tail -3
echo "== smoke done $(date -Is)"
