# M5: real IDE, headless (`idea.sh warmup`), gradle/gradle (2026-10-16): stock plugin vs task transport

IDE IU-253.28294.334 headless (D20), Gradle user home `~/.gradle-wbs`, wrapper distribution 9.9.0-milestone-2 (with
the `src` directory of D18), JDK 25, stock Gradle plugin. The warm-up indexes the project while and after the sync,
so the sync shares the CPU with indexing (the IDE would do the same on a first open).

| Run | Gradle resolve stage | of which Gradle build | data import | final tasks | import task total |
|---|---|---|---|---|---|
| 1, stock plugin, configuration cache directory deleted (store) | 56.7 s (08:53:30.47–08:54:27.16) | BUILD SUCCESSFUL at 50.2 s, entry stored at 50.6 s | 24.5 s | 2.1 s | 1 m 23.7 s |
| 2, same IDE install, project already imported | no sync: the warm-up configurator skips an imported project (24 s, 0 files indexed) | | | | |
| 3, fresh IDE system dir (= reload after an IDE restart), entry from run 1 present | 55.6 s (09:00:16.05–09:01:11.62) | BUILD SUCCESSFUL at 50.6 s, **entry stored** (a miss: the action's cache key carries the client classloader id) | 24.1 s | 2.5 s | 1 m 22.7 s |

Harness reference for the same build and distribution: builders warm plain 21.9 s, cold (M4) 51.6 s; the IDE's first
resolve is a cold daemon plus the store plus concurrent indexing. The 6 s between "entry stored" and "resolve stage
finished" is the IDE-side end of the model fetch (receiving the last models, converting the streamed states).

Run 3 is the IDE-side confirmation of the harness finding: an IDE restart makes the stock plugin's sync a full cache
miss (55.6 s resolve, no reuse) even with a valid entry on disk. The task transport's plain task run has a
classloader-independent key (M4 `launcher`: hit in a fresh client JVM); that is the scenario the patched IDE run
(`ide-plugin/`, D21) measures next.

## Task transport in the IDE (D21: plugin + patched `GradleModelFetchAction`, 17 IntelliJ models from the tasks, Gradle-owned builders kept)

| Run | Gradle resolve stage | of which Gradle build | IDE receive after the build | data import | final tasks | import task total |
|---|---|---|---|---|---|---|
| patched, fresh system dir, cache directory deleted (store) | **47.2 s** (10:31:03.59–10:31:50.74) | 41.1 s | 6.1 s | 23.5 s | 0.6 s | **1 m 11.5 s** |
| stock, same conditions (run 1) | 56.7 s | 50.2 s | 6.5 s | 24.5 s | 2.1 s | 1 m 23.7 s |
| stock, same conditions (run 4) | 51.6 s | 46.3 s | 5.2 s | 25.5 s | | 1 m 18.8 s |

Difference: −9.5 s on the resolve stage (−17 %), −9.1 s on the Gradle build (−18 %), −12.2 s end to end (−15 %). The
IDE-side parts are the same in both (receiving the last models, building the project model from the same models),
so the IDE adds about 30 s of constant to both and the percentage shrinks against the harness's `tasks` variant
(−18 % warm plain). 287 model tasks ran, the 9 known `apiStubElements` capture failures and nothing else, 0 missing
bundles, 17 providers dropped (`[wbs] dropping provider` in the IDE log).

Not yet in this run: the Gradle-owned models as payloads (the resolver would have to assemble `IdeaProject` from
them; dropping the builder empties the module list, LOG 2026-10-16), the IDE reading bundles itself instead of the
action re-streaming them, and the restart scenario.

## Fidelity in the IDE, gradle/gradle

The IDE's external storage after the import (`external_build_system/{modules,project}`: 1370 XML files, one per
module plus libraries and modules lists) compared between the stock run 4 and the patched run 2 with the project
hash normalized: **1370 identical, 0 different, none missing on either side**. The fixture's `.idea/modules/*.iml`
were identical too (10 files). With the 17 IntelliJ models coming from the task bundles, the IDE builds exactly the
project model it builds from the builders.

## Restart scenario (fresh IDE system dir, cache entry from the previous run present)

| IDE | Gradle resolve stage | Gradle build | cache | import task total |
|---|---|---|---|---|
| stock (run 3) | 55.6 s | 50.6 s | entry stored (miss) | 1 m 22.7 s |
| stock (run 5) | 51.8 s (10:39:52.21–10:40:43.98) | 46.8 s | entry stored (miss) | 1 m 18 s 804 ms |
| task transport (run 3) | 46.5 s (10:37:58.17–10:38:44.72) | 40.6 s | entry stored (miss) | 1 m 10.2 s |

