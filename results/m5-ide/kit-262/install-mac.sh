#!/bin/bash
# Installs the work-based-sync prototype (task-based Gradle sync + two IDE import fixes) into a COPY of
# IntelliJ IDEA 2026.2.2, build 262.10315.125, on macOS (Apple Silicon or Intel: the kit holds only Java classes).
#
#   ./install-mac.sh ["/Applications/IntelliJ IDEA.app"] ["/Applications/IntelliJ IDEA WBS.app"]
#
# The original app is not touched. The copy gets a "WBS" badge on its icon, its own name and bundle id, its own
# settings/caches directory (idea.paths.selector IntelliJIdea2026.2-WBS) and the JVM options that turn the task
# transport on. It is re-signed ad hoc, which macOS accepts for a locally modified app.
set -euo pipefail
HERE=$(cd "$(dirname "$0")" && pwd)
SRC=${1:-/Applications/IntelliJ IDEA.app}
DEST=${2:-/Applications/IntelliJ IDEA WBS.app}
EXPECTED=262.10315.125
die() { echo "error: $*" >&2; exit 1; }

[[ -d "$SRC/Contents" ]] || die "$SRC is not an app bundle"
BUILD=$(cat "$SRC/Contents/Resources/build.txt" 2>/dev/null || true)
[[ "$BUILD" == *"$EXPECTED" ]] || die "this kit is for build $EXPECTED; $SRC is '$BUILD' (download IntelliJ IDEA 2026.2.2)"
for tool in unzip codesign /usr/libexec/PlistBuddy; do command -v "$tool" >/dev/null || die "$tool not found"; done
if [[ -e "$DEST" ]]; then
  [[ -f "$DEST/Contents/Resources/wbs-kit.txt" ]] || die "$DEST exists and was not made by this kit; remove it or pass another target"
  rm -rf "$DEST"
fi

echo "copying $SRC -> $DEST (about 3 GB, takes a minute)"
cp -R "$SRC" "$DEST"
C="$DEST/Contents"

# The jar holding a class (names differ between IDE versions; found by content).
jar_of() {
  local jar
  for jar in "$C"/lib/*.jar "$C"/plugins/gradle-plugin/lib/*.jar; do
    [[ -f "$jar" ]] || continue
    if unzip -l "$jar" "$1" >/dev/null 2>&1; then echo "$jar"; return; fi
  done
  die "no jar in $C holds $1"
}
# The IDE's bundled JBR runs the overlay tool from source. Info-ZIP's zip -u must not be used: the IDE's jars carry a
# lookup index that would point at stale offsets afterwards, and the patched classes then fail to load.
JAVA="$C/jbr/Contents/Home/bin/java"; [[ -x "$JAVA" ]] || die "no bundled JBR at $JAVA"
overlay() { # overlay <class file path> <glob of class files to replace in its jar>
  local jar; jar=$(jar_of "$1")
  local classes; classes=$(cd "$HERE/patched-classes" && ls $2)
  (cd "$HERE/patched-classes" && "$JAVA" "$HERE/JarOverlay.java" "$jar" . $classes) || die "overlay into ${jar#"$C"/} failed"
}
overlay com/intellij/gradle/toolingExtension/impl/modelAction/GradleModelFetchAction.class "com/intellij/gradle/toolingExtension/impl/modelAction/GradleModelFetchAction*.class"
overlay org/jetbrains/plugins/gradle/service/project/GradleProjectResolver.class "org/jetbrains/plugins/gradle/service/project/GradleProjectResolver*.class"
overlay com/intellij/openapi/externalSystem/service/project/AbstractIdeModifiableModelsProvider.class "com/intellij/openapi/externalSystem/service/project/AbstractIdeModifiableModelsProvider*.class"
overlay com/intellij/openapi/externalSystem/service/project/manage/LibraryDependencyDataService.class "com/intellij/openapi/externalSystem/service/project/manage/LibraryDependencyDataService*.class"

rm -rf "$C/plugins/wbs-task-transport"
cp -R "$HERE/plugin/wbs-task-transport" "$C/plugins/"
echo "installed bundled plugin wbs-task-transport"

printf '\n# work-based sync prototype\n' >> "$C/bin/idea.vmoptions"
cat "$HERE/wbs.vmoptions" >> "$C/bin/idea.vmoptions"
echo "appended JVM options to Contents/bin/idea.vmoptions"

PB=/usr/libexec/PlistBuddy
$PB -c "Set :CFBundleName 'IntelliJ IDEA WBS'" "$C/Info.plist"
$PB -c "Set :CFBundleDisplayName 'IntelliJ IDEA WBS'" "$C/Info.plist" 2>/dev/null || $PB -c "Add :CFBundleDisplayName string 'IntelliJ IDEA WBS'" "$C/Info.plist"
$PB -c "Set :CFBundleIdentifier com.jetbrains.intellij.wbs" "$C/Info.plist"
echo "renamed to 'IntelliJ IDEA WBS' (bundle id com.jetbrains.intellij.wbs)"

# Icon: the IDE's SVG with a WBS badge, rendered by QuickLook, packed with iconutil (both macOS built-ins).
ICON=$($PB -c "Print :CFBundleIconFile" "$C/Info.plist"); [[ "$ICON" == *.icns ]] || ICON="$ICON.icns"
TMP=$(mktemp -d)
if qlmanage -t -s 1024 -o "$TMP" "$HERE/icon/idea-wbs.svg" >/dev/null 2>&1 && [[ -f "$TMP/idea-wbs.svg.png" ]]; then
  mkdir "$TMP/icon.iconset"
  for s in 16 32 128 256 512; do
    sips -z $s $s "$TMP/idea-wbs.svg.png" --out "$TMP/icon.iconset/icon_${s}x${s}.png" >/dev/null
    sips -z $((s*2)) $((s*2)) "$TMP/idea-wbs.svg.png" --out "$TMP/icon.iconset/icon_${s}x${s}@2x.png" >/dev/null
  done
  if iconutil -c icns "$TMP/icon.iconset" -o "$C/Resources/$ICON"; then echo "icon replaced ($ICON)"; else echo "warning: iconutil failed, icon unchanged"; fi
else
  echo "warning: could not render the badge icon with qlmanage; icon unchanged"
fi
rm -rf "$TMP"

echo "$EXPECTED installed $(date)" > "$C/Resources/wbs-kit.txt"
codesign --force --deep --sign - "$DEST" 2>/dev/null || die "ad hoc codesign failed"
xattr -dr com.apple.quarantine "$DEST" 2>/dev/null || true
echo
echo "done: $DEST"
echo "First start: it has its own settings directory (IntelliJIdea2026.2-WBS); import settings from the original if asked."
echo "Verify after a Gradle sync: ~/Library/Logs/JetBrains/IntelliJIdea2026.2-WBS/idea.log contains '[wbs] plain model task run'."
