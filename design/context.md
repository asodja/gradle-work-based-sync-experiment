# Context: everything known before this repo existed

Gathered 2026-10-14 from IntelliJ Community master and gradle/gradle master (268c64352a8). Verify a path
before relying on it; both repos move.

## 1. How IntelliJ fetches models during sync

- One `BuildAction`, `GradleModelFetchAction`
  (`plugins/gradle/tooling-extension-impl/src/com/intellij/gradle/toolingExtension/impl/modelAction/GradleModelFetchAction.java`),
  run through `BuildActionExecuter` with a projects-loaded and a build-finished part. IntelliJ injects its
  model builders into the daemon via an init script; each builder implements
  `org.jetbrains.plugins.gradle.tooling.ModelBuilderService` and is listed in a `META-INF/services` file.
- Resolver extensions on the IDE side (`GradleProjectResolverExtension`, EP `projectResolve`) declare which
  models to request through `ProjectImportModelProvider`s and in which phase. The core one is
  `plugins/gradle/src/org/jetbrains/plugins/gradle/service/project/CommonGradleProjectResolverExtension.java`
  (`getModelProviders`, `getExtraProjectModelClasses`, `getExtraBuildModelClasses`).
- Phases (`plugins/gradle/tooling-extension-api/src/com/intellij/gradle/toolingExtension/modelAction/GradleModelFetchPhase.kt`),
  in order: `BASE_SCRIPT_MODEL_PHASE` (before configuration), `PROJECT_LOADED_PHASE`,
  `TURN_OFF_DEFAULT_TASKS_PHASE`, then after the build finished `PROJECT_MODEL_PHASE`,
  `SOURCE_SET_MODEL_PHASE`, `DEPENDENCY_MODEL_PHASE`, `SCRIPT_MODEL_PHASE`, `ADDITIONAL_MODEL_PHASE`.
- Providers that matter for the port, all in `tooling-extension-impl/.../impl/model/`:
  `GradleSourceSetModelProvider` (also fetches `GradleTaskWarmUpRequest` first),
  `GradleSourceSetDependencyModelProvider` (fetches `GradleDependencyDownloadPolicy` and
  `GradleSourceSetArtifactBuildRequest` first, then the model), `GradleExternalProjectModelProvider`,
  `GradleTaskModelProvider`, `GradleBuildScriptClasspathModelProvider`, `GradleTurnOffDefaultTasksModelProvider`.
  Generic classes: `GradleClassProjectModelProvider` / `GradleClassBuildModelProvider` (default phase
  `ADDITIONAL_MODEL_PHASE`).
- **Precedent for tasks during sync**: `PrepareKotlinIdeaImportTaskModelBuilder.kt` and
  `KotlinDslScriptTaskModelBuilder.kt` (`plugins/kotlin/gradle/gradle-tooling/impl/...`) run in
  `PROJECT_LOADED_PHASE` and add task names to `gradle.startParameter.taskNames`. `GradleTurnOffDefaultTasksModelBuilder`
  sets default tasks to `:help` and excludes it so a sync without requested tasks runs nothing.
  Gradle's own `RunEclipseTasksBuilder` does the same for Buildship.
- IntelliJ already serializes every model to bytes (`SerializationService`s in tooling-extension-impl,
  `GradleDaemonModelHolder`), and the `Default*` model classes are `Serializable` because they cross the
  TAPI wire. "Write the model to a file" can reuse that.
- IntelliJ's sync has two timed phases on the IDE side: `GRADLE_CALL` (the whole BuildAction) and
  `PROJECT_RESOLVERS` (resolvers turning models into IDE data). See `GradleProjectResolver.java`
  (`ExternalSystemSyncActionsCollector.logPhaseStarted/Finished`). The port changes `GRADLE_CALL`.
- Resilient model fetch: registry key `gradle.use.resilient.model.fetch`, on for Gradle ≥ 9.7
  (`GradleProjectResolver.isResilientModelFetchEnabled`). Out of scope here but it determines which Gradle
  code path serves the request (see §3).

## 2. What a real sync requests

Request log from one IntelliJ Ultimate sync of a 5-project Kotlin DSL build (Gradle 9.9 nightly), 36
distinct models, in first-request order. Scope: default = root build without target, build = per build,
project = per project (×5).