Both miss, as the harness predicted for anything that runs inside IntelliJ's build action: the entry key carries the
client classloader id. The task transport is faster by the same margin as on a first import (−10 % resolve, −13 %
Gradle build) but does not turn the restart into a hit. That needs the plain task run with the IDE reading the
bundles itself (`design/m5-ide-consumer.md`, transport section), which the harness measured as a 3–5 s hit
(`launcher`); it is the next resolver change.

## Round 2a: plain model-task run before the action (`-Dwbs.transport.run=plain`)

The plugin's `preImportCheck` fetches `GradleBuild` and runs all 287 `ideProjectModel` tasks as a plain task run on
a second connection; the action then only reads the bundles (no model tasks scheduled inside it). Clean pair, the
plain run's output consumed and logged (`-Dwbs.transport.log`):

| Run | plain run (GradleBuild + tasks) | build action (remaining models) | resolve stage | import total |
|---|---|---|---|---|
| A2, fresh system dir, empty cache | 36.9 s (Gradle build 30 s), stored | 15.7 s, stored | 59.9 s | 1 m 25.4 s |
| B2, restart (fresh system dir, cache kept) | **10.5 s** (Gradle build 8 s), `GradleBuild` and task run **both reused** | 29.4 s, stored (miss) | 46.4 s | 1 m 10.7 s |
| stock, restart (runs 3 and 5) | | 55.6 / 51.8 s, stored (miss) | 55.6 / 51.8 s | 1 m 22.7 / 1 m 18.8 s |

The bundles are a cache hit after an IDE restart in the real IDE, 10.5 s for 287 projects in a fresh daemon (the
harness measured 3–5 s in a warm one). The resolve stage still costs 46 s because IntelliJ's build action runs for
the remaining models (`IdeaProject`, `KotlinDslScriptsModel`, eight small Kotlin ones) and misses on every restart by
construction (its key carries the client classloader id); on a first import the two stores cost more than one
(59.9 vs 47.2 s). Ways out: port the remaining models and drop the action (restart = 10 s plain run + the IDE's own
24 s import), or Gradle keys build actions by classpath content, which would make the stock action hit as well.
A first attempt of run A took 579 s in the plain run (daemon-side "BUILD SUCCESSFUL in 9m 33s"), not reproduced
by the CLI (29 s) nor by the instrumented rerun (36.9 s); the instrumented run consumes the run's output, the first
did not, which is the only difference found.

## Round 2b: Gradle-owned models assembled from the payloads, all 26 built-in providers dropped, plain pre-run

| Run | plain run | build action (eight small Kotlin models, buildscript classpath model, Kotlin prepare tasks) | resolve stage | import total |
|---|---|---|---|---|
| A, fresh system dir, empty cache | 62.6 s (store; 36.9 s in the 2a pair, fresh-daemon stores vary) | 13.6 s, stored | 84.4 s | 1 m 51.3 s |
| B, restart (fresh system dir, cache kept) | **11.8 s** (`287 actionable tasks: 287 up-to-date`, entry reused) | 33.6 s, stored (miss) | 53.3 s | 1 m 20.0 s |

Reading: the two Gradle-owned builders are gone from the action without loss (fixture project files identical;
gradle/gradle storage diff below), and after a restart the 287 bundles are a hit in 11.8 s. The action's miss is
not cheaper without them: in a fresh daemon it is the first configuration of gradle/gradle (33.6 s here against
13.6 s in run A, where the plain run had just configured the same daemon), plus its own store. Two Gradle
invocations on a first import cost more than one (84 s vs 47 s). The restart gain therefore needs the action gone
(the remaining small models and the Kotlin prepare tasks served by the plain run), or a content-based key for
build actions in Gradle; until then the plain run is a worse first import for no restart gain.

## Round 2c fidelity (all 35 built-in providers dropped)

Stock import vs 2c import, both taken today against the same Gradle cache: **1370/1370 external storage files
identical**. An earlier comparison against a stock snapshot from the morning showed 91 differences, all library
sources that IntelliJ attaches from the Gradle cache when the model has none; 266 sources jars had entered the cache
in between (a plain run without the idea-plugin configurator script resolved them). The cache is an input of the
IDE's project model: fidelity runs must share it.

## Round 2c: one Gradle invocation (D23, `-Dwbs.transport.run=only`), no build action

| Run | plain task run (287 `ideProjectModel` + `GradleBuild`) | holder state from bundles | resolve stage | data import | import total | cache dir |
|---|---|---|---|---|---|---|
| A, fresh system dir, empty cache | 36.6 s, stored | ~3 s (6321 payloads, 5755 models, 4 builds) | **41.0 s** | 23 s | **1 m 4.2 s** | 56 MB |
| B, restart (fresh system dir, cache kept) | **7.7 s, reused** | ~4 s | **13.5 s** | 27 s | **41.3 s** | 56 MB |
| stock, first import (runs 1, 4, today) | | | 56.7 / 51.6 / 55 s | 24–25 s | 1 m 23.7 / 1 m 18.8 / 1 m 20.8 s | 114 MB |
| stock, restart (runs 3, 5) | | | 55.6 / 51.8 s, miss | 24 s | 1 m 22.7 / 1 m 18.8 s | |

