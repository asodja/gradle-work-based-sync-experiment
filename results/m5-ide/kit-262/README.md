# Work-based sync prototype for IntelliJ IDEA 2026.2.2 (build 262.10315.125), macOS kit

What it is: the Gradle sync fetches per-project IDE models from Gradle *tasks* (one `ideProjectModel` task per
project, run as a plain task run that hits the configuration cache after a restart) instead of IntelliJ's model
builders inside a build action; plus two fixes in the IDE's import (quadratic library lookups). Measured on
gradle/gradle (287 projects): restart sync 1 m 20 s stock vs 28–29 s; cache-miss syncs 25–43 % faster.

## Requirements

- IntelliJ IDEA 2026.2.2, build **262.10315.125** (Ultimate or Community, Apple Silicon or Intel), installed.
  The kit patches a *copy* of it; the class files match this build only.
- The project under sync: Gradle 9.x with the configuration cache and Isolated Projects enabled in its
  `gradle.properties` (`org.gradle.configuration-cache=true`, `org.gradle.isolated-projects=true`); gradle/gradle
  has them. Other builds work too, but gain less without them.

## Install

    ./install-mac.sh "/Applications/IntelliJ IDEA.app" "/Applications/IntelliJ IDEA WBS.app"

Both arguments are optional (these are the defaults). The script copies the app, replaces four classes in three
jars, adds the bundled plugin `wbs-task-transport`, appends `wbs.vmoptions` to `Contents/bin/idea.vmoptions`,
renames the copy to "IntelliJ IDEA WBS" with a badged icon and its own settings directory
(`~/Library/Application Support/JetBrains/IntelliJIdea2026.2-WBS`), and re-signs it ad hoc. The original app is untouched.

## Use

Open a Gradle project in "IntelliJ IDEA WBS" and sync as usual. The first sync of a project configures the build and
stores the configuration cache entry (about as long as a stock sync); every later sync after an IDE or daemon restart
is a task run that hits the cache. To check that the transport is active, look in
`~/Library/Logs/JetBrains/IntelliJIdea2026.2-WBS/idea.log` for `[wbs] plain model task run: N projects in ... ms`
and `[wbs] holder state built from bundles`.

Build output of the tasks: `<project>/build/ide/models/ideProjectModel.bin` per project.

## Known limits

- The first import of a project also resolves what the IDE's "Download sources" setting asks for; with the 2026.2
  default (on) the first import of gradle/gradle took 2 m 16 s on the test machine, later syncs 30 s.
- 2026.2's Kotlin plugin takes `GradleDslBaseScriptModel` from the sync; the kit requests it separately (log line
  `[wbs] GradleDslBaseScriptModel fetched`). Kotlin DSL script highlighting was not verified on a Mac.

- Prototype: Kotlin MPP and Android models are not ported (they still go through the stock path if present).
- A change of the kit's own jar does not invalidate an existing configuration cache entry (Gradle finding); after
  updating the kit, run once with `--rerun-tasks` or change any build script.
- The Dependency Analyzer's graph model is still produced eagerly (the largest payload); nothing else is dropped.

## Remove

Delete "IntelliJ IDEA WBS.app" and, if wanted, `~/Library/Application Support/JetBrains/IntelliJIdea2026.2-WBS`,
`~/Library/Caches/JetBrains/IntelliJIdea2026.2-WBS` and `~/Library/Logs/JetBrains/IntelliJIdea2026.2-WBS`.

## Contents

- `patched-classes/`: GradleModelFetchAction (tooling extension), GradleProjectResolver (Gradle plugin),
  AbstractIdeModifiableModelsProvider and LibraryDependencyDataService (platform external-system)
- `plugin/wbs-task-transport/lib/`: the plugin jar and `ide-model-tasks.jar` (the Gradle-side tasks, put on the
  init script classpath by the plugin)
- `JarOverlay.java`: the jar patcher the installer runs with the IDE's bundled JBR (the IDE's jars carry a lookup
  index that `zip -u` would leave stale)
- `wbs.vmoptions`: `-Dwbs.transport=tasks`, the provider drop list, `-Dwbs.transport.run=only`, the paths selector
- `icon/idea-wbs.svg`: the badged icon source
