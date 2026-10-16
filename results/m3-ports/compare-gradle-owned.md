# D17 comparison: Gradle-owned build models vs per-project payloads on gradle/gradle (2026-10-16)

Builders: `IdeaProject` (one per build, `IdeaModelBuilder`) and `KotlinDslScriptsModel` (one per build,
`KotlinDslScriptsModelBuilder`), as the IDE requests them. Payloads: `IdeaModulePayload` and `KotlinDslScriptPayload`
in every project's `ideProjectModel` bundle (`prototype/.../GradleOwnedModels.java`). Both sides are written as text
by the harness (`--dump-models`, `GradleOwnedText`): one file per module (keyed by project path) and one per script
(keyed by script path), compared with `prototype/compare-gradle-owned.sh`. Dumps of the same day
(`m4-builders4` 00:58, task dumps `m4-tasks7` 04:58 and `m4-tasks8`), gradle/gradle at `dedc5d14f2d`, four builds.

## What the IDE reads from these models

- `IdeaModule` (`CommonGradleProjectResolverExtension.populateModuleContentRoots`, IDEA 253): with
  module-per-source-set (the default; the harness runs with `idea.resolveSourceSetDependencies=true`) the content
  roots and source folders of a JVM project come from `ExternalProject`'s source sets. From the `IdeaModule` content
  root the IDE takes only the **root directory** and the **exclude directories**; its source, test and resource
  directories are used only when the project has no source sets. The module's name, jdk name and compiler output
  are read elsewhere (`GradleProjectResolver`), the language levels via the `IdeaProject`.
- `KotlinDslScriptsModel`: every entry is consumed as is by the Kotlin plugin (`KotlinDslScriptModelResolver`),
  keyed by script file.

## Result, first task dump (`m4-tasks7`, payload mirrors the idea plugin's defaults; build script only)

| Kind | identical | different | task-only | builders-only | note |
|---|---|---|---|---|---|
| ideaModule | 13 | 242 | 36 | 0 | root build; the included builds' documents are unreadable for IntelliJ's own `IdeaProject` Ion reader (LOG 2026-10-16), so their 36 modules have no builder text |
| kotlinDslScript | 0 | 287 | 0 | 93 | all 287 differences are the hash of the type-safe accessors jar (`kotlin-dsl/accessors/<hash>/classes.jar`, `sources.jar`): the task world has one more accessor (`TaskContainer.ideProjectModel`), nothing else differs; builders-only = 4 settings scripts + 89 precompiled script plugins of `build-logic*` |

The 242 module differences are all `test` and `testResource` lines (1139 + 534 lines, one `exclude`, one
`module` name):

- gradle/gradle's build logic adds its integration, cross-version, smoke and fixture source sets to the module's test
  sources (`build-logic/integration-testing/.../shared-configuration.kt`, `plugins.withType<IdeaPlugin> { idea.module.testSources.from(...) }`)
  and the root module's `intTestHomeDir` exclude (`gradlebuild.ide.gradle.kts`). The first runs only when the idea
  plugin is applied, which in the builder world `IdeaModelBuilder` does for every project. The payload mirrored the
  plugin's defaults (`main.allJava`, resources, JvmTestSuite sources), so the customization was not seen.
- `module gradle-kotlin-dsl` vs `kotlin-dsl` for `:kotlin-dsl`: the idea plugin de-duplicates module names across the
  composite (`build-logic` has a `:kotlin-dsl` too). The payload carries the project name; the consumer has to
  de-duplicate (IntelliJ does its own module naming from the Gradle path when qualified names are on).

For the IDE's content roots in the default mode only the exclude line matters (one module). The missing precompiled
script plugins are a real gap: the Kotlin plugin needs their models to edit `build-logic/**/*.gradle.kts`.

## Changes for the second dump (`m4-tasks8`)

1. When the project applied the idea plugin itself, the payload reads `idea.module` (name, content root, source,
   test, resource, test-resource and exclude dirs, generated flags, jdk name, compiler output). The language levels
   stay the raw compatibility values (their conventions compare all projects of the build). Without the plugin the
   defaults are mirrored as before.
2. The script payload carries every script the builder attributes to the project: build script, precompiled script
   plugins (`PrecompiledScriptPluginsSupport.collectScriptPluginFilesOf`), and on the root project the settings
   script and the init scripts.
3. The stage-1 accessors classpath is computed once before captures run in parallel (deadlock otherwise, LOG).

## Result, second task dump (`m4-tasks8`)

| Kind | identical | different | task-only | builders-only | note |
|---|---|---|---|---|---|
| ideaModule | **255** | 0 | 36 | 0 | every module of the root build identical, customizations included (`intTestHomeDir` exclude, 1139 test and 534 test-resource dirs, the de-duplicated `gradle-kotlin-dsl` name read from the plugin); the 36 task-only modules are the included builds, whose builder documents IntelliJ's Ion reader cannot read |
| kotlinDslScript | **93** | 287 | 0 | 0 | 4 settings scripts and 89 precompiled script plugins identical; the 287 build scripts differ only by the accessors jar hash (the new `ideProjectModel` accessor), no other line differs (`grep` over the diff: 0 non-accessor lines) |

Dump run: 34.6 s in a fresh daemon, 314 tasks executed (first run of the new jar), the 9 known `apiStubElements`
`GradleExtensions` failures and nothing else. Raw: `scratchpad logs/compare-gradle-owned8.txt` of this session; the
`_error.txt` files of the three unreadable builds are in the builders dump.

## What remains different, and for whom

- The accessors jar: by construction, the task world has one more task type. Not a fidelity issue.
- Included builds' idea modules: unverifiable here until IntelliJ's Ion reader issue is understood (LOG 2026-10-16).
- Builds that never apply the idea plugin get the plugin's defaults and lose `plugins.withType(IdeaPlugin)`
  customizations; the IDE does not read the affected fields for JVM projects in the default mode (see above).
