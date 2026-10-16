# GradleBuild, BuildEnvironment, GradleDslBaseScriptModel as tasks (investigation, 2026-10-17)

Read-only investigation (investigator subagent) of what the remaining Gradle-owned model requests of the
single-invocation mode are, what the IDE needs from them, and whether a task or a client-side substitute could
replace them. Sources: Gradle 9.9.0-milestone-2 checkout, IDE 253.28294.334 jars (javap, byte scans), IntelliJ
sources of branch 253 and master fetched to the scratchpad (`inv-gradlebuild/`).

## What the IDE consumes, where, when

- **BuildEnvironment never reaches the daemon.** `ProviderConnection.run()` answers it in the client's provider
  layer (`platforms/core-runtime/launcher/.../ProviderConnection.java:160-174`, "we don't really need to launch the
  daemon"; `initParams` 338-368 builds `DefaultBuildEnvironment` from the build layout, `gradle.properties` daemon
  params and `GradleVersion.current()`). The IDE fetches it on every `GradleExecutionHelper.execute`
  (`GradleExecutionHelper.java:226-248, 529-562`) and keeps it in the context (`GradleExecutionContextImpl.kt:26-37`,
  the Gradle version derives from it). Consumers: version/JVM check, logging setup and build root, java home for the
  operation, `GradleProjectResolver.java:174-175,222,289`, `DefaultProjectResolverContext.java:189-194`, the Kotlin
  script model processor and sync contributor (java home, version). Milliseconds, nothing to move. Correction to LOG
  2026-10-16: the cold-daemon cost attributed to "GradleBuild and BuildEnvironment" is GradleBuild alone.
- **GradleBuild.** Stock: first call inside the action (`GradleModelFetchAction.java:164-212`, `getBuildModel()` plus
  `getEditableBuilds()`). Ours: a plain request before the run (`WbsTaskTransportResolverExtension.java:102-139`).
  Everything the IDE keeps is copied once into `DefaultGradleLightBuild` (`.java:30-49,123-136`) and
  `DefaultGradleLightProject` (`.java:36-46`): root project name, `buildIdentifier.rootDir`, per project name, path,
  `buildTreePath` (Gradle >= 8.2), directory, `projectIdentifier(rootDir, path)`, children; `getIncludedBuilds()` for
  the parent-build hierarchy; buildSrc parents by directory name. After that only `GradleLightBuild`/`GradleLightProject`
  and `GradleModelId` strings (class simple name + name hash, root dir path hash, project path; `GradleModelId.java:65-90`)
  are used. Gradle side: `GradleBuildBuilder.java:91-98` (`ensureProjectsLoaded`, settings only), 111-122 adds every
  importable build (buildSrc included, `AbstractBuildState.java:121`).
- **GradleDslBaseScriptModel: nothing in 253.28294.334 requests it** (byte scan: zero hits). The Kotlin plugin
  derives script definitions client-side from `<gradleHome>/lib` (`GradleScriptDefinitionsUtilsKt.loadGradleDefinitions`:
  `gradle-(kotlin-dsl|core|base-services)*.jar`, `kotlin-stdlib-*`, `kotlin-compiler-embeddable*`; templates by
  version). Master and 2026.2 fetch it first inside the action and stream it (`GradleDslBaseScriptModelHolder`);
  2026.2's `KotlinDslBaseScriptSyncContributor` reads it with `resolverContext.getRootModel(GradleDslBaseScriptModel)`.
  Gradle side it is a pure function of the distribution (`GradleDslBaseScriptModelBuilder.java:42-62`) but still a
  `BuildScopeModelBuilder`: a daemon and a build tree.

## Could a task, the client or the run itself produce the data

- **Root task of the init script, per build (a).** Init scripts run in every build of the tree, buildSrc and included
  builds too (`DefaultSettingsPreparer.java:159`). Without configuring projects, IP-safe:
  `gradle.settingsEvaluated { settings.rootProject }` gives the `ProjectDescriptor` tree (name, path, projectDir,
  children); included builds are attached after that callback (`DefaultSettingsPreparer.java:159-175`), so read
  `gradle.getIncludedBuilds()` (name, projectDir) in `projectsLoaded` or the root project's `beforeProject`. Build
  identity path from the `gradle.getParent()` chain plus `IncludedBuild.getName()`; `buildTreePath` = identity path +
  project path (as `GradleBuildBuilder.java:210-214`). Plain strings in `@Input` properties: CC- and IP-safe.
  `GradleDslBaseScriptModel`: a task can call the builder's services via `gradle.getServices()` and write the result
  into the root bundle (matters for 2026.2+). Addressing the tree in one run: a bare task name runs only in the root
  build; fan out with `IncludedBuild.task(":ideBuildTree")` (`IncludedBuild.java:45`) from each build's root task,
  nesting covered by each build's own init-script instance. buildSrc is not an included build: selectable by path
  from the root (`DefaultBuildTaskSelector.java:148-160`) but not via `dependsOn`; hook onto buildSrc's own work
  (`BuildSrcBuildListenerFactory.java:79-86`) gated on a sync property, or fall back to the model request. IP legality
  of string-path `dependsOn` across projects: not verified.
