#!/usr/bin/env bash
set -u
S=/tmp/claude-1000/-home-agent-projects-gradle-work-based-sync-experiment/03bfd696-8798-4573-b4b6-36c58133e9f7/scratchpad; OUT=$S/logs/d41; BM=~/projects/gradle-wbs-benchmark
for mode in none scripts; do
  (cd $BM && ./gradlew -g ~/.gradle-wbs --stop -q 2>&1 | grep -v JAVA_TOOL)
  rm -rf $S/ide-system $S/ide-logs-262; mkdir -p $S/ide-system $S/ide-logs-262
  vm=$S/ide-262-tree.vmoptions; [[ $mode == none ]] && vm=$S/ide-262-none.vmoptions
  echo "== $mode: import $(date -Is)"
  (export IDEA_VM_OPTIONS=$vm GRADLE_USER_HOME=$HOME/.gradle-wbs JAVA_HOME=/usr/lib/jvm/java-25-openjdk-arm64; cd $S; timeout 1800 $S/idea-patched-262/bin/idea.sh warmup --project-dir=$BM > $OUT/$mode-import.out 2>&1; echo "exit $?")
  grep -hE "ConsoleLog - .*Gradle Project' ended" $S/ide-logs-262/idea.log | grep -v STDOUT | sed -E 's/ \[ *[0-9]+\] +INFO - #c.i.w.u.ConsoleLog -//' | cut -c1-110
  echo "== $mode: resync $(date -Is)"
  (export IDEA_VM_OPTIONS=$vm GRADLE_USER_HOME=$HOME/.gradle-wbs JAVA_HOME=/usr/lib/jvm/java-25-openjdk-arm64; cd $S; timeout 1800 $S/idea-patched-262/bin/idea.sh wbsResync $BM 1 > $OUT/$mode-resync.out 2>&1; echo "exit $?")
  cp $S/ide-logs-262/idea.log $OUT/$mode.log
  grep -h "wbs-resync\] sync\|sync contributors: all done" $OUT/$mode-resync.out $OUT/$mode.log | sed -E 's/^.*STDERR - //' | sort -u | cut -c1-100
  echo "errors: $(grep -c ' ERROR - ' $OUT/$mode.log)"
done
echo "== done $(date -Is)"
