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
jars, installs the plugin `wbs-task-transport` into the copy's user plugin directory
(`~/Library/Application Support/JetBrains/IntelliJIdea2026.2-WBS/plugins`; the bundled plugin directory is indexed
and not scanned in 2026.2), appends `wbs.vmoptions` to `Contents/bin/idea.vmoptions`,
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

## Re-sync error "removeContentEntry: removed content entry url … still exists after removing"

An IDE issue, not specific to this kit: the IDE's content-root import matches existing content entries through the
directory on disk, so a content root whose directory does not exist at sync time (for example
`build/generated/sources/annotationProcessor/java/main` before the first build) gets a duplicate entry on each re-sync,
and once that directory leaves the sync data the removal of one duplicate trips a workspace-model assertion. The sync
itself completes. To clear the duplicates: quit the WBS app, delete
`~/Library/Caches/JetBrains/IntelliJIdea2026.2-WBS`, start it and sync again. Keeping such directories present
(build once) avoids the toggle.

## Headless re-sync (for measurements)

    "/Applications/IntelliJ IDEA WBS.app/Contents/MacOS/idea" wbsResync /path/to/project 2

opens an already imported project and syncs it twice without a UI; timings in the log as `[wbs-resync] sync N finished`.

## Measuring with gradle-profiler (0.26+)

One command for stock vs WBS with a merged report (needs gradle-profiler on the PATH and python3):

    ./compare.sh "/Users/<you>/Applications/IntelliJ IDEA.app" "/Applications/IntelliJ IDEA WBS.app" ~/workspace/gradle

Runs `sync_miss_settings` and `sync_hit` (1 warm-up, 3 iterations) for both IDEs with the full IDE, then prints and
saves `report.md` in `~/wbs-profiler-<timestamp>`. Tunables: `SCENARIOS`, `ITERATIONS`, `WARMUPS`, `GRADLE_USER_HOME`.
Details of what the profiler does and why the two adaptations below are needed:

gradle-profiler's `idea-sync` scenarios run the IDE in a sandbox: it starts the app's launcher with `IDEA_VM_OPTIONS`
pointing at its own vmoptions file (sandbox config/system/plugins/log paths, its agent, the scenario's `ide-jvm-args`),
which replaces the app's `Contents/bin/idea.vmoptions`, and the sandbox has its own `plugins` directory. The included
`run-idea-sync.sh` handles both: it copies the plugin into `<sandbox>/plugins` (the profiler leaves other plugins there
alone) and puts the `-Dwbs.*` options into `ide-jvm-args`; the patched classes come with the WBS app itself.

    WBS_KIT=$(pwd) ./run-idea-sync.sh "/Applications/IntelliJ IDEA WBS.app" ~/workspace/gradle ~/wbs-sandbox ~/wbs-results wbs
    ./run-idea-sync.sh "/Users/<you>/Applications/IntelliJ IDEA.app" ~/workspace/gradle ~/stock-sandbox ~/stock-results stock

Scenarios in the generated file: `sync_hit` (cache kept), `sync_miss_settings` (settings script changed before every
sync), `sync_miss_cleared` (`.gradle/configuration-cache` deleted before every sync); pass scenario names after the mode
to run a subset, and other gradle-profiler options (e.g. `--iterations 5`). Use separate sandbox and output
directories per mode. Known limit: the profiler's agent only intercepts the stock sync's build action, so for the WBS
runs it reports the Gradle part as 0 ms and cannot inject `--gradle-version`; the total sync time is measured for both.
The IDE stays open across the iterations of a scenario, so hits run against a warm daemon and a warm IDE.

## Remove

Delete "IntelliJ IDEA WBS.app" and `~/Library/Application Support/JetBrains/IntelliJIdea2026.2-WBS` (holds the
plugin and the settings), and if wanted
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