Fidelity: the restart run's external storage vs today's stock import, **1370 of 1370 files identical**.

Reading: on a first import the Gradle side is 41 s against 52–57 s (−25 %) and the whole import 1 m 4 s against
1 m 19–24 s. After an IDE restart the Gradle side is 13.5 s against 52–56 s (−75 %) and the whole import 41 s against
1 m 19–23 s (−48 %); what remains is the IDE's own data import (23–27 s), the same work on the same models in both
worlds, and the one-time plain-run hit plus state assembly (12 s). The sync produces the model count of the stock
sync (5755) under the same model ids, which is why the stock resolver builds the identical project.
Not served without the action, to add to the plain run: the Kotlin `prepare` task triggers (none needed on
gradle/gradle's JVM-only projects beyond `prepareKotlinBuildScriptModel`, which the assembled script model makes
unnecessary) and the MPP-only Kotlin models (null here).

## Where the IDE's own 27 s go (data import step timings from the warm-up log, restart run; stock in brackets)

| Step | one invocation | stock |
|---|---|---|
| Importing library dependency data | 12.8 s | 12.5 s |
| Importing project repository data | 6.4 s | 7.5 s |
| data import (final tasks) | 2.6 s | 2.7 s |
| library data / source set data / module dependency data | 1.5 / 1.3 / 0.8 s | 0.6 / 0.8 / 0.8 s |
| module data, content roots, project data, sdk, java data, annotation processing | 0.1–0.6 s each | 0.1–0.4 s each |

