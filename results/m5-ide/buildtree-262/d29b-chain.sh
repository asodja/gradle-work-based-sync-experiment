#!/usr/bin/env bash
# D29 on 2026.2.2: GradleBuild model request (model) vs ideBuildTree task (tree), restart scenario, three rounds
# round-robin, first with the daemon stopped before each run (cold), then with the daemon kept (warm).
set -u
S=/tmp/claude-1000/-home-agent-projects-gradle-work-based-sync-experiment/03bfd696-8798-4573-b4b6-36c58133e9f7/scratchpad; OUT=$S/logs/d29b; BM=~/projects/gradle-wbs-benchmark
run() { local cfg=$1 i=$2 vm=$3 daemon=$4
  [[ $daemon == cold ]] && (cd $BM && ./gradlew -g ~/.gradle-wbs --stop -q 2>&1 | grep -v JAVA_TOOL)
  rm -rf $S/ide-system $S/ide-logs-262; mkdir -p $S/ide-system $S/ide-logs-262
  echo "== $cfg-$daemon round $i $(date -Is)"
  (export IDEA_VM_OPTIONS=$vm GRADLE_USER_HOME=$HOME/.gradle-wbs JAVA_HOME=/usr/lib/jvm/java-25-openjdk-arm64; cd $S; timeout 1800 $S/idea-patched-262/bin/idea.sh warmup --project-dir=$BM > $OUT/$cfg-$daemon-$i.out 2>&1)
  cp $S/ide-logs-262/idea.log $OUT/$cfg-$daemon-$i.log
  grep -hE "ConsoleLog - (\[Gradle\]: Gradle (resolve|data import) stage|.*Gradle Project' ended)" $OUT/$cfg-$daemon-$i.log | grep -v STDOUT | sed -E 's/ \[ *[0-9]+\] +INFO - #c.i.w.u.ConsoleLog -//' | cut -c1-110
  grep -h "\[wbs\] plain model task run\|\[wbs\] build tree from tasks" $OUT/$cfg-$daemon-$i.log | sed -E 's/^.*STDERR - //' | sort -u | cut -c1-120
}
for daemon in cold warm; do
  for i in 1 2 3; do
    run model $i $S/ide-262.vmoptions $daemon
    run tree $i $S/ide-262-tree.vmoptions $daemon
  done
done
echo "== all done $(date -Is)"
