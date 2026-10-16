# M5 design: the IDE-side consumer of the per-project bundles (written 2026-10-16, after D17–D19)

What M5 has to build in the IntelliJ fork, derived from the harness results. Numbers refer to gradle/gradle
(`results/m4h-NOTES.md`, `results/hit-NOTES.md`, `results/m3-ports/compare-gradle-owned.md`).

## 1. Transport: the IDE reads the bundles, nothing is streamed back through the action

Today every model the action streams with `BuildController.send` is serialized by the Tooling API and recorded as a
configuration cache side effect, replayed on every hit: 113.8 MB of the builders' 114 MB entry. The harness task
variants re-streamed the models they had read from the bundles, which made their entry 186 MB and their hit slower
than the builders'. The launcher transport keeps nothing but the task state (52 MB) and its hit is the entry load
plus the fingerprint check.

So the IDE should (a) request the `ideProjectModel` tasks (plain run or `runTasks` inside the action) and (b) read
`build/ide/models/ideProjectModel.bin` of every project itself, one reader per core (0.23 s for 287 deflated bundles
in the harness client; 2–3 s single-threaded). Reading as tasks finish (progress events) overlaps the read with the
run. The bundle format is `ModelBundle` (magic, entries of class name, format, fingerprint, inflated length,
deflated bytes); payloads inflate with `ModelPayload.getBytes()`; Ion models read with a fresh `ToolingSerializer`
per entry, Java models with `ObjectInputStream`. The models arrive in IntelliJ's own classes, so the data services
and resolvers stay as they are once the two Gradle-owned ones are covered (below).

## 2. `IdeaProject` from the `ideaModule` payloads

`IdeaModulePayload` per project carries what the resolver reads from `IdeaModule` and its `GradleProject`: names,
directories, build script, content root with source, test, resource and exclude dirs (generated flags), compiler
output, jdk name, raw source and target compatibility; the root project adds the idea project name and the current
JDK. The IDE builds the module list and the build-level values from them the way `IdeaModelBuilder` and the idea
plugin do: project language level and bytecode version = maximum over the modules, a module carries its own only
where it differs. Module names come as project names (the idea plugin's composite de-duplication, `gradle-kotlin-dsl`
for `:kotlin-dsl`, is read from the plugin only where a project applied it); IntelliJ's own module naming applies.
What the resolver uses from the `IdeaModule` content root in the default module-per-source-set mode is only the root
directory and the exclude dirs (`populateModuleContentRoots`); source and test dirs matter for projects without
source sets. Included builds' modules compare only against themselves until IntelliJ's `IdeaProject` Ion reader
issue is understood (LOG 2026-10-16).

## 3. The Kotlin plugin from the `kotlinDslScript` payloads

`KotlinDslScriptPayload.scripts` per project: the build script, the project's precompiled script plugins, and on the
root project the settings script and the init scripts, each with class path, source path, implicit imports, editor
reports and exceptions, as `KotlinDslScriptsModel` delivers them. The Kotlin plugin's `KotlinDslScriptModelResolver`
consumes them keyed by script file; the only difference to the builder's model is the accessors jar hash (one more
accessor in the task world). The `src` directory of the distribution decides whether the source path contains the
Gradle sources without a network lookup (D18); with a `-bin` distribution and no `src` the lookup happens once per
sync in both worlds.

## 4. What to measure in the IDE

The two scenarios where the harness predicts the gain: a cache miss (IDE restart, invalidated input: 21.9 s builders
vs 15.3 s tasks-only in the harness) and the edit of one build script (12.5 vs 11.2 s); the no-change hit should
stay within a second of the builders' (entry load). And the IDE-side time between the last model and the end of the
import, which the harness does not measure: this is where parallel, lazy reading of bundles can gain more than the
Gradle side did.

## 5. Open before or during M5

- IntelliJ's `IdeaProject` Ion reader fails on the included builds' documents (LOG 2026-10-16).
- Module-name de-duplication across the composite moves to the consumer.
- The daemon's hit load (2–3 s for the task state) is the part of the hit the task world adds; trimming the task
  state further (the per-project payloads repeat ~300 paths each, 809 distinct) is a Gradle-side or format question.

## Learned while building it (2026-10-16, D21, D22)

- Injection without a fork: a resolver extension (plugin) contributes the init script, the daemon classpath and the
  providers; one patched IDE class (`GradleModelFetchAction`) drops built-in providers by name. Fidelity: project
  files identical to stock on the fixture (11/11) and the external storage identical on gradle/gradle (1370 files).
- The model-tasks init script needs the IDE's full tooling-extension classpath plus every resolver extension's
  tooling classes; the path-mapper init script must precede it.
- `IdeaProject` and `KotlinDslScriptsModel` are assembled in the file provider from the payloads (D22); the Kotlin
  model is a project model of each build's root project, not a build model.
- A plain task run before the action makes the bundles a cache hit after an IDE restart (10.5 s for 287 projects in
  a fresh daemon). The action itself misses on every restart (its key carries a per-process classloader id), so the
  restart gain needs the action gone: the eight small Kotlin models, `GradleBuildScriptClasspathModel` and the
  Kotlin `prepare` tasks (`PrepareKotlinIdeImportTaskModel`, `KotlinDslScriptAdditionalTask`, which only schedule
  tasks) have to be served by the plain run too. Or Gradle keys actions by classpath content.
- Test hygiene: an import into a project dir with stale `.idea/modules.xml` entries makes the IDE de-duplicate module
  names with directory prefixes; start fixture imports from an `.idea` holding only `gradle.xml`.
- Single invocation (D23): the plugin builds the `GradleModelHolderState` itself after the plain run and the patched
  resolver adds it instead of running the action. Model ids are `SimpleName + name.hashCode()` for the class part, the
  build root dir path hash and the project path; Ion payloads are read one document per fresh serializer; Java
  payloads of Kotlin models need the plugin to depend on the Kotlin plugin.

## Next milestone: incremental import on the IDE side (owner's question of 2026-10-16: "can the IDE side be faster?")

The IDE's own part of a sync is 23–27 s on gradle/gradle (library dependency data 12.8 s, project repository data
6.4 s, final tasks 2.6 s, the rest small; `results/m5-ide/baseline-NOTES.md`) and is the same in both worlds today
because the import always rebuilds every data node and re-applies every data service. The task transport gives the
IDE something the stock sync never had: a per-project change signal. Each bundle carries a fingerprint per model and
Gradle reports each `ideProjectModel` task as up to date or executed.

Sketch:
1. Keep the last import's fingerprints per project (bundle hash or the per-model fingerprints) in the IDE's external
   storage next to the project data.
2. After the plain run, compare: projects whose bundle is unchanged contribute no data nodes; changed projects are read
   and converted as today. Module dependencies between a changed and an unchanged project keep working because data
   nodes reference modules by id.
3. Apply with the partial-resolve mechanics IntelliJ already has (used for buildSrc and task-only refreshes), so the
   data services update the changed modules and leave the rest in place instead of removing what is absent.
4. Phase it: a no-change restart then costs the plain run (7.7 s) and a trivial import; an edit costs the changed
   projects' share of the 27 s. Streaming reads (D23) already start each project's read as its task finishes, so
   step 2 can run per project while the run is still going.

Risks: the data services' handling of partial trees for every data type the import touches (content roots,
libraries, facets, run configurations), the project-level data (libraries table, repositories) that is derived from
all projects, and anything the IDE recomputes globally after an import (the "final tasks" step).
