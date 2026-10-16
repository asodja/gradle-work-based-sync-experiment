#!/usr/bin/env bash
# 2026.2.2, part A: forced configuration cache miss (settings script content changed before every run, fresh IDE system
# dir, shared config dir), three rounds round-robin: stock / tasks without the import fixes / tasks with fixes.
# Part B: restart scenario (cache kept) for tasks without the import fixes, three rounds, to complete the hit table.
set -u
cd /home/agent/projects/gradle-work-based-sync-experiment
S=/tmp/claude-1000/-home-agent-projects-gradle-work-based-sync-experiment/03bfd696-8798-4573-b4b6-36c58133e9f7/scratchpad
BM=~/projects/gradle-wbs-benchmark; IDEA=$HOME/tools/ideaIC/idea-IU-262.10315.125
run() { # run <out dir> <config> <round> <idea home> <vmoptions> <log dir> <miss|hit>
  local OUT=$1 cfg=$2 i=$3 home=$4 vm=$5 logdir=$6 kind=$7
  (cd $BM && ./gradlew -g ~/.gradle-wbs --stop -q 2>&1 | grep -v JAVA_TOOL)
  rm -rf $S/ide-system "$logdir"; mkdir -p $S/ide-system "$logdir"
  [[ $kind == miss ]] && echo "// work-based sync experiment: forced configuration cache miss $cfg $i $(date +%s)" >> $BM/settings.gradle.kts
  echo "== $cfg round $i ($kind) $(date -Is)"
  (export IDEA_VM_OPTIONS=$vm GRADLE_USER_HOME=$HOME/.gradle-wbs JAVA_HOME=/usr/lib/jvm/java-25-openjdk-arm64; cd $S; timeout 1800 $home/bin/idea.sh warmup --project-dir=$BM > $OUT/$cfg-$i.out 2>&1)
  cp "$logdir/idea.log" $OUT/$cfg-$i.log
  grep -hE "ConsoleLog - (\[Gradle\]: Gradle (resolve|data import) stage|.*Gradle Project' ended)" $OUT/$cfg-$i.log | grep -v STDOUT | sed -E 's/ \[ *[0-9]+\] +INFO - #c.i.w.u.ConsoleLog -//' | cut -c1-110
  grep -h "\[wbs\] plain model task run" $OUT/$cfg-$i.log 2>/dev/null | sed -E 's/^.*STDERR - //' | sort -u | cut -c1-80
}
echo "== part A: forced miss $(date -Is)"
for i in 1 2 3; do
  run $S/logs/d27c stock $i "$IDEA" $S/ide-262-stock.vmoptions $S/ide-logs-262-stock miss
  WBS_PATCH_IDE_IMPORT=0 ide-plugin/install-patched-ide.sh "$IDEA" $S/idea-patched-262 2>&1 | grep -c "^patched" | sed 's/^/installed without import fixes, patched jars: /'
  run $S/logs/d27c tasks $i $S/idea-patched-262 $S/ide-262.vmoptions $S/ide-logs-262 miss
  ide-plugin/install-patched-ide.sh "$IDEA" $S/idea-patched-262 2>&1 | grep -c "^patched" | sed 's/^/installed with import fixes, patched jars: /'
  run $S/logs/d27c tasks-fixed $i $S/idea-patched-262 $S/ide-262.vmoptions $S/ide-logs-262 miss
done
git -C $BM checkout settings.gradle.kts && echo "settings script restored: $(git -C $BM status --short settings.gradle.kts | wc -l) changes"
echo "== part B: restart hits, tasks without import fixes $(date -Is)"
WBS_PATCH_IDE_IMPORT=0 ide-plugin/install-patched-ide.sh "$IDEA" $S/idea-patched-262 2>&1 | grep -c "^patched" | sed 's/^/installed without import fixes, patched jars: /'
run $S/logs/d27d tasks 0 $S/idea-patched-262 $S/ide-262.vmoptions $S/ide-logs-262 hit   # warm-up: stores the entry for the restored settings script
for i in 1 2 3; do run $S/logs/d27d tasks $i $S/idea-patched-262 $S/ide-262.vmoptions $S/ide-logs-262 hit; done
ide-plugin/install-patched-ide.sh "$IDEA" $S/idea-patched-262 2>&1 | grep -c "^patched" | sed 's/^/reinstalled with import fixes, patched jars: /'
echo "cc dir: $(du -sh $BM/.gradle/configuration-cache | cut -f1), disk free: $(df -h / | tail -1 | awk '{print $4}')"
echo "== all done $(date -Is)"
