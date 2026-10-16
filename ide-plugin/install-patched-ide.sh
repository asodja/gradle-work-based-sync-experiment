#!/usr/bin/env bash
# Makes a hard-linked copy of the installed IDE with the patched GradleModelFetchAction in
# plugins/gradle/lib/gradle-tooling-extension-impl.jar, GradleProjectResolver in gradle.jar, the memoizing
# AbstractIdeModifiableModelsProvider in lib/app.jar (D24, skip with WBS_PATCH_IDE_IMPORT=0) and the wbs-task-transport
# plugin in a custom plugins dir.
#   install-patched-ide.sh <idea-home> <target-dir>
set -euo pipefail
HERE=$(cd "$(dirname "$0")" && pwd)
IDEA=$1; TARGET=$2
rm -rf "$TARGET"; mkdir -p "$(dirname "$TARGET")"
cp -al "$IDEA" "$TARGET"
# The jar holding a class, relative to the IDE home: the layout and jar names differ between 2025.3
# (plugins/gradle/lib/gradle.jar, lib/app.jar) and 2026.2 (plugins/gradle-plugin/lib/intellij.gradle.jar,
# lib/intellij.platform.externalSystem.impl.jar).
jar_of() { # jar_of <class file path inside the jar>
  local jar
  for jar in "$IDEA"/lib/*.jar "$IDEA"/plugins/gradle/lib/*.jar "$IDEA"/plugins/gradle-plugin/lib/*.jar; do
    [[ -f "$jar" ]] || continue
    if unzip -l "$jar" "$1" >/dev/null 2>&1; then echo "${jar#"$IDEA"/}"; return; fi
  done
  echo "no jar in $IDEA holds $1" >&2; exit 1
}
declare -A COPIED
overlay() { # overlay <class file path of the class whose jar is patched> <class path glob>
  local REL; REL=$(jar_of "$1"); local JAR=$TARGET/$REL
  # a real copy, not the hard link; copied once per run, since several overlays can target one jar (2026.2)
  if [[ -z "${COPIED[$REL]:-}" ]]; then rm -f "$JAR"; cp "$IDEA/$REL" "$JAR"; COPIED[$REL]=1; fi
  (cd "$HERE/build/patched-classes" && jar uf "$JAR" $2)
  echo "patched $REL: $(cd "$HERE/build/patched-classes" && ls $2 | wc -l) classes"
}
overlay com/intellij/gradle/toolingExtension/impl/modelAction/GradleModelFetchAction.class "com/intellij/gradle/toolingExtension/impl/modelAction/GradleModelFetchAction*.class"
overlay org/jetbrains/plugins/gradle/service/project/GradleProjectResolver.class "org/jetbrains/plugins/gradle/service/project/GradleProjectResolver*.class"
if [[ "${WBS_PATCH_IDE_IMPORT:-1}" == 1 ]]; then
  overlay com/intellij/openapi/externalSystem/service/project/AbstractIdeModifiableModelsProvider.class "com/intellij/openapi/externalSystem/service/project/AbstractIdeModifiableModelsProvider*.class"
  overlay com/intellij/openapi/externalSystem/service/project/manage/LibraryDependencyDataService.class "com/intellij/openapi/externalSystem/service/project/manage/LibraryDependencyDataService*.class"
fi
mkdir -p "$TARGET-plugins"; rm -rf "$TARGET-plugins/wbs-task-transport"; cp -r "$HERE/build/plugin/wbs-task-transport" "$TARGET-plugins/"
echo "patched IDE: $TARGET"; echo "plugins dir:  $TARGET-plugins (use -Didea.plugins.path)"
echo "drop list:    $(java -cp "$TARGET-plugins/wbs-task-transport/lib/*:$IDEA/plugins/gradle/lib/*:$IDEA/plugins/gradle-plugin/lib/*:$IDEA/lib/*" wbs.ide.DropList 2>/dev/null)"
