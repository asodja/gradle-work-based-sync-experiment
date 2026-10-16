#!/usr/bin/env bash
# 2026.2.2: stock IDE + stock sync vs patched IDE + task sync (D24 fixes included), restart scenario (fresh system dir,
# shared config dir, Gradle caches kept), three rounds round-robin.
set -u
cd /home/agent/projects/gradle-work-based-sync-experiment
S=/tmp/claude-1000/-home-agent-projects-gradle-work-based-sync-experiment/03bfd696-8798-4573-b4b6-36c58133e9f7/scratchpad; OUT=$S/logs/d27b; BM=~/projects/gradle-wbs-benchmark
IDEA262=$HOME/tools/ideaIC/idea-IU-262.10315.125
run() { # run <cfg> <round> <idea home> <vmoptions> <log dir>
  local cfg=$1 i=$2 home=$3 vm=$4 logdir=$5
  (cd $BM && ./gradlew -g ~/.gradle-wbs --stop -q 2>&1 | grep -v JAVA_TOOL)
  rm -rf $S/ide-system "$logdir"; mkdir -p $S/ide-system "$logdir"
  echo "== $cfg round $i $(date -Is)"
  (export IDEA_VM_OPTIONS=$vm GRADLE_USER_HOME=$HOME/.gradle-wbs JAVA_HOME=/usr/lib/jvm/java-25-openjdk-arm64; cd $S; timeout 1800 $home/bin/idea.sh warmup --project-dir=$BM > $OUT/$cfg-$i.out 2>&1)
  cp "$logdir/idea.log" $OUT/$cfg-$i.log
  grep -hE "ConsoleLog - (\[Gradle\]: Gradle (resolve|data import) stage|.*Gradle Project' ended)" $OUT/$cfg-$i.log | grep -v STDOUT | sed -E 's/ \[ *[0-9]+\] +INFO - #c.i.w.u.ConsoleLog -//' | cut -c1-110
  grep -h "\[wbs\] plain model task run\|\[wbs\] GradleDslBaseScriptModel" $OUT/$cfg-$i.log 2>/dev/null | sed -E 's/^.*STDERR - //' | sort -u | cut -c1-120
}
for i in 1 2 3; do
  run stock $i "$IDEA262" $S/ide-262-stock.vmoptions $S/ide-logs-262-stock
  run tasks-fixed $i $S/idea-patched-262 $S/ide-262.vmoptions $S/ide-logs-262
done
echo "== all done $(date -Is)"
