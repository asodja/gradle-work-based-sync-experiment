#!/usr/bin/env bash
# D29 on 2026.2.2, forced configuration cache miss (settings script content changed before every run, fresh IDE system
# dir, daemon stopped): GradleBuild model request (model) vs ideBuildTree task (tree), three rounds round-robin.
set -u
S=/tmp/claude-1000/-home-agent-projects-gradle-work-based-sync-experiment/03bfd696-8798-4573-b4b6-36c58133e9f7/scratchpad; OUT=$S/logs/d29d; BM=~/projects/gradle-wbs-benchmark
run() { local cfg=$1 i=$2 vm=$3
  (cd $BM && ./gradlew -g ~/.gradle-wbs --stop -q 2>&1 | grep -v JAVA_TOOL)
  rm -rf $S/ide-system $S/ide-logs-262; mkdir -p $S/ide-system $S/ide-logs-262
  echo "// work-based sync experiment: forced configuration cache miss $cfg $i $(date +%s)" >> $BM/settings.gradle.kts
  echo "== $cfg round $i (miss) $(date -Is)"
  (export IDEA_VM_OPTIONS=$vm GRADLE_USER_HOME=$HOME/.gradle-wbs JAVA_HOME=/usr/lib/jvm/java-25-openjdk-arm64; cd $S; timeout 1800 $S/idea-patched-262/bin/idea.sh warmup --project-dir=$BM > $OUT/$cfg-$i.out 2>&1)
  cp $S/ide-logs-262/idea.log $OUT/$cfg-$i.log
  grep -hE "ConsoleLog - (\[Gradle\]: Gradle (resolve|data import) stage|.*Gradle Project' ended)" $OUT/$cfg-$i.log | grep -v STDOUT | sed -E 's/ \[ *[0-9]+\] +INFO - #c.i.w.u.ConsoleLog -//' | cut -c1-110
  grep -h "\[wbs\] plain model task run\|\[wbs\] build tree from tasks\|\[wbs\] GradleDslBaseScriptModel" $OUT/$cfg-$i.log | sed -E 's/^.*STDERR - //' | sort -u | cut -c1-120
}
for i in 1 2 3; do
  run model $i $S/ide-262.vmoptions
  run tree $i $S/ide-262-tree.vmoptions
done
git -C $BM checkout settings.gradle.kts && echo "settings script restored: $(git -C $BM status --short settings.gradle.kts | wc -l) changes"
echo "== all done $(date -Is)"
