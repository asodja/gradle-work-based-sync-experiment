# Progress

Keep this file current. A resuming agent continues from here alone.

## Current milestone

M0–M4 done, D14 step 2 done (2026-10-16): every IntelliJ model and both Gradle-owned models come from the per-project
task, exact on gradle/gradle (D17). Network-free, deflated (D18, D19): `tasks-only` 15.3 / 2.16 / 11.2 s vs
builders 21.9 / 1.47 / 12.5 s (warm plain / hit / edit, `results/m4h-NOTES.md`); `tasks` with IntelliJ's action
17.9 / 7.7 / 13.0 s. Next: daemon live set in the task world, then M5 (IDE side).

## Environment

This machine; details and disk budget in `design/environment.md`.

- Benchmark worktree of gradle/gradle: `~/projects/gradle-wbs-benchmark`, detached at `dedc5d14f2d` (master of 2026-10-14)
- Gradle distribution used for measurements: the worktree's own wrapper distribution `gradle-9.9.0-milestone-2`
  (DECISIONS D2); Gradle user home for all benchmark runs: `~/.gradle-wbs` (D4)
- IntelliJ for tooling jars: `~/tools/ideaIC/idea-IU-253.28294.334` (IDEA 2025.3, unified installer, Linux aarch64; D5).
  Harness classpath uses only `plugins/gradle`, `plugins/gradle-java`, `plugins/Kotlin` and `lib`.
- IntelliJ Community fork: none exists (see "Blocked on owner"); prototype in `prototype/` (D7). The installed IDE runs
  headless (`idea.sh warmup --project-dir=`, D20): IDE-side measurements happen here.
- Disk free at last check: 15 GB (2026-10-16 00:45, after deleting old model dumps and CC entries).
  `~/projects/gradle-wbs-benchmark/.gradle/configuration-cache` (~100 MB per client JVM) and `results/**/*-trace-log.txt`
  (~900 MB each; delete after aggregation).
- Build the harness and prototype only with `./build-all.sh`, never while a harness JVM or daemon is running.
- Owner instruction (2026-10-15): commit locally, do not push; ask before pushing or squashing.

## Done

- **M5 / D20–D24** (2026-10-16): headless IDE runs; plugin + patched resolver; one Gradle invocation per sync (D23);
  two IDE import classes patched against quadratic library lookups (D24: `findIdeLibrary` by name, `findLibraryOrderEntry`
  by library id), data import stage 26.5–27 s → 17–18 s, storage identical; remaining dependency-step cost is in the
  workspace model's Kotlin bridges (`setScope` re-indexing, `registerPaths`, `createLibrary`), documented for JetBrains
  in `results/m5-ide/baseline-NOTES.md`.
- **D14 step 2 / D17–D19** (2026-10-16): `IdeaModulePayload` (idea extension when applied, plugin defaults otherwise)
  and `KotlinDslScriptPayload` (build script, precompiled plugins, settings and init scripts on the root) in every
  bundle; comparison `results/m3-ports/compare-gradle-owned.md` (255/255 modules, every script but for the
  accessors jar). Stage-1 accessors and distribution-sources warm-ups before the parallel captures (deadlock and
  network stall otherwise). `src` dir in the benchmark distribution (D18). Deflated payloads (D19). Timing rounds
  `m4d`–`m4h` with notes; final numbers in `results/m4h-NOTES.md` and `results/m4-summary.md`.
- **M4**: `results/m4-summary.md`, `results/m4-NOTES.md`, `results/REPORT.md` filled. Medians (builders / tasks /
  tasks-only / launcher): warm plain 21.4 / 22.4 / 14.8 / 16.6 s; hit 1.65 / 1.75 / 1.68 / 3.3 s; edit 14.1 / 16.5 /
  10.2 / 11.4 s; settings edit 16.6 / 15.9 / 11.5 / 11.6 s; warm miss run 0: 25.1 / 20.0 / 11.5 / 13.5 s; fresh client
  JVM: 19.2 / 20.0 / 14.1 s misses, launcher 2.9 s hit.
- **M3 mechanical group**: 14 models captured via `PortedModels`, all identical to builder output
  (`results/m3-ports/compare-mechanical.md`); `GradleTaskModel` differs only by the new task.
