# Task contract (decided in M2, 2026-10-15: DECISIONS D7, D8, D9; items below not covered by a decision are still as drafted)

Every ported model follows the same contract so that ports are mechanical and the transport is one piece
of code.

## Builders to tasks is not one to one

The builder set is shaped by IntelliJ's phases and by model classes the IDE consumes; the task set should
be shaped by what makes sense as work. Merge, split or drop freely, as long as the consumer still receives
the same model objects:

- **Merge**: one task per project producing several models is often better than one task per model. It
  snapshots the project once, cuts the task count (254 × 1 instead of 254 × 15 on gradle/gradle), and
  writes one bundle file. A natural first grouping is one task per IntelliJ phase group: a "project
  model" task (`ExternalProject`, source sets, tests, extensions, settings, catalogs, accessors, manifest,
  annotation processing, repositories, task model) and a "dependency model" task (source set dependencies,
  artifact index, project dependencies), since the second resolves configurations and is the expensive,
  separately-invalidated part. Decide the grouping in M2 against: input overlap, failure isolation (one
  failing part should not lose the whole bundle), incrementality (what changes together), and file count.
- **Split**: a builder that does two unrelated things (e.g. `GradleSourceSetModelBuilder` collects jar
  artifacts for all tasks and builds source sets) may become two tasks if their inputs change independently.
- **Drop**: builders that exist only because of the builder world disappear. `GradleTaskWarmUpRequest`
  (realizes tasks so later builders see them), `GradleTurnOffDefaultTasksRequest` (moot once tasks are
  requested), `GradleDependencyDownloadPolicy` as a separate request (becomes an input of the dependency
  task), `GradleSourceSetArtifactBuildRequest` (becomes an output or an input).
- **Keep the model classes**: the consumer, the resolvers and the serializers are keyed by model class, so
  tasks still produce `DefaultExternalProject`, `DefaultGradleSourceSetModel` and friends. A bundle file is
  just several serialized models in one container with a small index.

Record the mapping in `design/model-inventory.md` (add a "task" column) as it is decided.

## Naming and registration

- Task names reflect the chosen grouping, e.g. `ideProjectModel`, `ideDependencyModel`, or
  `ide<ModelSimpleName>` for single-model tasks. No group, so they do not show in `gradle tasks`.
- Registered from IntelliJ's init script with `gradle.lifecycle.beforeProject` (Isolated Projects safe),
  lazily (`tasks.register`), and only where the model applies (`plugins.withId("java-base")` for
  source-set models). Projects without the plugin get no task and the consumer treats a missing file as
  "no model", exactly as a `null` from the builder today.
- Requested by adding the task names to `startParameter.taskNames` in `PROJECT_LOADED_PHASE`, like
  `PrepareKotlinIdeaImportTaskModelBuilder` does. One provider does this for all ported models.

## Base class

`abstract class IdeModelTask extends DefaultTask` in `tooling-extension-impl`:

- `@OutputFile RegularFileProperty getModelFile()` defaulting to
  `<project build dir>/ide/models/<taskName>.bin`; a bundle holds several serialized models with an index
  (model class name → byte range), single-model tasks hold one.
- `@Internal` project identity captured at configuration time (identity path, project dir) for the
  consumer to key the model.
- A validation (at graph time via `gradle.taskGraph.whenReady`, or a `doFirst` in development builds)
  over `getTaskDependencies().getDependencies(this)`: every dependency must be another `IdeModelTask` or
  one of the IDE's prepare tasks (`prepareKotlinIdeaImport`, `prepareKotlinBuildScriptModel`); anything
  else fails with the offending task and the input that introduced it.

  Why an allow-list and not a ban: a model task depending on another model task is how shared builder
  state (the source set artifact index) becomes a file, and the prepare tasks are what IntelliJ already
  schedules during sync today. Why a ban on everything else: compiling user code during sync is slow,
  can fail for IDE-unrelated reasons and has side effects; a dependency on `jar` would make the task
  out of date on every source edit and destroy the warm-sync win; and timings would then compare
  compilation, not sync. Build logic (`buildSrc`, included plugin builds) is compiled by configuration
  regardless and is not a violation. Generated source directories are registered as roots without being
  generated. Project dependencies are mapped to modules by component identity, never by building jars;
  resolve metadata and lenient artifact views, and never declare a configuration as `@InputFiles`.
- `@DisableCachingByDefault` initially. Enable the build cache per task later only if inputs are stable
  across machines (they contain absolute paths today).

## Inputs

- Paths, never files. `srcDirs`, `classesDirs`, `archiveFile`, toolchain homes go in as `@Input`
  `String`/`List<String>`/`Map<String,…>`. `@InputFiles` would fingerprint contents and make the task out of
  date on every source edit.
