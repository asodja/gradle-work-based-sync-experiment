# Plan

Milestones in order. Each ends in a number or a go/no-go the agent applies itself and records in
`PROGRESS.md`. Estimates are agent wall-clock hours; most of each is waiting on builds and syncs,
so run those in the background and do other work meanwhile.

## M0: baseline on gradle/gradle (4–6 h)

Goal: know where sync time goes before changing anything.

1. Build the Gradle distribution with the model-request logging in `DefaultBuildTreeModelCreator.getModel`
   (see `design/context.md`, "Logging model requests"), or reuse the one listed in `PROGRESS.md`.
2. Point gradle/gradle's wrapper at it (`design/building.md`) and sync it from an installed IntelliJ.
   Capture `~/model-requests.log` and IntelliJ's phase timings (`GRADLE_CALL`, `PROJECT_RESOLVERS`,
   from the sync log / `ExternalSystemSyncActionsCollector`).
3. Aggregate the request log by model name: count, total ms, max ms. Save as
   `results/m0-baseline/models.md` plus the raw log.
4. Run the four scenarios of `design/measurement.md` three times each with the current model-builder
   sync. Save per-run numbers in `results/m0-baseline/`.

Exit: per-model and per-phase cost tables.
Gate: per-project IntelliJ-owned models (everything except `GradleBuild`, `GradleDslBaseScriptModel`,
`KotlinDslScriptsModel`, `IdeaProject`, `BuildEnvironment`, `DeclarativeSchemaModel`) sum to more than
about 3 s on a warm sync. If not, write it up in `results/REPORT.md` and stop: the port cannot pay; the
remaining cost is settings evaluation and script models, which need a different idea.

## M1: measurement harness without IntelliJ (8–12 h)

Goal: measure the Gradle side of sync from the command line, repeatably, so no milestone below needs
the IDE to produce numbers.

1. `harness/`: a small Tooling API client (Gradle build, Java) that runs IntelliJ's real
   `GradleModelFetchAction` with the same `ProjectImportModelProvider`s IntelliJ registers, loaded from
   the installed IDE's `plugins/gradle/lib/*.jar` and `plugins/Kotlin/lib/*.jar`. Use `BuildActionExecuter`
   with `projectsLoaded`/`buildFinished` like IntelliJ does. See `design/context.md`, "How IntelliJ fetches".
2. It prints total wall time, per-phase time, and the per-model aggregation from the request log.
3. A runner script for the four scenarios: daemon control (`--stop`), CC flag, the one-line edit.
4. Verify against M0: the harness's total must match IntelliJ's `GRADLE_CALL` within noise (say 10 %).

Exit: one command yields the comparison table for any Gradle distribution and any tooling jars.

## M2: task contract and the first model end to end (12–16 h)

Goal: settle the design on the hardest representative before fanning out.

1. Turn `design/task-contract.md` from draft into decided. Record decisions in `DECISIONS.md`.
2. In the IntelliJ fork (`plugins/gradle/tooling-extension-impl`), add the model task base class and
   port `GradleSourceSetModel` (`GradleSourceSetModelBuilder.java`, ~880 lines). It shares state with
   `GradleSourceSetDependencyModel` through `GradleSourceSetArtifactIndex`; decide whether that index goes
   into a file or is recomputed per task, and write the decision down.
3. Transport in the harness first, not in the IDE: a provider that adds the task to the start parameter
   in the projects-loaded phase (the Kotlin "prepare import" precedent) and reads the file in the
   build-finished phase, handing a `DefaultGradleSourceSetModel` to the same consumer code.
4. Comparison test: import gradle/gradle (or a 20-project fixture during development), serialize both
   the builder model and the file model, diff field by field.

Exit: gradle/gradle syncs through the harness with `GradleSourceSetModel` from a task, identical content,
with the project's own settings (CC + IP on) and no IP problems reported.
Gate: in the warm IP scenario the task is up to date in all 254 projects and the CC entry is reused.
If not, find out why (an input that fingerprints file contents, a provider capturing `Project`, a
cross-project access, a task dependency that slipped in) before porting anything else.

## M3: port the per-project models (40–60 h, parallelizable)

Goal: every IntelliJ per-project model gradle/gradle exercises comes from a task.

Mechanical group (one pattern, port by copying): `GradleExtensions`, `ExternalTestsModel`,
`IntelliJSettings`, `IntelliJProjectSettings`, `RepositoryModels`, `ProjectRepositoriesModel`,
`JavaGradleManifestModel`, `AnnotationProcessingModel`, `VersionCatalogsModel`,
`DependencyAccessorsModel`, `GradleBuildScriptClasspathModel`, `GradleTaskModel`,
`GradleDependencyDownloadPolicy`, `GradleTaskWarmUpRequest`, `KaptGradleModel`.

Hard group: `ExternalProject`, `GradleSourceSetDependencyModel` (+ `GradleSourceSetArtifactBuildRequest`),
`KotlinGradleModel`, `ProjectDependencies`.

Out of scope: `KotlinMPPGradleModel` (gradle/gradle has no MPP), Ultimate-only models, Gradle-owned models.

Each port: task, comparison test, LOG entry with the per-project cost before and after on the fixture.
If several agents work in parallel, one owns `design/task-contract.md` and the base class; the others
do not change them without a DECISIONS entry.

Exit: the harness syncs gradle/gradle with no IntelliJ builder invoked except the out-of-scope ones,
and all comparison tests pass.

## M4: Gradle-side comparison (6–8 h)

Goal: the headline numbers.

1. Four scenarios, three runs each, baseline vs task version, same distribution, same machine, same day.
2. Break the task version down: configuration, task execution, file reading, remaining builders.
3. Incremental scenario in detail: which tasks re-ran after the one-line edit, and the total.
4. Write `results/REPORT.md`: tables, what was measured, what was not, blockers found so far.

Exit: the report. Gate: warm CC-on task sync must beat today's warm sync by a clear margin (decide the
margin before measuring and write it in LOG.md; 30 % is a reasonable bar). If it does not, stop here and
say so in the report; the IDE wiring would not change the outcome.

## M5: IntelliJ wiring and end-to-end measurement (20–40 h, least certain)

Goal: the same comparison with the real IDE, resolvers included, and the list of IDE-side blockers.

1. Move the harness provider into the IntelliJ fork (`GradleProjectResolverExtension` /
   `ProjectImportModelProvider`). Resolvers and data services untouched.
2. Then the streaming version: subscribe to task-finish progress events and read each file as it lands.
3. Build only the Gradle and Kotlin-Gradle plugin modules with Bazel and drop the jars into an installed
   IDE, or run the Gradle import test harness from sources. A full Community build only if both fail,
   and then note it as a blocker.
4. Measure `GRADLE_CALL` and `PROJECT_RESOLVERS` on gradle/gradle, same scenarios, both versions.

Exit: end-to-end numbers for both versions and the blocker list, appended to `results/REPORT.md`.

## Parallelism

M0 → M1 → M2 are sequential and one agent. M3 fans out. M5's environment work (Bazel plugin build,
import test harness) can start while M3 runs. M4 needs M3 complete.