- **M3 hard group** (D13 + D16): `GradleSourceSetDependencyModel` 287/287 identical in the ordered comparison,
  `KotlinGradleModel` 60/60, `ProjectDependencies` same nodes with sibling-order noise (`results/m3-ports/compare-hard.md`).
  Needed one builder context per build (shared build service) and per-project source-set realization at
  `afterProject` (race otherwise). Floor timings unchanged (`results/m4c-tasks-only/`).
- **M2**: `prototype/` (`IdeModelTask`, `IdeProjectModelTask`, `IdeModelTasks.apply(gradle)` from an init script,
  `IdeModelTaskRequestBuilder`), harness transports `--tasks`, `--tasks-only`, `--launcher`. Gate passed (287 tasks
  up to date on the hit, no IP problems).
- **M1**: harness (`harness/README.md`): real `GradleModelFetchAction`, the IDE's 22 resolver extensions, IntelliJ's
  init scripts, phase timings, model counts, task outcomes, CC lines, `--repeat` in one JVM, traces + `aggregate_trace.py`,
  `summarize.py`, model dumps and comparison tools.
- **M0**: `results/m0-baseline/` (cold 34.7 s; warm plain 16.9 s; warm IP hit 1.4 s; warm IP miss 21.9 s; incremental
  IP 14.9 s; IP + model cache hit 1.3 s; incremental + model cache fails, Gradle bug).

## In flight