Identical in both worlds, as expected (same models, same data services). Two items dominate: attaching library
dependencies to ~900 source-set modules (12.5–12.8 s) and the project repository data service (6.4–7.5 s, for
287 projects' repository lists). Indexing of the 573k-file project ran concurrently in warm-up mode (17.5–18.9 s), so
these steps are inflated by CPU contention; a real IDE after a restart has its indexes.

What the task transport makes possible here and the stock sync cannot: every bundle carries per-model fingerprints and
the tasks were up to date, so the IDE knows which projects' models did not change since the last import. An import
that skips unchanged projects (IntelliJ's partial-resolve mechanics in the data services) would turn the 27 s into
work proportional to the change: near zero on a plain restart, and the 4 s of state assembly with it.

## Streaming bundle reads (read each bundle as its task finishes)

| Run | plain run | state ready after the run | resolve stage | import total |
|---|---|---|---|---|
| A, store, streaming | 34.5 s (287 of 287 read during the run) | 2.0 s | **37.6 s** (41.0 without) | **58.6 s** (1 m 4 s without) |
| B, restart, streaming | 12.6 s (hit; 7.7 s in the earlier run) | 4.3 s | 18.1 s (13.5 without) | 44.9 s (41.3 without) |

On a first import the reads overlap with the run and save about 3.5 s. On a restart they cannot: with every task up to
date Gradle reports all 287 completions in a burst at the end of the run, so the reads start when the run ends and
the 4 s of deserialization stays (the plain run's own 7.7–12.6 s is the fresh daemon's cache load, noisy). That
4 s and the IDE's 27 s only go with the incremental import (`design/m5-ide-consumer.md`, next milestone).

## IDE-side hot spots (thread samples of the IDE process every 0.5 s during an import, single-invocation mode)

167 samples; the plain run was a store this time (the prototype rebuild invalidated the entry), so 41 samples are the
IDE waiting for Gradle (`BlockingResultHandler.getResult`). Per import step, the importing thread's hot frames:

- **Importing library dependency data** (17 samples, ~8 s): `LibraryDependencyDataService.findLibraryOrderEntry` →
  `LibraryOrderEntryBridge.getLibrary` (legacy-bridge lookups per dependency), `ModuleEntityData.updateLinksIndex` and
  other workspace-storage index updates per entity, `LibraryEntityData.createEntity`.
- **Importing project repository data** (11 samples, ~6 s): `ProjectModifiableLibraryTableBridgeImpl.librariesArrayValue`
  recomputed over and over (the project library table's cached array, invalidated by every modification: O(libraries)
  per operation, with 577 project libraries), `LibraryEntityData.createEntity`, `AbstractModuleDataService.rearrangeOrderEntries`,
  `KotlinSourceSetDataService.getProjectPlatforms`. The step label lags: this is the library table being committed.
- The rest (module data, content roots, library data, final tasks): single samples each; VFS attribute writes and the
  workspace storage serializer.

Reading: the IDE's import time is IntelliJ's workspace-model legacy bridges doing per-library and per-module work for
about 900 modules and 577 project libraries; nothing in it depends on the transport. Two platform-side low-hanging
fruits for JetBrains: the repeated `librariesArrayValue` recomputation during a bulk import (looks quadratic) and the
per-dependency `findLibraryOrderEntry` lookups. From our side the lever stays the incremental import: fewer modules
touched means fewer of these operations.

## D24: the two IDE-side hot spots patched (owner's request; `AbstractIdeModifiableModelsProvider`, `LibraryDependencyDataService` overlaid into `lib/app.jar`)

A JFR profile of the restart import (10 ms sampling, `settings=profile`) confirmed and sharpened the jstack picture.
On the importing thread inside the data import stage (540 samples, 26.5 s):

| Share | Where | Why it is slow |
|---|---|---|
| 28 % | `KotlinGradleLibraryDataService.postProcess` → `IdeModifiableModelsProvider.findIdeLibrary` → `LibraryTable.ModifiableModel.getLibraries()` | the Kotlin plugin asks for every library by data node; `getLibraries()` is the table bridge's `librariesArrayValue`, a cached value on the diff storage that the loop's own `setKind` invalidates before the next call: the 577-library array is rebuilt from the entity storage 577 times (this is what the "project repository data" step label covers) |
| 28 % | `LibraryDependencyDataService.findLibraryOrderEntry` → `LibraryOrderEntryBridge.getLibrary` | for each module library it adds, the service scans all order entries of the module and resolves each entry's library through the workspace storage; counters in the patched method: **103,090 lookups, 21.6 million entries scanned, 21.5 million resolutions**, 99 % of the searched libraries unnamed (file dependencies), 7.5 million of the entries unnamed too |
| 12 % | `ProjectDataManagerImpl.commit` | workspace model commit |
| 8 % | `setLibraryScope` → `LibraryOrderEntryBridge.setScope` | every scope change re-indexes all links of the module entity |
| 7 % | `importMissingModuleLibraryOrderEntry`, `registerPaths` | library entity creation, root registration |

Fixes (D24, Java only, stock semantics kept): `findIdeLibrary` looks the library up by name (`isRelated` is name
equality, names are unique in the table) and only scans as a fallback; `getLibraryByName` memoized per import;
`findLibraryOrderEntry` compares the library's `LibraryId` with each entry's dependency item (plain values) and falls
back to the stock scan. A first attempt that skipped entries by name did nothing (both names null for the unnamed
module libraries).

| Restart import (fresh system dir, cache kept) | data import stage | library dependency step | project repository step | whole import |
|---|---|---|---|---|
| unpatched IDE classes (round 2c, streaming) | 26.5–27 s | 10.6–13.5 s | 6.4–7.5 s | 41.3–44.9 s |
| D24, `findIdeLibrary` + name filter (runs B1, B2, profiled) | 23.0 / 18.6 / 21.9 s | 11.2–13.5 s | 2.7–3.3 s | 37.6 / 38.4 / 38.5 s |
| D24b, `findIdeLibrary` + id lookup (runs B3, B4, profiled) | **16.9 / 17.2 / 18.0 s** | **9.0–9.6 s** | **2.9 s** | **30.9 / 33.9 / 33.8 s** |
| stock IDE, stock sync (runs 3, 5) | ~24 s | 12.5 s | 7.5 s | 1 m 18.8–1 m 22.7 s |

External storage after runs B2 and B4 vs today's stock import: 1370 of 1370 files identical both times. The restart
import is now 31–34 s against the stock 1 m 19–23 s (−58 %); the Gradle side is 12–16 s of it and the IDE's data import
17–18 s. The two fixes apply to the stock sync as well (the IDE's data import does not depend on the transport).

What remains in the library dependency step (second profile, 97 samples): `LibraryOrderEntryBridge.setScope` 39 %
(each change re-indexes the module entity's links: quadratic per module), `LibraryDataService.registerPaths` 21 %,
`ModifiableModuleLibraryTableBridge.createLibrary` 13 % (`generateLibraryEntityName` checks candidates against the
existing names), `findLibraryOrderEntry` 5 %. These sit in the workspace model's Kotlin bridges; not patched here.

For JetBrains, with the profiles in the scratchpad (`ide-import.jfr`, `ide-import-d24.jfr`):
1. `ProjectModifiableLibraryTableBridgeImpl.librariesArrayValue` is rebuilt after every library modification while a
   data service iterates `getLibraries()`; quadratic in the number of project libraries.
2. `LibraryDependencyDataService.findLibraryOrderEntry` resolves every entry's library per lookup; quadratic in a
   module's dependencies, for every module library.
3. `LibraryOrderEntryBridge.setScope`/`setExported` and `ModuleEntityData.updateLinksIndex`: a scope change re-indexes
   all links of the module.
