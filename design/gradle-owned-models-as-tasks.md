# Gradle-owned models as tasks (D14 follow-up, draft 2026-10-15; implemented 2026-10-16 as D17)

Once every IntelliJ model comes from a task, two Gradle-owned build-level models still force full configuration
and travel over the wire: `IdeaProject` (9.5 MB on gradle/gradle) and `KotlinDslScriptsModel` (4.2 MB, 4 builds).

- `IdeaProject` (`IdeaModelBuilder`): per build, modules with content roots, compiler settings, dependencies, the
  `GradleProject` (tasks) per module. The IDE uses it as the module backbone (`IdeaModule` iteration in
  `GradleProjectResolver`). Per-project substitute: everything it carries per module already exists in
  `ExternalProject`, `GradleSourceSetModel`, `GradleSourceSetDependencyModel` and `GradleTaskModel`; a small
  `ideaModule` payload (module name, content roots, inherited output dirs, jdk name, language level) from the
  `idea` plugin's model, captured per project, completes it. The IDE then assembles the module list from the
  per-project bundles instead of iterating `IdeaProject` (resolver change, M5).
- `KotlinDslScriptsModel` (`KotlinDslScriptsModelBuilder`): per build, the classpath, implicit imports and source
  path of every `.gradle.kts` script. Per-project substitute: a `kotlinDslScript` payload per project capturing its
  build script's classpath (`buildscript.configurations.classpath` resolved at store time, plus the Kotlin DSL
  implicit imports and accessors directories from `KotlinScriptClassPathProvider` / the accessors generator, which
  are Gradle internals available in the daemon). Settings and init scripts stay with the builder for the root build.
- `GradleBuild` and `BuildEnvironment` stay as they are: settings-only, sub-second. Investigated on 2026-10-17
  (`results/m5-ide/gradlebuild-as-task-NOTES.md`): `BuildEnvironment` is answered in the client without a daemon;
  `GradleBuild` could become a per-build `ideBuildTree` root task (settings descriptor tree + included builds, fanned
  out with `IncludedBuild.task()`, light builds synthesized client-side) for the 0.25–0.35 s warm request, with the
  model request kept as a fallback for broken settings and buildSrc; `GradleDslBaseScriptModel` is needed by 2026.2+
  only and is requested on the same connection in the single-invocation mode (D27). Implemented on 2026-10-17 as D29
  (`-Dwbs.transport.tree=task`, `results/m5-ide/buildtree-262-NOTES.md`): one Gradle operation per sync, identical
  IDE storage, no measurable time change on a hit (the first daemon contact costs ~1.8 s whichever operation makes it).
Measured floor without either model: `--tasks-only` (LOG 2026-10-15), which is what these substitutes would reach.

Implemented (D17): `IdeaModulePayload` and `KotlinDslScriptPayload` in the `ideProjectModel` bundle
(`prototype/.../GradleOwnedModels.java`), harness text comparison (`GradleOwnedText`, `prototype/compare-gradle-owned.sh`).
Deviation from the sketch: the idea plugin is not applied (not possible from project code under Isolated Projects),
the payload mirrors its defaults from the java plugin; the build-level values are derived by the consumer.