- **Client-side substitute (b).** `BuildEnvironment` already is. `GradleDslBaseScriptModel`: 253 already scans `lib/`;
  for 2026.2 a per-version cache of a task-written root bundle is safer than reproducing `ImplicitImports` client-side.
  `GradleBuild`: not derivable without evaluating settings.
- **From the run itself (c).** Task events carry the identity path only (`TaskOperationMapper.java:95-102`), no
  project directory or build root dir, so events alone cannot locate bundles; they do give the project paths of every
  scheduled task (up-to-date ones included). (c) complements (a): a root bundle per build at a fixed path under the
  root dir the IDE knows, project bundles streamed by identity path.

## Objects the resolver needs

The resolver never holds Tooling API proxies after `convertGradleBuilds` (`GradleDaemonModelHolder.java:70-73,112-115`;
ours `WbsTaskTransportResolverExtension.java:237-238`). Needed: `GradleBuild` (`getRootProject`, `getBuildIdentifier`,
`getProjects`, `getIncludedBuilds`; `getEditableBuilds` only for our traversal), `BasicGradleProject` (`getName`,
`getPath`, `getBuildTreePath`, `getProjectDirectory`, `getProjectIdentifier`, `getChildren`), identifiers.
`DefaultBuildIdentifier`/`DefaultProjectIdentifier` are in the IDE's `gradle-api` jar and are what the light build
constructs anyway; that jar also ships the provider POJOs `DefaultGradleBuild`/`BasicGradleProject`, which
`ProtocolToModelAdapter.adapt` turns into the same proxy the Tooling API returns. A plain implementation (as
`IdeaProjectFromPayloads`) is enough. Identity: the light build's maps rely on `equals`; proxies compare by source
object identity (`ProtocolToModelAdapter.java:540-549`), so reuse the same instances for root/children/included builds.
`GradleModelId` keys are strings. `buildTreePath` must be set (gated by `isBuildTreePathAvailable`). Nothing else in
`gradle.jar` references `org.gradle.tooling.model.gradle.GradleBuild`.

## Expected saving

Warm daemon: the whole 0.25–0.35 s request (dispatch, `BuildModelAction`, entry lookup and fingerprint check for the
model entry, serialization). Cold daemon: the 2–3 s is daemon start plus JIT-cold settings evaluation, both of which
move to the task run; only the duplicate entry check / duplicate "Load build" on a miss goes, 0.3–1 s. `BuildEnvironment`:
nothing. `GradleDslBaseScriptModel`: nothing in 253; in 2026.2 the single-invocation mode has to supply it (the plugin
now requests it on the same connection, D27).

## Recommended design and risks

Keep `BuildEnvironment`. Add an init-script-registered `ideBuildTree` task in the root project of every build (inputs:
descriptor tree, included builds with names, identity path, Gradle version and user home; output
`<buildRootDir>/build/ide/models/buildTree.bin`), fanned out through `IncludedBuild.task(":ideBuildTree")`; the IDE
requests the fixed list `ideBuildTree ideProjectModel`, reads the tree bundle first via its finish event, then streams
the rest; `GradleBuild`/`BasicGradleProject` synthesized client-side from it. Risks: (1) no resilience: the builder
returns partial builds on broken settings (`GradleBuildBuilder.java:154-181`) while a task run fails whole. Owner,
2026-10-17: resilience is not a goal of this prototype; resilient task runs can be added to Gradle later, so no
fallback to the model request is needed; (2) the current addressing of included builds by directory name
(`WbsTaskTransportResolverExtension.java:128`) is wrong for `includeBuild(dir) { name = ... }` or duplicate dir names;
the tree bundle's names fix this; (3) buildSrc needs its own trigger or the fallback; (4) 2026.2+ needs
`GradleDslBaseScriptModel` in the root bundle or a request; (5) IP legality of cross-project string `dependsOn`.
