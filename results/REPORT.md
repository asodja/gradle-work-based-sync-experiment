# Work-based sync: report (draft, 2026-10-15; numbers from the M4 matrix are filled in when `results/m4-summary.md` exists)

Question: does IntelliJ's Gradle sync get faster when its per-project tooling models are produced by Gradle
tasks that write files instead of model builders running in the daemon's model-building phase?

Benchmark: gradle/gradle at `dedc5d14f2d` (254 build-script projects, 287 projects over 4 builds, Kotlin DSL),
Gradle 9.9.0-milestone-2 (the wrapper's version), IntelliJ IDEA 2025.3 tooling jars, this machine
(`design/environment.md`). No IDE runs here; the harness (`harness/`) runs IntelliJ's real `GradleModelFetchAction`
with the IDE's own 22 resolver extensions and measures the Gradle side. IDE-side phases are not measured (D1).

## Short answer

1. **No-change re-sync is already free.** Under Isolated Projects, Gradle caches the complete result of IntelliJ's
   build action in the configuration cache entry. A no-change re-sync of gradle/gradle (5755 models, 113 MB) takes
   1.4 s with today's builders and no builder runs. Tasks cannot beat that (task version: 1.5–1.9 s).
2. **Where builders cost time**: a cache miss (every IDE restart, since the entry key includes the client
   classloader's UUID; any invalidated input) costs ~22 s, and an edit of one build script ~15 s, because every
   model is rebuilt. Two thirds of the model time is the source set dependency model.
3. **Tasks help exactly there**: with all 17 IntelliJ per-project models produced by one task per project
   (`ideProjectModel`) and no builder requested at all, a miss costs ~14–15 s and an edit ~13 s on gradle/gradle
   (`LOG.md`, "All-tasks floor", "Launcher transport"). Final medians: see the M4 table below.
4. **Why not more**: the configuration cache resolves dependencies when the entry is stored, so a task cannot
   defer that work to execution; the models are captured at configuration time and a miss still computes them all.
   And on this Gradle version a build-script edit invalidates the whole entry in every transport, so the edit case
   is a full reconfiguration for everyone. The task version wins by parallel capture at store time and by not
   re-sending 113–132 MB of models.
5. **Fidelity**: 14 of the 17 ported models are identical to the builder output for every project
   (`results/m3-ports/compare-mechanical.md`); `GradleTaskModel` differs only by the new task; the two dependency
   models need the ordered comparison (IntelliJ's Ion documents reference each other) and are still open.

## Scenarios and medians (M4)

_To be filled from `results/m4-summary.md`: builders / tasks (ported models from bundles, Gradle-owned models from
builders) / tasks-only (no builder) / launcher (plain task run + client-side reading), scenarios 1 cold, 2 warm plain,
3 warm hit, 4 edit, 7 full invalidation._

## What was measured and how

- Harness: `harness/README.md`. Runs inside one client JVM (`--repeat`) because of the classloader UUID in the cache
  key; reports phase marks, task outcomes, cache lines and whole-machine stall gaps.
- Per-model costs: build operation traces of a warm plain sync and a warm miss (`results/m0-baseline/models.md`).
- Comparison: byte dumps of every model in both modes, canonical Ion text (`prototype/compare-models.sh`).

## Findings and blockers (chronological detail in `LOG.md`)

- Whole-action result caching under IP (`DefaultConfigurationCache.loadOrCreateModel`), independent of the opt-in
  per-model cache; key includes the Tooling API client classloader UUID.
- Gradle regression: `isolated-projects.caching=tooling` fails after a cache load on private/final attribute rule
  classes (`ImmutableAttributesSchemaCodec` no longer matches `ValidatingAction`); unreported upstream.
- Dependency resolution cannot be deferred past the configuration cache store (`ArtifactCollectionCodec`).
- A project build script is a build-scoped input (`onScriptFileResolved`), so IP's per-project entry update is not
  reached from the IDE on 9.9.0-milestone-2.
- Intermittent whole-machine stalls of ~100 s on this sandbox (detected via idle-daemon health-check gaps).
- IntelliJ's Ion serializers are not thread-safe and keep object tables across documents; readers must be fresh
  per document on the consumer side, or read all documents of a class in order (as the IDE does).
- `PrepareKotlinIdeaImportTaskModelBuilder` updates the start parameter without synchronization while IntelliJ
  requests it in parallel (potential lost prepare tasks on large Kotlin builds; not verified in the IDE).
- `GradleBuildScriptClasspathModel` needs the parent project's model from the shared builder context; not ported.
- Nine gradle/gradle projects remove `apiStubElements`; `ProjectExtensionsDataBuilderImpl` fails on them in both modes.

## Not measured

- IDE-side phases (`GRADLE_CALL`, `PROJECT_RESOLVERS`): no display on this machine (D1).
- Scenario 6 (incremental with the per-model cache): blocked by the Gradle regression (D10).
- The IDE-side assembly of `IdeaProject` from per-project models (D14, M5).
