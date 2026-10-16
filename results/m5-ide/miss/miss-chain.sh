#!/usr/bin/env bash
# Forced configuration cache miss (settings script content changed before every run, fresh IDE system dir), three rounds, round-robin over three configurations:
# stock IDE + stock sync; patched IDE with the task sync (D23) without the D24 import fixes; the same with the fixes.
set -u
cd /home/agent/projects/gradle-work-based-sync-experiment
S=/tmp/claude-1000/-home-agent-projects-gradle-work-based-sync-experiment/03bfd696-8798-4573-b4b6-36c58133e9f7/scratchpad
BM=~/projects/gradle-wbs-benchmark; IDEA=$(ls -d ~/tools/ideaIC/idea-* | head -1); OUT=$S/logs/miss
run() { # run <config> <round> <idea home> <vmoptions> <log dir>
  local cfg=$1 i=$2 home=$3 vm=$4 logdir=$5
  (cd $BM && ./gradlew -g ~/.gradle-wbs --stop -q 2>&1 | grep -v JAVA_TOOL)
  rm -rf $S/ide-system "$logdir"; mkdir -p $S/ide-system "$logdir"
  echo "// work-based sync experiment: forced configuration cache miss $cfg $i $(date +%s)" >> $BM/settings.gradle.kts
  echo "== $cfg round $i, settings script changed $(date -Is)"
  (export IDEA_VM_OPTIONS=$vm GRADLE_USER_HOME=$HOME/.gradle-wbs JAVA_HOME=/usr/lib/jvm/java-25-openjdk-arm64; cd $S; timeout 1800 $home/bin/idea.sh warmup --project-dir=$BM > $OUT/$cfg-$i.out 2>&1)
  cp "$logdir/idea.log" $OUT/$cfg-$i.log
  grep -hE "ConsoleLog - (\[Gradle\]: Gradle (resolve|data import) stage|.*Gradle Project' ended)" $OUT/$cfg-$i.log | grep -v STDOUT | sed -E 's/ \[ *[0-9]+\] +INFO - #c.i.w.u.ConsoleLog -//' | cut -c1-110
}
rm -rf $BM/.gradle/configuration-cache
for i in 1 2 3; do
  run stock $i "$IDEA" $S/ide-gg.vmoptions $S/ide-logs-gg
  WBS_PATCH_IDE_IMPORT=0 ide-plugin/install-patched-ide.sh "$IDEA" $S/idea-patched 2>&1 | grep -c "^patched" | sed 's/^/installed without import fixes, patched jars: /'
  run tasks $i $S/idea-patched $S/ide-only-gg.vmoptions $S/ide-logs-gg-patched
  ide-plugin/install-patched-ide.sh "$IDEA" $S/idea-patched 2>&1 | grep -c "^patched" | sed 's/^/installed with import fixes, patched jars: /'
  run tasks-fixed $i $S/idea-patched $S/ide-only-gg.vmoptions $S/ide-logs-gg-patched
done
git -C $BM checkout settings.gradle.kts && echo "settings script restored: $(git -C $BM status --short settings.gradle.kts | wc -l) changes"
echo "cc dir: $(du -sh $BM/.gradle/configuration-cache | cut -f1), disk free: $(df -h / | tail -1 | awk '{print $4}')"
echo "== all done $(date -Is)"