```
default  org.gradle.tooling.model.dsl.GradleDslBaseScriptModel                                   2937 ms
default  org.gradle.tooling.model.gradle.GradleBuild                                             10864 ms
default  com.intellij.gradle.toolingExtension.impl.model.util.daemonClassLoaderModel.GradleDaemonClassLoaderModel
build    org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask
project  org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel
default  com.intellij.gradle.toolingExtension.impl.model.util.turnOffDefaultTasksModel.GradleTurnOffDefaultTasksRequest
project  org.jetbrains.plugins.gradle.model.ExternalProject
project  com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest            ~50 ms each
project  org.jetbrains.plugins.gradle.model.GradleSourceSetModel                                   3141 ms once, else ~1 ms
project  com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy
project  com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest
project  org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel                        5–46 ms each
build    org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel                               7646 ms
project  org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel
project  org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel
project  org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel
build    org.gradle.declarative.dsl.tooling.models.DeclarativeSchemaModel                          499 ms
project  com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies
project  org.jetbrains.plugins.gradle.model.AnnotationProcessingModel
project  org.jetbrains.plugins.gradle.model.RepositoryModels
project  org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel
project  com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel
project  org.jetbrains.plugins.gradle.model.ear.EarConfiguration            (Ultimate)
project  org.jetbrains.plugins.gradle.model.web.WebConfiguration            (Ultimate)
project  com.intellij.ktor.run.gradle.tooling.KtorApplicationGradleModel    (Ultimate)
project  com.intellij.micronaut.gradle.tooling.MnApplicationGradleModel     (Ultimate)
project  org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel
project  com.intellij.compose.ide.plugin.gradleTooling.rt.ComposeResourcesModel
project  org.jetbrains.plugins.gradle.model.GradleExtensions
project  org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel
project  org.jetbrains.plugins.gradle.model.IntelliJProjectSettings
project  org.jetbrains.plugins.gradle.model.IntelliJSettings
project  org.jetbrains.plugins.gradle.model.DependencyAccessorsModel
project  org.jetbrains.plugins.gradle.model.VersionCatalogsModel
build    org.gradle.tooling.model.idea.IdeaProject                                                68 + 24 ms
project  org.jetbrains.plugins.gradle.model.GradleTaskModel
```

Everything not annotated was 0–3 ms per project on this small build. `BuildEnvironment` is fetched
separately before the action (`GradleExecutionHelper.kt`). Lesson: on a small build the cost is settings
evaluation, the Kotlin DSL script model and first-time dependency resolution, none of which a task port
changes. The port can only win on warm re-syncs (CC) and on large builds. Hence gradle/gradle and the
scenarios in `design/measurement.md`.

## 3. Gradle internals that matter

- Every model request passes through `DefaultBuildTreeModelCreator.getModel`
  (`subprojects/core/src/main/java/org/gradle/internal/buildtree/DefaultBuildTreeModelCreator.java`, the
  "Fetch model" build operation). That is where the request logger lives (see §5). Do **not** log in
  `AbstractToolingScope.getModel` of `DefaultBuildToolingModelController`: the resilient scopes
  (`ResilientBuildToolingModelController`) override `getModel` and bypass it.
- Build-scoped builders (`BuildScopeModelBuilder`, internal) run without configuring projects:
  `GradleBuildBuilder` (settings only, `ensureProjectsLoaded`), `GradleDslBaseScriptModelBuilder`
  (pure function of the distribution), `BuildEnvironmentBuilder`. Public API for such builders is
  requested in gradle/gradle#36506 (open, backlog). These stay models in this experiment.
- The phased action does not force projects loaded by itself; `GradleBuild` does (`ensureProjectsLoaded`).
  `DefaultBuildTreeLifecycleController.fromBuildModel`: beforeTasks → run tasks (if requested) → fromBuildModel.
  So tasks added to the start parameter in the projects-loaded phase run before the build-finished phase.
- Running tasks on demand from inside configuration exists: `BuildTreeWorkGraphController.withNewWorkGraph`
  (used by `DefaultBuildLogicBuildQueue` for buildSrc). An alternative transport if start-parameter
  injection turns out inconvenient.
- The build operation trace writes every build operation, including "Fetch model '<name>' for <target>",
  to a file: daemon JVM arg `-Dorg.gradle.internal.operations.trace=<path-prefix>`. Zero-code alternative to
  the request logger.
- CC caches per-model results only under Isolated Projects (`BuildModelParameters.isCachingModelBuilding`).
  Without IP, CC does not apply to a model-building invocation at all. A task-based sync is a task run and
  CC applies. This is the whole thesis.