- Nothing running. All of 2026-10-16 is in the single local commit (not pushed; the last amends are unsigned because
  the sandbox's ssh-agent stopped answering, re-sign with `git commit --amend -S --no-edit`). Final state of the day: one Gradle
  invocation per sync in the real IDE, project model identical to stock (1370/1370), restart import **31–34 s** with
  the two IDE-side fixes of D24 (41–45 s without them) vs stock 1 m 19–23 s, first import 59 s vs 1 m 19–24 s
  (`results/m5-ide/baseline-NOTES.md`, REPORT points 10–13). Stable three-way restart comparison, medians of 3:
  stock 1 m 20.4 s, tasks 37.6 s (−53 %), tasks + fixes 29.2 s (−64 %) (`results/m5-ide/stable-NOTES.md`).
  Forced-miss series (settings script changed per run): stock 1 m 32.4 s, tasks 1 m 3.6 s (−31 %), tasks + fixes
  52.9 s (−43 %) (`results/m5-ide/miss-NOTES.md`). Empty-cache miss series (no script change): stock 1 m 21.1 s, tasks
  1 m 1.1 s (−25 %), tasks + fixes 52.3 s (−35 %) (`results/m5-ide/emptycc-NOTES.md`). The benchmark's CC dir holds
  one task-world entry for the unmodified build (a hit for the next restart measurement), 56 MB.
- The patched IDE copy in the scratchpad (`idea-patched`) now also carries the D24 classes in `lib/app.jar`
  (`install-patched-ide.sh`, skip with `WBS_PATCH_IDE_IMPORT=0`); the stock installation is untouched. JFR profiles of
  the import: scratchpad `ide-import.jfr` (before) and `ide-import-d24.jfr` (after), aggregation scripts
  `jfr-import-agg.py`, `jfr-step-agg.py`, step durations `logs/ide-steps.py`.

## Next step

0. D29 (2026-10-17): `GradleBuild` as a task implemented (`-Dwbs.transport.tree=task`, root `ideBuildTree` +
   `ideModels`, base-script model included; `results/m5-ide/buildtree-262-NOTES.md`): identical storage, no time gain
   on a hit (first daemon contact ~1.8 s whichever operation); kept as an option, model request stays the default.
   D27 (2026-10-17): Mac kit for IntelliJ IDEA 2026.2.2 (262.10315.125) built and smoke-tested headless on Linux
   (restart hit 31.6 s): `dist/wbs-ide-kit-262.10315.125.tar.gz` (also unpacked in the scratchpad `kit/`). Patches
   ported to `ide-plugin/src/patched-262/java` (`-PideaBranch=262`, IDE at `~/tools/ideaIC/idea-IU-262.10315.125`);
   the 253 build is unchanged (`-PideaHome=.../idea-IU-253.28294.334`). Owner to run `install-mac.sh` on the Mac;
   unverified there: ad hoc signing, icon rendering, Kotlin DSL script support with the fetched
   `GradleDslBaseScriptModel`. Investigation of `GradleBuild`/`BuildEnvironment`/`GradleDslBaseScriptModel` as tasks:
   `results/m5-ide/gradlebuild-as-task-NOTES.md` (design: per-build `ideBuildTree` root task; saving 0.25–0.35 s warm;
   no resilience fallback needed, D28). 2026.2.2 measured (`results/m5-ide/stable-262-NOTES.md`): stock 1 m 13.5 s vs
   tasks + fixes 31.8 s (−57 %), data import 21.2 → 16.1 s; forced miss 1 m 19.2 → 1 m 3.6 (tasks) → 54.8 s (tasks +
   fixes), `results/m5-ide/miss-262-NOTES.md`. `install-patched-ide.sh` now copies each jar once (two overlays share a
   jar in 2026.2; the first measurement ran with one fix missing). Disk: 3 GB free after removing the benchmark's
   configuration cache dir; the 2026.2.2 IDE (3 GB) is at `~/tools/ideaIC/idea-IU-262.10315.125`.
   D26 (2026-10-17 night): the dependency model travels as a compact string table + records (`CompactDependencies`,
   entry format `compact-deps`, expanded by `ModelPayload.readModel`, the one reader for all consumers); state form per
   model class, `-PideModelTasks.compact=false` for the all-bytes comparison (the D25 `state=objects` option is gone).
   Entry 56 → 39 MB, bundles 51.9 → 43.1 MB, fidelity 287/287, but no measurable time gain on this machine (hit load
   0.2 s either way, IDE import within noise); `results/m5-ide/compact-NOTES.md`, DECISIONS D26. Next size levers in
   that direction: drop `ProjectDependencies` from the eager sync, the script classpath as one build-level table.
   D25 (2026-10-16 evening): whole object graphs as the task state were no faster and a 21 % larger entry
   (`results/m5-ide/phases-NOTES.md`, DECISIONS D25).
   The configuration cache entry was the 287 bundles (49 MB) plus ~1.7 MB of fingerprint and task graph state, and the 6 s store is the
   model capture itself; the sizes come from the models repeating the build script classpath and the dependency
   records per project (shared tables in the model contract would make it ~7 MB). A changed init-script classpath jar
   does not invalidate the entry: after a prototype rebuild, invalidate explicitly (the first run otherwise reuses the
   old task state). Candidate changes ranked for the owner on 2026-10-16 (LOG): version check + fallback in the init
   script, `GradleBuild`/`BuildEnvironment` from the root bundle, the Mac kit, bulk library entries in the IDE import,
   cheaper dependency capture, incremental IDE import.

1. Re-measure the Kotlin DSL script captures without the `src` directory on a stable connection (owner: from
   Saturday 2026-10-17) to put a number on the lookup for the report; keep D18 for comparisons.
2. Hit measurements: warm daemon, more than three runs, or GC/JIT-aware (`results/hit-NOTES.md`); the launcher series
   with 13–23 s loads on reused entries is an open outlier to catch with a sampler that attaches reliably
   (`pgrep` pattern failed when the daemon ran with extra JVM args).
3. D10: a patched distribution to run scenario 6 (per-model cache competitor); owner: gradle/gradle's `binDist` task
   builds it in about a minute, so patch the codec in the worktree and build with `binDist`.
4. M5 round 2c done (D23), streaming reads done (first import −3.5 s, no gain on a restart). Left for the single-invocation mode: request the Kotlin `prepare` tasks in the plain run
   where they exist, the MPP-only Kotlin models as payloads, and the `GradleBuild`/`BuildEnvironment` structure as a
   root-level bundle so the task run is the only daemon round trip; then the fork branch with the plugin and the two
   patched classes as source. `IdeaProject` and `KotlinDslScriptsModel` are done (D22); left: the eight small
   Kotlin compiler-plugin models and `GradleBuildScriptClasspathModel` as payloads (IntelliJ builders exist, mechanical
   M3 ports), the Kotlin `prepare` tasks (`PrepareKotlinIdeImportTaskModel`, `KotlinDslScriptAdditionalTask` only
   schedule tasks: request them in the plain run), `GradleBuild`/`BuildEnvironment` as plain model requests, and the
   resolver fed from the plain run's bundles (the assembly moves to the IDE side). Then a restart is the 11 s plain
   run plus the IDE's own import. Also for Gradle: key build actions by classpath content, not per-process id.
   (file transport in the IDE, no re-streaming through the action; `IdeaProject` from the `ideaModule` payloads;
   the Kotlin plugin from `kotlinDslScript`; module-name de-duplication in the consumer).
5. IDE-side investigation stopped by the owner on 2026-10-16 evening ("lets stop with that"). Known so far: the stock
   sync's resolve stage is pure Gradle time (IDE threads idle); the data import is single-threaded and loses ~5 s to
   concurrent warm-up indexing in the headless runs (warm-index re-import: data import 10–11 s, LOG 2026-10-16); the
   remaining dependency-step cost is `setScope`/`updateLinksIndex`, `registerPaths`, `createLibrary` (bulk
   `addLibraryEntries` by scope is the candidate change). JFR recordings in the scratchpad: `ide-import*.jfr`
   (patched, before/after D24), `ide-import-stock.jfr`; scripts `jfr-import-agg.py`, `jfr-step-agg.py`,
   `jfr-timeline.py`. Unfinished: the delegated read-only code investigation of these paths (no result recorded).
6. IDE-side import, after D24: the remaining 9 s of the library dependency step are in the workspace model's Kotlin
   bridges (`LibraryOrderEntryBridge.setScope` → `updateLinksIndex`, `registerPaths`, `createLibrary`); a Kotlin
   compiler (kotlin-compiler-embeddable 2.4.x is in `~/.gradle-wbs/caches`) would allow patching them the same way.
   Otherwise the lever is the incremental import (`design/m5-ide-consumer.md`). Report the three items to JetBrains.

## Blocked on owner

- **Not blocking, for the owner to file**: Gradle bug found by scenario 6 (LOG 2026-10-15, "Scenario 6 failure
  explained"): with `-Dorg.gradle.internal.isolated-projects.caching=tooling`, a re-sync after a cache load fails
  on any private or final attribute compatibility rule class (KGP's `KotlinUsagesCompatibility`), because
  `ImmutableAttributesSchemaCodec` stopped recognizing rules when `db79ac69208` wrapped them in `ValidatingAction`.
  Reproducer: `harness/run-scenarios.sh <label> 6` on gradle/gradle. Plan here: D10.
- Display: no longer needed (D20, headless warm-up).
- **Where do the fork branches live?** README deliverable 2 asks for branches `work-based-sync` on the IntelliJ
  Community fork (and the Gradle fork if needed). `asodja/intellij-community` does not exist on GitHub and
  `asodja/gradle` has no such branch (2026-10-15). Assumption while waiting: the prototype is developed in
  `prototype/` of this repo (DECISIONS D7) and moved to the fork for M5 once the owner names it.
- Answered 2026-10-15: the experiment continues with the thesis refocused on misses and incremental re-sync (D6 (b));
  Gradle-owned models become tasks too (D14).

## Gates applied

| Gate | Criterion | Outcome | Date |
|---|---|---|---|
| after M0 | per-project IntelliJ models sum to > 3 s on warm sync | Met on a warm **miss**: ~48 s summed fetch time, ~14 s wall of IntelliJ per-project models in `results/m0-baseline/3-models.md` (traced warm miss, 21 s total). Not applicable on a warm **hit**: 0 s, whole action result from the CC entry, 1–2 s total. See D6. | 2026-10-15 |
| after M2 | task up to date on all 254 projects in the warm IP run, no IP problems | Passed: 287 `ideProjectModel` up to date, CC entry reused, no IP problems (LOG 2026-10-15 "M2 on gradle/gradle") | 2026-10-15 |
| after M4 | task sync faster than the builder sync by ≥ 30 % (D15) on scenario 2, the warm miss and scenario 4 | `tasks`: not met (+5 %, +4 %, +17 %). `tasks-only`: borderline (−30 %, −26 % traced / −54 % run 0, −28 %). `launcher`: −22 %, cross-JVM hit (2.9 s vs 19–25 s), −19 %. Continue with D14 step 2, which targets exactly the serial builder that keeps `tasks` from winning (`results/m4-summary.md`). | 2026-10-15 |