- Wire with providers, do not resolve at configuration time: `jar.getArchiveFile().getLocationOnly().map(…)`,
  `javaCompile.getJavaCompiler().map(c -> c.getMetadata().getInstallationPath().getAsFile().getPath())`.
  Resolving toolchains moves to execution this way.
- Values that need reflection into other tasks' internals (e.g. `AbstractCopyTask.getMainSpec` in
  `GradleSourceSetModelBuilder.containsAllSourceSetOutput`) are computed at configuration time into plain
  values. They cannot run inside the task under CC because they capture the `Jar` task.
- One `@Nested` bean per source set (`SourceSetInput`: name, java src dirs, resource dirs, includes,
  excludes, classes dirs, resources dir, compiler args, toolchain home, isTest) rather than flat maps.
- No `Project` access in the task action. Services via `@Inject`. This keeps the task CC-compatible.

## Isolated Projects and configuration cache compatibility

gradle/gradle runs with `org.gradle.isolated-projects=true` and `org.gradle.configuration-cache=true`
(its `gradle.properties`), so the tasks must be IP- and CC-compatible from the first port, not as a
follow-up:

- Registration from the init script with `gradle.lifecycle.beforeProject` (an `IsolatedAction`), never
  `allprojects {}` / `subprojects {}`.
- A task reads only its own project's state at configuration time. No `project.getRootProject()`,
  `getParent()`, `getAllprojects()`, `project(":x")`, no `evaluationDependsOn`. Cross-project facts the
  builders read today (e.g. the artifact index across projects) come from another model task's output or
  from the build-finished consumer that sees all files.
- No `Project`, `Gradle` or `TaskContainer` captured in task fields or providers. Values are plain or
  providers derived from the project's own objects; services via `@Inject`. The action must work when
  the task is deserialized from the CC entry.
- Verify on gradle/gradle: a sync with `--configuration-cache` and IP on produces no IP violations in
  the CC report and reuses the entry on the second run. A port is not done until this holds.
- The consumer side (the IDE provider) only reads files; it has no IP constraints.

## Output and serialization

- Use IntelliJ's existing `SerializationService<T>` for the model (Amazon Ion binary, `byte[] write(T,
  Class)` / `T read(byte[], Class)`, registered in `META-INF/services/…SerializationService`). Thirteen
  models already have one, including `ExternalProject`, `GradleSourceSetModel`,
  `GradleSourceSetDependencyModel`, `GradleTaskModel`, `GradleBuildScriptClasspathModel`, repositories,
  extensions, tests, annotation processing and project dependencies. The task writes `write(model)` to the
  output file; the consumer calls `read`. The same code path IntelliJ uses for its streamed values, so the
  format is maintained by JetBrains and independent of the tooling classloader.
- Models without a serializer (small ones: settings, manifest, catalogs, accessors, download policy, kapt)
  get a small Ion serializer written the same way, or JSON if that is quicker; record the choice. Ion's
  text writer is the debugging aid: a `-Pide.models.text=true` switch that writes `.ion` text next to the
  binary is cheap and worth having from M2.
- Java serialization is used only for models without an Ion serializer (DECISIONS D12); it ties those entries to
  the tooling classloader and is not inspectable, so a real implementation should add Ion serializers for them.
- Delete the output on failure (a failed task leaves no stale model). The consumer checks the file's
  mtime is after the build start.
- Shared builder state (`GradleSourceSetArtifactIndex`) becomes either a file the dependent model task
  reads as an `@InputFile` (a model-task-on-model-task dependency, allowed) or a recomputation in each
  task. Decide in M2 and record.

## Warnings and partial models

Builders tolerate failing elements (`GradleCollectionVisitor.onFailure`) and report through
`MessageReporter`. Tasks catch per element in the action, write the warning into the model (a `warnings`
list in the serialized payload if the model has none; otherwise a sidecar `.warnings` file) and report via
the injected `Problems` service. A task succeeds with a partial model when anything was producible.

## Transport (harness in M2, IDE in M5)

- Projects-loaded phase: provider adds all `ide*` task names of ported models to the start parameter.
  Sync runs with `--continue` so one failing model task does not stop the others.
- Build-finished phase: provider walks the projects of each `GradleBuild`, reads
  `<build dir>/ide/models/*.bin`, deserializes, and hands the objects to the same consumer entry points the
  builders fed (`GradleModelConsumer` / `ProjectResolverContext`). Nothing downstream changes.
- Later (M5): subscribe to task-finish events and read each file when its task completes.

## Comparison test

For each port: import the fixture with builders, import with tasks, serialize both models with the same
serializer, diff. Differences are recorded in LOG.md with a verdict (bug in port / acceptable). The test
lives next to IntelliJ's existing builder tests in `tooling-extension-impl/testSources`.
