# Task contract (draft; M2 turns it into decisions)

Every ported model follows the same contract so that ports are mechanical and the transport is one piece
of code.

## Naming and registration

- One task per model per project, named `ide<ModelSimpleName>` (e.g. `ideSourceSetModel`,
  `ideExternalProject`). No group, so they do not show in `gradle tasks`.
- Registered from IntelliJ's init script with `gradle.lifecycle.beforeProject` (Isolated Projects safe),
  lazily (`tasks.register`), and only where the model applies (`plugins.withId("java-base")` for
  source-set models). Projects without the plugin get no task and the consumer treats a missing file as
  "no model", exactly as a `null` from the builder today.
- Requested by adding the task names to `startParameter.taskNames` in `PROJECT_LOADED_PHASE`, like
  `PrepareKotlinIdeaImportTaskModelBuilder` does. One provider does this for all ported models.

## Base class

`abstract class IdeModelTask extends DefaultTask` in `tooling-extension-impl`:

- `@OutputFile RegularFileProperty getModelFile()` defaulting to
  `<project build dir>/ide/models/<ModelSimpleName>.bin`.
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

## Output and serialization

- Java serialization of the existing `Default*` model class (`DefaultGradleSourceSetModel`,
  `DefaultExternalProject`, …). They are `Serializable` already. The consumer deserializes with the
  tooling extension classloader. If a model is not `Serializable`, IntelliJ's `SerializationService` for
  that model is the fallback.
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
