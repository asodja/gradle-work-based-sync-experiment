#!/usr/bin/env bash
# D26 on gradle/gradle: compact state (default) vs bytes state (-PideModelTasks.compact=false). Part 1: launcher harness,
# warm daemon, traced misses and hits, entry sizes, fidelity of the expanded dependency model, bundle recreation.
# Part 2: the IDE, restart scenario (fresh system dir), run 1 stores, runs 2-3 hit; compact then bytes.
set -u
cd /home/agent/projects/gradle-work-based-sync-experiment
S=/tmp/claude-1000/-home-agent-projects-gradle-work-based-sync-experiment/03bfd696-8798-4573-b4b6-36c58133e9f7/scratchpad; OUT=$S/logs/d26; BM=~/projects/gradle-wbs-benchmark
IDEA=$(ls -d ~/tools/ideaIC/idea-* | head -1); H=harness/build/install/harness/bin/harness; JAR=$PWD/prototype/build/libs/ide-model-tasks.jar; LIB=harness/build/install/harness/lib
COMMON=(--project "$BM" --gradle-user-home "$HOME/.gradle-wbs" --idea-home "$IDEA" --arg --no-scan --arg --no-build-cache --launcher --extra-tooling-jar "$JAR")
ccsize() { du -sm $BM/.gradle/configuration-cache 2>/dev/null | cut -f1; }
entries() { for d in $BM/.gradle/configuration-cache/*/; do [ -f $d/buildfingerprint.bin ] && echo "entry $(basename $d): $(du -sm $d | cut -f1) MB, strings $(du -k $d/.strings.work.bin 2>/dev/null | cut -f1) KB"; done; }
bundles() { find $BM -path '*/build/ide/models/ideProjectModel.bin' | sort; }
run() { local name=$1; shift; local before=$(ccsize); echo "== $name $(date -Is)"; $H "${COMMON[@]}" "$@" --out $OUT/$name.txt 2>&1 | grep "^run " | cut -c1-110; grep -hE "Reusing configuration cache|Calculating task graph|BUILD SUCCESSFUL|actionable|Configuration cache entry|problems were found|problem" $OUT/$name.txt.build.log | head -6 | cut -c1-120; grep -h "bundlesRead_ms\|bundlesRead " $OUT/$name.txt | tr '\n' ' '; echo; echo "cc dir: $before -> $(ccsize) MB"; }
traced() { local name=$1; shift; run $name "$@" --arg "-Dorg.gradle.internal.operations.trace=$OUT/$name-trace"; python3 -I $S/trace-phases.py $OUT/$name-trace-log.txt 300 > $OUT/$name-phases.txt 2>&1; grep -E "overall span|Store configuration cache state|Load configuration cache state|Check configuration cache fingerprint|Run main tasks|Configure build$" $OUT/$name-phases.txt | awk '!seen[$0]++' | head -8; rm -f $OUT/$name-trace-log.txt; }
echo "== part 1: launcher $(date -Is)"
run compact-coldwarmup
rm -rf $BM/.gradle/configuration-cache
traced compact-miss; entries
traced compact-hit
echo "bundles: $(bundles | wc -l) files, $(bundles | xargs du -cb | tail -1 | cut -f1) bytes"
java -cp "$LIB/*" wbs.harness.BundleDump --text-all GradleSourceSetDependencyModel $OUT/text-compact $(bundles) 2>&1 | grep -v JAVA_TOOL | head -3
java -cp "$LIB/*" wbs.harness.BundleDump $BM/subprojects/core/build/ide/models/ideProjectModel.bin 2>&1 | grep -v JAVA_TOOL | awk '{print $1, $2, $3, $4, $5}' | cut -c1-140 > $OUT/core-bundle-compact.txt
echo "== delete :core's bundle, hit again (compact): must be recreated"
rm -f $BM/subprojects/core/build/ide/models/ideProjectModel.bin
run compact-recreate
ls -la $BM/subprojects/core/build/ide/models/ideProjectModel.bin | awk '{print "core bundle:", $5, "bytes"}'
rm -rf $BM/.gradle/configuration-cache
traced bytes-miss --arg -PideModelTasks.compact=false; entries
traced bytes-hit --arg -PideModelTasks.compact=false
echo "bundles: $(bundles | wc -l) files, $(bundles | xargs du -cb | tail -1 | cut -f1) bytes"
java -cp "$LIB/*" wbs.harness.BundleDump --text-all GradleSourceSetDependencyModel $OUT/text-bytes $(bundles) 2>&1 | grep -v JAVA_TOOL | head -3
java -cp "$LIB/*" wbs.harness.BundleDump $BM/subprojects/core/build/ide/models/ideProjectModel.bin 2>&1 | grep -v JAVA_TOOL | awk '{print $1, $2, $3, $4, $5}' | cut -c1-140 > $OUT/core-bundle-bytes.txt
echo "== fidelity: expanded compact model vs Ion model, canonical text per project"
same=0; diffn=0; for f in $OUT/text-bytes/*.txt; do b=$(basename $f); if cmp -s $f $OUT/text-compact/$b; then same=$((same+1)); else diffn=$((diffn+1)); echo "DIFFERENT: $b"; fi; done; echo "identical=$same different=$diffn"; grep -l "^ERROR" $OUT/text-compact/*.txt | wc -l | sed 's/^/compact read errors: /'
echo "== part 2: IDE restart scenario $(date -Is)"
ide-plugin/install-patched-ide.sh "$IDEA" $S/idea-patched 2>&1 | grep -c "^patched" | sed 's/^/installed, patched jars: /'
iderun() { local cfg=$1 i=$2
  (cd $BM && ./gradlew -g ~/.gradle-wbs --stop -q 2>&1 | grep -v JAVA_TOOL)
  rm -rf $S/ide-system $S/ide-logs-gg-patched; mkdir -p $S/ide-system $S/ide-logs-gg-patched
  echo "== IDE $cfg run $i $(date -Is)"
  (export IDEA_VM_OPTIONS=$S/ide-only-gg.vmoptions GRADLE_USER_HOME=$HOME/.gradle-wbs JAVA_HOME=/usr/lib/jvm/java-25-openjdk-arm64; cd $S; timeout 1800 $S/idea-patched/bin/idea.sh warmup --project-dir=$BM > $OUT/ide-$cfg-$i.out 2>&1)
  cp $S/ide-logs-gg-patched/idea.log $OUT/ide-$cfg-$i.log
  grep -hE "ConsoleLog - (\[Gradle\]: Gradle (resolve|data import) stage|.*Gradle Project' ended)" $OUT/ide-$cfg-$i.log | grep -v STDOUT | sed -E 's/ \[ *[0-9]+\] +INFO - #c.i.w.u.ConsoleLog -//' | cut -c1-110
  grep -h "\[wbs\] plain model task run\|\[wbs\] holder state built\|\[wbs\] bundles read during" $OUT/ide-$cfg-$i.out $OUT/ide-$cfg-$i.log 2>/dev/null | sort -u | cut -c1-160 | head -4
}
sed -i '/^ideModelTasks.compact=/d' $BM/gradle.properties
rm -rf $BM/.gradle/configuration-cache
for i in 1 2 3; do iderun compact $i; done; entries
echo "ideModelTasks.compact=false" >> $BM/gradle.properties
rm -rf $BM/.gradle/configuration-cache
for i in 1 2 3; do iderun bytes $i; done; entries
sed -i '/^ideModelTasks.compact=/d' $BM/gradle.properties
echo "cc dir: $(ccsize) MB, disk free: $(df -h / | tail -1 | awk '{print $4}')"
echo "== all done $(date -Is)"
