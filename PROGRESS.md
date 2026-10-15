# Progress

Keep this file current. A resuming agent continues from here alone.

## Current milestone

M0 and M1 done (2026-10-15). Next: M2 (task contract + `GradleSourceSetModel` end to end), under the D6
assumption while the owner question below is open.

## Environment

This machine; details and disk budget in `design/environment.md`. Fill in as things are created:

- Benchmark worktree of gradle/gradle: `~/projects/gradle-wbs-benchmark`, detached at `dedc5d14f2d` (master of 2026-10-14)
- Gradle distribution used for measurements: the worktree's own wrapper distribution `gradle-9.9.0-milestone-2`
  (DECISIONS D2); Gradle user home for all benchmark runs: `~/.gradle-wbs` (D4)
- IntelliJ for tooling jars: `~/tools/ideaIC/idea-IU-253.28294.334` (IDEA 2025.3, unified installer, Linux aarch64; D5).
  Harness classpath uses only `plugins/gradle`, `plugins/gradle-java`, `plugins/Kotlin` and `lib`, so Ultimate-only
  resolvers are not loaded.
- IntelliJ Community fork: none exists (see "Blocked on owner"); prototype in `prototype/` (D7)
- Disk free at last check: 28 GB (2026-10-15, after cleanup)

## Done

(nothing yet)

## Done

- **M3 mechanical group**: 13 models captured via `PortedModels`, all identical to builder output
  (`results/m3-ports/compare-mechanical.md`). Key finding on the way: resolution cannot be deferred under CC
  (LOG, D13), so the dependency model is captured at configuration time too.
- **M0**: `results/m0-baseline/` (models.md = cost tables, summary.md, NOTES.md, per-run files, `<s>-models.md`
  per-model tables from traces). Medians: cold 34.7 s; warm plain 16.9 s; warm IP hit 1.4 s; warm IP miss 21.9 s;
  incremental IP 14.9 s; IP + model cache hit 1.3 s; incremental + model cache: fails (Gradle). Gate applied below.
- M1 harness works end to end on `harness/fixture` (see `harness/README.md`): real `GradleModelFetchAction`, providers
  from the IDE's 22 `projectResolve` extensions, IntelliJ's init scripts, phase timings, model counts, task outcomes,
  CC lines; `--repeat` runs in one JVM (needed for CC entry reuse, LOG 2026-10-15); `--before-run` hook; trace
  aggregation (`aggregate_trace.py`) and `summarize.py` verified on fixture output.
- Benchmark worktree warmed up (`./gradlew help`, 25 min, build-logic compiled, `~/.gradle-wbs` 2.5 GB).

## In flight

- **M3 hard group** (D13: GradleDependencyDownloadPolicy + artifact-index request + GradleSourceSetDependencyModel,
  KotlinGradleModel, ProjectDependencies captured at configuration) running on gradle/gradle: miss/hit/incremental in
  task mode plus the comparison (session scratchpad `logs/gg-m3h.log`, `logs/gg-m3h-compare.txt`, dumps
  `dump/gg-builders2`, `dump/gg-tasks4`). Expected differences: dependency-model parts that need other projects'
  artifact-index entries. Then: `results/m3-ports/compare-hard.md`, M4 runs for both versions with
  `WBS_EXTRA="--tasks --extra-tooling-jar prototype/build/libs/ide-model-tasks.jar"`, scenario 7 (build-logic edit).
- **M2 end to end**: `prototype/` (task base class `IdeModelTask`, `IdeProjectModelTask`, registration `IdeModelTasks.apply(gradle)`
  from an extra init script, `IdeModelTaskRequestBuilder` scheduling the tasks in the projects-loaded phase) and the
  harness task transport (`--tasks --extra-tooling-jar prototype/build/libs/ide-model-tasks.jar`, `IdeTasksTransport`).
  Works on the fixture with identical `GradleSourceSetModel` content (`prototype/compare-models.sh`). Running on
  gradle/gradle now: builder dump, task-mode miss/hit/incremental, comparison (session scratchpad `logs/gg-m2.log`,
  dumps `dump/gg-builders`, `dump/gg-tasks`). The M2 gate (task up to date in all projects on the warm IP hit, entry
  reused, no IP problems) is read from `logs/gg-m2-tasks-1.txt`.

- First (cold) harness sync of gradle/gradle done: 26 min with downloads, LOG 2026-10-15; per-model table in
  `results/m0-baseline/first-cold-sync-models.md`.
- Unexplained, low priority: scenario 3 run 0 (warm miss) took 66 s vs 21–23 s for the other warm misses
  (3-trace, 6-0, 6-trace); it overlapped with a 911 MB trace aggregation.