## 4. Pitfalls already hit

- **Assertions are on in integration tests.** The Gradle integ test harness runs daemons with `-ea`. Code
  that passes by hand can trip an `assert` there (e.g. `CommandLineTaskConfigurer.configureTasks` asserts a
  non-empty task set). When a test fails "with no output", rerun the exact command from the test's XML
  with `GRADLE_OPTS` as the harness sets it.
- **Implicit task dependencies.** Wiring `jar.getArchiveFile()` or `sourceSet.getOutput()` into a task
  input creates a dependency on the producing task and the "sync" starts compiling. Use
  `getLocationOnly()` / `getElements()`-free paths and `@Input` strings. Enforce in the base class.
- **Included builds configure before root projects.** `BuildTreePreparingProjectsPreparer.prepareProjects`
  registers dependency substitutions, which configures every included build, before the root's own
  projects. Any cost or failure there is attributed to the root.
- **A throw from a plan node aborts the plan.** `DefaultFinalizedExecutionPlan.handleFailure` treats an
  exception from `Node.execute` as an execution failure and aborts even under `--continue`. Only a node
  failure (`getNodeFailure`) is tolerated. Relevant if the transport ever uses `WorkNodeAction`s.
- **Shared builder state.** `GradleSourceSetModelBuilder` and `GradleSourceSetDependencyModelBuilder`
  share `GradleSourceSetArtifactIndex` and `GradleDependencyDownloadPolicy` through `ModelBuilderContext`.
  Tasks have no shared context; decide file vs recompute (M2).
- **Shell hygiene in agent sessions.** A grep with an empty file argument blocks on stdin forever; guard
  `$(git grep -l …)` results. `pkill -f <pattern>` kills your own shell when the pattern is in your command
  line. Never run two Gradle builds in one checkout concurrently; it corrupts outputs.
- **Daemon and wrapper caching.** The wrapper caches a distribution by URL hash, the daemon by version.
  After rebuilding a distribution with the same version, delete `~/.gradle/wrapper/dists/gradle-<v>-bin/`
  and run `gradle --stop`, or patch the single changed jar into the unpacked distribution instead.

## 5. Logging model requests (the instrument used for §2)

In `DefaultBuildTreeModelCreator.getModel`'s `call`, around `scope.getModel(modelRequestContext, parameter)`:
record `System.currentTimeMillis()`, `target.describeTargetScope()`, `modelRequestContext.getModelName()`,
whether parameterized, and the elapsed nanos; append a tab-separated line to `~/model-requests.log`
(synchronized; `user.home` is the daemon's). Aggregate with
`awk -F'\t' '{n[$3]++; t[$3]+=$5} END {for (m in n) print n[m], t[m], m}' | sort -k2 -n -r`
after stripping the `ms` suffix. Delete the file between syncs.

## 6. Related prototype (out of scope, useful reference)

`origin/asodja/prototype-resilient-task-execution` in gradle/gradle (2 commits, 2026-10-14) implements
per-build resilient task scheduling behind `-Dorg.gradle.internal.resilient-task-scheduling=true`:
a build that fails to configure is dropped from the task run, the others run, the failure is reported.
Includes `BrokenBuildTaskReference`/`BrokenBuildTaskNode` for cross-build task dependencies and the
`BuildInclusionCoordinator` tolerance. Not needed for the performance experiment; do not merge it in.

## 7. Decisions already made

- Benchmark build is gradle/gradle. Headline metric is warm re-sync with CC on.
- Gradle-owned models stay models: `GradleBuild`, `BuildEnvironment`, `GradleDslBaseScriptModel`,
  `KotlinDslScriptsModel`, `IdeaProject`, `DeclarativeSchemaModel`. They are either cheap (`IdeaProject`
  ~90 ms) or impossible as tasks (run before projects exist).
- IntelliJ resolvers and data services are not changed. Producers and transport only.
- Transport for the Gradle-side measurement: start-parameter task injection in the projects-loaded phase,
  file read in the build-finished phase. Streaming via task-finish events is M5.
- File format: IntelliJ's existing Ion-based `SerializationService`s, not Java serialization.
- Tasks are Isolated-Projects- and configuration-cache-compatible from the first port; gradle/gradle runs
  with both on, and the baseline on that build is the IP model cache, not uncached builders.
- Kotlin Multiplatform, Ultimate models, resilient sync, public API: out of scope.
- Measurements are done with the harness (M1) first; the IDE only confirms at the end (M5).
