#!/usr/bin/env bash
# D26 part 2 (corrected): the IDE, restart scenario, compact then bytes; the bundles are deleted before the bytes
# config's first run so that its tasks execute and write Ion bundles (otherwise they stay up to date and the IDE
# would read the compact bundles in both configs). Then the fidelity check against those Ion bundles.
set -u
cd /home/agent/projects/gradle-work-based-sync-experiment
S=/tmp/claude-1000/-home-agent-projects-gradle-work-based-sync-experiment/03bfd696-8798-4573-b4b6-36c58133e9f7/scratchpad; OUT=$S/logs/d26; BM=~/projects/gradle-wbs-benchmark
IDEA=$(ls -d ~/tools/ideaIC/idea-* | head -1); LIB=harness/build/install/harness/lib
ccsize() { du -sm $BM/.gradle/configuration-cache 2>/dev/null | cut -f1; }
entries() { for d in $BM/.gradle/configuration-cache/*/; do [ -f $d/buildfingerprint.bin ] && echo "entry $(basename $d): $(du -sm $d | cut -f1) MB, strings $(du -k $d/.strings.work.bin 2>/dev/null | cut -f1) KB"; done; }
bundles() { find $BM -path '*/build/ide/models/ideProjectModel.bin' | sort; }
iderun() { local cfg=$1 i=$2
  (cd $BM && ./gradlew -g ~/.gradle-wbs --stop -q 2>&1 | grep -v JAVA_TOOL)
  rm -rf $S/ide-system $S/ide-logs-gg-patched; mkdir -p $S/ide-system $S/ide-logs-gg-patched
  echo "== IDE $cfg run $i $(date -Is)"
  (export IDEA_VM_OPTIONS=$S/ide-only-gg.vmoptions GRADLE_USER_HOME=$HOME/.gradle-wbs JAVA_HOME=/usr/lib/jvm/java-25-openjdk-arm64; cd $S; timeout 1800 $S/idea-patched/bin/idea.sh warmup --project-dir=$BM > $OUT/ide-$cfg-$i.out 2>&1)
  cp $S/ide-logs-gg-patched/idea.log $OUT/ide-$cfg-$i.log
  grep -hE "ConsoleLog - (\[Gradle\]: Gradle (resolve|data import) stage|.*Gradle Project' ended)" $OUT/ide-$cfg-$i.log | grep -v STDOUT | sed -E 's/ \[ *[0-9]+\] +INFO - #c.i.w.u.ConsoleLog -//' | cut -c1-110
  grep -h "\[wbs\] plain model task run\|\[wbs\] holder state built\|\[wbs\] bundles read during" $OUT/ide-$cfg-$i.out $OUT/ide-$cfg-$i.log 2>/dev/null | sort -u | cut -c1-160 | head -4
  echo "bundles: $(bundles | wc -l) files, $(bundles | xargs du -cb | tail -1 | cut -f1) bytes; formats in :core: $(java -cp "$LIB/*" wbs.harness.BundleDump $BM/subprojects/core/build/ide/models/ideProjectModel.bin 2>/dev/null | awk 'NR>1{print $2}' | sort | uniq -c | tr '\n' ' ')"
}
echo "== part 2: IDE restart scenario $(date -Is)"
sed -i '/^ideModelTasks.compact=/d' $BM/gradle.properties
rm -rf $BM/.gradle/configuration-cache
for i in 1 2 3; do iderun compact $i; done; entries
echo "ideModelTasks.compact=false" >> $BM/gradle.properties
rm -rf $BM/.gradle/configuration-cache; bundles | xargs rm -f; echo "bundles deleted: $(bundles | wc -l) left"
for i in 1 2 3; do iderun bytes $i; done; entries
echo "== fidelity (corrected): Ion bundles of the bytes config vs the compact dumps"
java -cp "$LIB/*" wbs.harness.BundleDump --text-all GradleSourceSetDependencyModel $OUT/text-bytes2 $(bundles) 2>&1 | grep -v JAVA_TOOL | head -3
same=0; diffn=0; for f in $OUT/text-bytes2/*.txt; do b=$(basename $f); if cmp -s $f $OUT/text-compact/$b; then same=$((same+1)); else diffn=$((diffn+1)); echo "DIFFERENT: $b"; fi; done; echo "identical=$same different=$diffn"; grep -l "^ERROR" $OUT/text-bytes2/*.txt | wc -l | sed 's/^/bytes read errors: /'
sed -i '/^ideModelTasks.compact=/d' $BM/gradle.properties
echo "cc dir: $(ccsize) MB, disk free: $(df -h / | tail -1 | awk '{print $4}')"
echo "== all done $(date -Is)"
