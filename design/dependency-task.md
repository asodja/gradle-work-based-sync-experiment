# `ideDependencyModel`: the hard-group task (draft, 2026-10-15)

**Superseded the same day by D13**: the execution-time design below is not implementable because the configuration
cache resolves `ArtifactCollection`s at store time (`ArtifactCollectionCodec`, LOG 2026-10-15). The dependency
model is captured at configuration time with the builder code; what is kept from this draft is the consumer-side
handling of the cross-project artifact index, needed for a complete IDE-side port (M5).

Target models: `GradleSourceSetDependencyModel` (two thirds of all per-project builder time on gradle/gradle,
`results/m0-baseline/models.md`), later `ProjectDependencies` and `KotlinGradleModel`. Unlike the cheap models (D8),
the cost here *is* the work (dependency resolution), so it has to move to execution time and be skipped when the
declared inputs are unchanged. That is the only place where tasks can beat IP's per-model cache: a build-logic
edit or a comment edit reconfigures projects and invalidates their cached models, but leaves these inputs alone.

## What the builder does today (`GradleSourceSetDependencyModelBuilder`, `GradleSourceSetDependencyResolver`, `GradleDependencyResolver`)

Per source set: resolve the compile classpath (the `JavaCompile` task's classpath, falling back to the source set's)
and the runtime classpath, merge (compile-only → PROVIDED), then `providedCompile`/`providedRuntime` for war
projects. Resolution: `configuration.getIncoming().artifactView { lenient = true }.getArtifacts()` for the files,
`getResolvedConfiguration().getLenientConfiguration()` for module dependencies and their artifacts, the
`ResolutionResult` for project dependencies whose artifacts failed to transform, an auxiliary resolver for sources
and javadoc (gated by `GradleDependencyDownloadPolicy`), and the cross-project `GradleSourceSetArtifactIndex`
(jar path → source set, output dir → jar path) to turn project artifacts into source-set dependencies and to add
runtime output dirs.

## Task design

Inputs captured at configuration (all `@Input` strings, no files):
- per source set: name, the names of its compile and runtime classpath configurations, and the classpath
  *declaration* fingerprint: for each configuration, the dependency notations (`group:name:version` or
  `project:path`, with attributes, exclusions and capability requests rendered to strings), the resolution
  strategy's force/substitution rules rendered to strings, and the repository URLs of the project. Changing any
  of these changes the input; a comment edit or a build-logic recompilation that leaves them alone does not.
- the download policy (sources/javadoc booleans), `idea.resolveSourceSetDependencies`.
- this project's own artifact index entries (jar path → source set name and output dirs), captured from the
  `Jar` tasks' `archiveFile.getLocationOnly()` and the source set outputs.

Resolution at execution:
- `Configuration` objects cannot be stored in the task (not CC-serializable). Capture instead
  `configuration.getIncoming().artifactView { lenient = true }.getArtifacts().getResolvedArtifacts()`
  (`Provider<Set<ResolvedArtifactResult>>`) and `getIncoming().getResolutionResult().getRootComponent()`
  (`Provider<ResolvedComponentResult>`) as `@Internal` properties: both are documented as CC-compatible and
  resolve lazily at execution. Unresolved dependencies come from the `ResolutionResult`
  (`UnresolvedDependencyResult`), replacing the `LenientConfiguration` path, which is not CC-serializable.
  Sources/javadoc: an `ArtifactResolutionQuery` at execution for the resolved module components (as the
  auxiliary resolver does), or an artifact view with the `sources`/`javadoc` doc type attribute.
- The dependency on producing tasks: an artifact view's file set, when wired as a *task input*, adds a dependency
  on the producer (`jar`). As an `@Internal` provider read in the action it schedules nothing; the `File` of a
  project artifact is just a path. The base class check (`IdeModelTask.checkDependencies`) verifies that no
  such dependency slipped in.
- Cross-project: the task does not look at other projects. It records project dependencies by component identity
  (build path, project path, configuration/variant name) plus the artifact file paths Gradle reports. The
  consumer (harness, later the IDE) owns every project's artifact index entries from the bundles and performs
  `findByArtifact` / `findArtifactBySourceSetOutputDir` / `findArtifactSources` when it assembles
  `DefaultGradleSourceSetDependencyModel`. So the model class produced by the task is a *pre-index* form
  (`IdeRawDependencyModel`, new, Ion-serialized) and the consumer produces the IntelliJ model from it. This is the
  one place where the task version adds consumer-side logic; it is confined to the transport class.

## Expected incrementality

- Comment edit in `:core`: IP reconfigures `:core`; the dependency task's inputs are unchanged → up to date; the
  per-model cache re-runs `:core`'s dependency builder (~0.5 s).
- Build-logic edit (every project reconfigured): tasks up to date everywhere (configuration cost only, ~7 s);
  the per-model cache rebuilds every model (~21 s). This is the scenario to add (scenario 7: edit a precompiled
  script plugin in `build-logic`).
- Version bump of one dependency in `:core`: `:core`'s task re-resolves; dependents whose declarations did not
  change stay up to date, although their resolved graph may differ (transitive version change). This is a
  correctness gap of declaration-based inputs: fix by including the resolved graph identity of project
  dependencies as an input via a second, cheap task output (`ideDependencyGraphHash`) that dependents read as an
  `@InputFile`... which is a model-task-on-model-task dependency across projects (allowed by the contract) but
  couples tasks across projects under IP. Decide after measuring whether the simple version already pays.

## Comparison test

Builder dump vs consumer-assembled model from the bundle, canonical Ion, same script as the cheap models.
Expected acceptable differences: ordering of dependencies with equal classpath order; sources/javadoc paths if the
download policy differs.
