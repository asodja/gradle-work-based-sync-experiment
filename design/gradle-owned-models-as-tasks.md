# Gradle-owned models as tasks (D14 follow-up, draft 2026-10-15)

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
- `GradleBuild` and `BuildEnvironment` stay as they are: settings-only, sub-second.
Measured floor without either model: `--tasks-only` (LOG 2026-10-15), which is what these substitutes would reach.