## Next step

1. Read the investigator's answer on scenario 6; record it in LOG.md; if a workaround exists, rerun
   `harness/run-scenarios.sh m0-baseline 6` with it (script must not be edited while running).
2. M2: decide `design/task-contract.md` (grouping; artifact index -> per-project entries + consumer-side lookup, see
   the reading notes in `DECISIONS.md` once written), clone the IntelliJ fork shallow at the 253 branch (D5) into
   `~/projects/intellij-community` (16 GB free; budget 5–8 GB), add the task base class and port
   `GradleSourceSetModel`, transport in the harness first (`harness/` gets a `--tasks` mode that adds the task
   names in the projects-loaded phase and reads the files in the build-finished phase).

## Blocked on owner

- **Not blocking, for the owner to file**: Gradle bug found by scenario 6 (LOG 2026-10-15, "Scenario 6 failure
  explained"): with `-Dorg.gradle.internal.isolated-projects.caching=tooling`, a re-sync after a cache load fails
  on any private or final attribute compatibility rule class (KGP's `KotlinUsagesCompatibility`), because
  `ImmutableAttributesSchemaCodec` stopped recognizing rules when `db79ac69208` wrapped them in `ValidatingAction`.
  Reproducer: `harness/run-scenarios.sh <label> 6` on gradle/gradle. Plan here: D10.

- **Where do the fork branches live?** README deliverable 2 asks for branches `work-based-sync` on the IntelliJ
  Community fork (and the Gradle fork if needed). `asodja/intellij-community` does not exist on GitHub and
  `asodja/gradle` has no such branch (2026-10-15). Assumption while waiting: the prototype is developed in
  `prototype/` of this repo (DECISIONS D7) and moved to the fork for M5 once the owner names it.

- **Not blocking, for the owner to file**: Gradle bug found by scenario 6 (LOG 2026-10-15, "Scenario 6 failure
  explained"): with `-Dorg.gradle.internal.isolated-projects.caching=tooling`, a re-sync after a cache load fails
  on any private or final attribute compatibility rule class (KGP's `KotlinUsagesCompatibility`), because
  `ImmutableAttributesSchemaCodec` stopped recognizing rules when `db79ac69208` wrapped them in `ValidatingAction`.
  Reproducer: `harness/run-scenarios.sh <label> 6` on gradle/gradle. Plan here: D10.

- **Where do the fork branches live?** README deliverable 2 asks for branches `work-based-sync` on the IntelliJ
  Community fork (and the Gradle fork if needed). `asodja/intellij-community` does not exist on GitHub and
  `asodja/gradle` has no such branch (2026-10-15). Assumption while waiting: the prototype is developed in
  `prototype/` of this repo (DECISIONS D7) and moved to the fork for M5 once the owner names it.

- **Does the experiment continue given the whole-action cache?** Finding: with Gradle 9.9 and gradle/gradle's
  defaults (CC + IP on), a no-change re-sync in the same client JVM is served whole from the configuration cache
  entry: 1.1–2.3 s for 287 projects and 113 MB of models, no builder runs (`results/m0-baseline/3-*.txt`). A
  warm miss (new client JVM, e.g. IDE restart, or any invalidation) costs ~21 s, of which ~14 s is model building.
  The plan assumed builders re-run on every warm sync; they do not. Options: (a) stop and report that Gradle
  already solves the no-change case; (b) continue with the thesis refocused on misses and incremental re-sync,
  measured against the opt-in per-model cache (DECISIONS D6); (c) continue the original plan unchanged.
  **Assumption while waiting: (b).** Work that does not depend on the answer continues: finishing M0 (scenarios
  1, 2, 4, 5 and the new 6), M1 is done, and the M2 contract work is unaffected.

## Gates applied

| Gate | Criterion | Outcome | Date |
|---|---|---|---|
| after M0 | per-project IntelliJ models sum to > 3 s on warm sync | Met on a warm **miss**: ~48 s summed fetch time, ~14 s wall of IntelliJ per-project models in `results/m0-baseline/3-models.md` (traced warm miss, 21 s total). Not applicable on a warm **hit**: 0 s, whole action result from the CC entry, 1–2 s total. See "Blocked on owner" and D6. | 2026-10-15 |
| after M2 | task up to date on all 254 projects in the warm IP run, no IP problems | Passed: 287 `ideProjectModel` up to date, CC entry reused, no IP problems (LOG 2026-10-15 "M2 on gradle/gradle") | 2026-10-15 |
| after M4 | warm IP task sync faster than the warm IP builder sync by the margin fixed in LOG.md | | |
