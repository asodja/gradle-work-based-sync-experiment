# Work-based sync: report (2026-10-16, after M4, D17–D19 and the M5 rounds in the real IDE)

Question: does IntelliJ's Gradle sync get faster when its per-project tooling models are produced by Gradle
tasks that write files instead of model builders running in the daemon's model-building phase?

Benchmark: gradle/gradle at `dedc5d14f2d` (254 build-script projects, 287 projects over 4 builds, Kotlin DSL),
Gradle 9.9.0-milestone-2 (the wrapper's version), IntelliJ IDEA 2025.3 tooling jars, this machine
(`design/environment.md`). The harness (`harness/`) runs IntelliJ's real `GradleModelFetchAction` with the IDE's own
22 resolver extensions and measures the Gradle side (points 1–9); the installed IDE runs headless for the end-to-end
numbers (points 10–12, D20).

## Short answer

1. **No-change re-sync is already free.** Under Isolated Projects, Gradle caches the complete result of IntelliJ's
   build action in the configuration cache entry. A no-change re-sync of gradle/gradle (5755 models, 113 MB) takes
   1.4–1.7 s with today's builders and no builder runs. Tasks cannot beat that (task variants: 1.7–3.3 s).
2. **Where builders cost time**: a cache miss (every IDE restart, since the entry key includes the client
   classloader's UUID; any invalidated input) costs 19–25 s, and an edit of one build script 14 s, because every
   model is rebuilt. Two thirds of the model time is the source set dependency model.
3. **Tasks help there, by about 30 %, not more**: with all 17 IntelliJ per-project models produced by one task per
   project (`ideProjectModel`) and no builder requested, gradle/gradle costs 14.8 s instead of 21.4 s warm without
   the cache, 10.2 s instead of 14.1 s after a build-script edit, 11.5 s instead of 16.6 s after a settings edit,
   and 11.5 s instead of 25.1 s on the warm miss (single runs). The gate (D15, 30 %) is met or missed by a few
   points depending on the scenario (`results/m4-summary.md`).
4. **The biggest effect is structural, not faster work**: a plain task run (`--launcher`: `BuildLauncher` instead of
   a build action) is served from the configuration cache in a *fresh* client JVM, 2.9 s where every build-action
   variant misses and pays 19–25 s. An IDE restart would re-sync from the cache. Its hit is slower than the
   action's (3.3 s vs 1.65 s: output checks of 132 MB of bundles plus client-side reading), and its edit case
   is 11.4 s vs 14.1 s.
5. **Moving only the IntelliJ models while the IDE still runs the remaining builders did not pay in M4** (`tasks`
   variant: 22.4 s warm plain, no better than builders): the action waited for the serial `KotlinDslScriptsModel`
   builder. **With the Gradle-owned models as payloads too (D17) the same variant is faster than builders**:
   17.9 s warm plain vs 21.9 s, 13.0 vs 12.5 s on the edit (`results/m4h-NOTES.md`). `IdeaProject` becomes an
   `ideaModule` payload per project (255/255 identical to the builder's modules on gradle/gradle, customizations
   included where the project applies the idea plugin), `KotlinDslScriptsModel` a `kotlinDslScript` payload per
   project with its build script, precompiled script plugins and, on the root project, the settings and init
   scripts (every script identical but for the accessors jar hash). The IDE assembles the build-level values; that
   is the M5 resolver change.
6. **Why not more**: the configuration cache resolves dependencies when the entry is stored, so a task cannot
   defer that work to execution; the models are captured at configuration time and a miss still computes them all.
   Configuration itself (8.7–9.3 s warm, 4.5 s after an edit) is unchanged. And on this Gradle version a
   build-script edit invalidates the whole entry in every transport, so the edit case is a full reconfiguration
   for everyone. The task version wins by parallel capture at store time, by not re-sending 113–132 MB of models,
   and by the classloader-independent cache key of a plain task run.
7. **Fidelity**: all 17 ported models reproduce the builder output on gradle/gradle. 15 are identical for every
   project (`results/m3-ports/compare-mechanical.md`, `compare-hard.md`); `GradleTaskModel` differs only by the new
   task; `ProjectDependencies` differs in 4–9 projects per run pair by the sibling order inside its dependency graph
   (same nodes). Getting the dependency model identical needed one IntelliJ builder context per build, filled with
   every project's artifact-index entries when the project is evaluated (D16), the same cross-project read the
   builders do today.

8. **Two things found on the way that are not about tasks** (LOG 2026-10-16). (a) Every sync of a Kotlin DSL
   build whose distribution has no `src` directory looks the Gradle distribution sources up on services.gradle.org,
   once per build, with a lock held: on the owner's connection 8–16 s, and it fails anyway for a snapshot version. It
   was most of the serial `KotlinDslScriptsModel` cost in M4 and, in the task world, stalled every capture thread
   behind it. A persistent per-version cache in Gradle would remove it; the measurements from `m4g` on use a `src`
   directory (D18), as the `-all` distribution has. (b) The models are mostly repeated file paths (151,501 path
   entries over 809 distinct paths in the script payloads alone); deflating the payloads (D19) halves the task
   world's configuration cache entry (155 → 85 MB) and brings the `tasks-only` hit back to 2.2 s. The erratic hits
   seen in several series (3.0–8.6 s for `tasks`, 2.0–8.5 s for builders in `results/heap-NOTES.md`) are full-GC
   pauses of gradle/gradle's 3.3 GB daemon running at 93–99.9 % old generation, in both worlds; the builders' daemon
   holds more (2.34 GB live after a full GC vs 1.24 GB for tasks-only).
9. **Final harness comparison, network-free** (`results/m4h-NOTES.md`, builders from `results/m4g-builders/`): `tasks-only`
   15.3 / 2.16 / 11.2 s against builders 21.9 / 1.47 / 12.5 s (warm plain / hit / edit): −30 %, +0.7 s, −10 %. The IDE
   numbers of point 11 supersede these for the question the owner asked.

10. **In the real IDE, headless** (D20, D21; `results/m5-ide/baseline-NOTES.md`): IntelliJ runs its Gradle sync
    without a display through `idea.sh warmup`, so the IDE side was measured here after all. gradle/gradle, fresh
    IDE state, empty cache: stock plugin 56.7 s resolve stage and 1 m 23.7 s import; with the 17 IntelliJ models
    coming from the tasks (a small plugin and one patched IDE class) 47.2 s and 1 m 11.5 s: −18 % on the Gradle
    build, −15 % end to end. The IDE adds about 30 s that the transport does not touch (receiving the last models,
    building the project model). On the fixture the task transport produces module files identical to the stock
    plugin's. An IDE restart is a full miss for the stock plugin (55.6 and 51.8 s, entry stored again) and still a miss
    with the tasks running inside the build action (46.5 s). With the tasks run as a plain task run before the
    action, the bundles are a hit after a restart (10.5 s for 287 projects, `GradleBuild` and the task run both
    reused) and the remaining 29 s is IntelliJ's action for the Gradle-owned models, which misses by construction;
    porting those models too would make a restart the 10 s plain run plus the IDE's own 24 s import. The IDE's project model is identical in both worlds: 1370 external
    storage files on gradle/gradle, 10 module files on the fixture, no difference.

11. **One Gradle invocation in the real IDE (D23, `results/m5-ide/baseline-NOTES.md`)**: with the build action gone
    and the IDE building its model holder from the bundles, gradle/gradle imports in 1 m 4 s on a first open (stock
    1 m 19–24 s; Gradle side 41 vs 52–57 s) and in **41 s after an IDE restart** (stock 1 m 19–23 s; Gradle side
    **13.5 vs 52–56 s**, the plain task run being a cache hit in 7.7 s). The project model is identical to the stock
    plugin's (1370 of 1370 storage files). The remaining time is the IDE's own data import, 23–27 s in both worlds.
    This is the number the experiment set out to find: on the Gradle side a re-sync after a restart drops by three
    quarters; end to end by about half.
12. **The IDE's own import has two quadratic library lookups (D24, `results/m5-ide/baseline-NOTES.md`)**: a JFR
    profile of the import puts 28 % of the importing thread in the Kotlin plugin's `findIdeLibrary` (the project
    library table rebuilds its library array after every `setKind`, 577 times for 577 libraries) and 28 % in
    `LibraryDependencyDataService.findLibraryOrderEntry` (21.6 million order entries scanned and resolved through the
    workspace storage for 103k unnamed module libraries). Two Java classes patched with the stock semantics (lookup by
    name, lookup by library id) cut the data import stage from 26.5–27 s to **17–18 s** and the restart import to
    **31–34 s** (stock 1 m 19–23 s, −58 %), storage identical. The remainder of that step is in the workspace model's
    Kotlin bridges (`setScope` re-indexing all links of a module, `registerPaths`, `createLibrary`). Both fixes apply to
    the stock sync too; three items for JetBrains are listed in the notes.
13. **Stable three-way comparison, restart scenario, medians of 3 rounds** (`results/m5-ide/stable-NOTES.md`):

    | Configuration | Gradle side | IntelliJ side | whole import |
    |---|---|---|---|
    | stock IDE, stock sync | 51.8 s | 27.1 s | 1 m 20.4 s |
    | tasks, one invocation (D23) | 10.3 s (−80 %) | 27.2 s (0 %) | 37.6 s (−53 %) |
    | tasks + import fixes (D24) | 10.0 s (−81 %) | 19.0 s (−30 %) | 29.2 s (−64 %) |

    The project model is identical in all three. The two import fixes would also take 30 % off the IntelliJ side of the
    stock sync, about 10 % end to end there, because the stock sync is dominated by the Gradle side.
14. **The same three configurations on a forced configuration cache miss** (settings script content changed before
    every run; `results/m5-ide/miss-NOTES.md`), medians of 3 rounds:

    | Configuration | Gradle side | IntelliJ side | whole import |
    |---|---|---|---|
    | stock IDE, stock sync | 63.1 s | 28.8 s | 1 m 32.4 s |
    | tasks, one invocation (D23) | 40.7 s (−35 %) | 23.0 s (−20 %) | 1 m 3.6 s (−31 %) |
    | tasks + import fixes (D24) | 40.2 s (−36 %) | 12.7 s (−56 %) | 52.9 s (−43 %) |

    On a miss the task run configures all 287 projects like the stock action (27 s) but all 287 model tasks are
    up to date (unchanged fingerprints), so it skips the model building, not the configuration. Caveat from the
    build operation traces (`results/m5-ide/phases-NOTES.md`): these IDE chains stopped the daemon before every run,
    and a cold daemon roughly doubles every miss (plain run 27–29 s cold, 13–15 s warm) and hurts the builders more
    than the task run. With a warm daemon the two are within noise on a miss (builders 11.9–14.2 s, task run
    13.0–15.9 s), because the model capture then happens inside the configuration cache store. The robust gains are
    the hit (point 13) and the IDE-side fixes; the miss gain depends on daemon warmth.
15. **The same on a miss without any script change** (cache dir emptied before every run; `results/m5-ide/emptycc-NOTES.md`),
    medians of 3 rounds: stock 54.5 s Gradle / 26.4 s IntelliJ / 1 m 21.1 s; tasks 39.1 / 22.0 s / 1 m 1.1 s (−28 % /
    −16 % / −25 %); tasks + fixes 39.2 / 13.1 s / 52.3 s (−28 % / −50 % / −35 %). The three kinds of miss side by side:

    | Scenario | stock | tasks | tasks + fixes |
    |---|---|---|---|
    | restart, cache kept (stock misses, tasks hit) | 1 m 20.4 s | 37.6 s (−53 %) | 29.2 s (−64 %) |
    | cache emptied, no script change | 1 m 21.1 s | 1 m 1.1 s (−25 %) | 52.3 s (−35 %) |
    | settings script changed | 1 m 32.4 s | 1 m 3.6 s (−31 %) | 52.9 s (−43 %) |

16. **Compact record format for the dependency model (D26, `results/m5-ide/compact-NOTES.md`)**: a per-project string
    table plus varint records instead of IntelliJ's Ion objects, expanded by the consumer; configuration cache entry
    56 → 39 MB, bundles 51.9 → 43.1 MB, dependency model bytes −44 %, identical models for 287 of 287 projects. No
    measurable time gain on this machine (hit load 0.2 s in both forms, IDE restart import 27.7–28.3 vs 27.9–29.7 s):
    the encoding is worth ~10 %, the data model 3×, and the store remains the capture.

17. **IntelliJ IDEA 2026.2.2 (build 262.10315.125), restart scenario, medians of 3 rounds**
    (`results/m5-ide/stable-262-NOTES.md`): stock 48.2 s Gradle / 24.8 s IntelliJ / 1 m 13.5 s; tasks + fixes
    10.5 / 21.6 / 31.8 s (−78 % / −13 % / −57 %); data import stage 21.2 → 16.1 s. 2026.2's extra
    `GradleDslBaseScriptModel` request costs 81–95 ms. Tasks without the fixes: 10.9 / 27.0 / 38.0 s (−48 %).
    Forced miss (settings script changed per run, `results/m5-ide/miss-262-NOTES.md`): stock 53.3 / 25.3 / 1 m 19.2 s,
    tasks 41.3 / 22.2 / 1 m 3.6 s (−20 %), tasks + fixes 41.3 / 13.5 / 54.8 s (−31 %). The IntelliJ side is smaller on a
    miss than on a hit because the headless warm-up indexing has finished by then; the miss column is the warm-index
    number. The Mac kit for this build is `dist/wbs-ide-kit-262.10315.125.tar.gz` (D27).

## What the kit changes (the table of changes)

| # | Change | Side | Where | Decision | Measured effect |
|---|---|---|---|---|---|
| 1 | One `ideProjectModel` task per project captures IntelliJ's per-project models at configuration time with IntelliJ's own builders and writes a bundle file; the IDE reads bundles instead of requesting models through a build action | Gradle (init script + `ide-model-tasks.jar`) | `prototype/.../ideTask/` | D8, D9 | Gradle side of a restart sync 51.8 → 10.0 s (2025.3), 48.2 → 10.5 s (2026.2.2): the task run hits the configuration cache, the stock action never can |
| 2 | Gradle-owned build models (`IdeaProject`, `KotlinDslScriptsModel`) as per-project payloads, assembled on the IDE side | Gradle + plugin | `GradleOwnedModels`, `IdeaProjectFromPayloads`, `KotlinDslScriptsModelFromPayloads` | D14, D17 | removes the last requests that configured the build (14 MB of models) |
| 3 | Payloads deflated; the dependency model as a compact string table + records | Gradle | `ModelPayload`, `CompactDependencies` | D19, D26 | bundles 132 → 43 MB, configuration cache entry 56 → 39 MB; no time change |
| 4 | Single Gradle invocation: a plain task run on a second connection, the IDE's model holder state built from the bundles, no build action | plugin + patched `GradleProjectResolver` | `WbsTaskTransportResolverExtension`, `TaskTransport` | D23 | no configuration at all after a restart; streaming bundle reads −3.5 s on a first import |
| 5 | Built-in model providers of the ported models dropped from the fetch action | patched `GradleModelFetchAction` | `-Dwbs.transport.drop` | D21 | prerequisite for 1 and 4 |
| 6 | IDE import fix: library order entries looked up by library id instead of a scan per entry (`findLibraryOrderEntry`) | IDE platform | `LibraryDependencyDataService` | D24 | library dependency step 12–13.5 → 8–9 s |
| 7 | IDE import fix: memoized library-by-name lookup (`findIdeLibrary`) instead of rebuilding the library array per lookup | IDE platform | `AbstractIdeModifiableModelsProvider` | D24 | project repository step 6.4 → 2.6–3.0 s |
| 8 | 2026.2 support: patches ported, `GradleDslBaseScriptModel` requested for the Kotlin plugin, jar layout found by content; Mac kit | build + kit | `ide-plugin/src/patched-262`, `install-mac.sh` | D27 | 2026.2.2 restart sync 1 m 13.5 → 31.8 s |

Not changed: IntelliJ's resolvers and data services beyond 6 and 7, the Gradle distribution, the project's build.

## Scenarios and medians (M4, `results/m4-summary.md`)

| Scenario | builders | tasks | tasks-only | launcher |
|---|---|---|---|---|
| 1 cold (daemon stopped, CC off, IP off) | 51.6 s | 45.6 s | 30.0 s | 29.2 s |
| 2 warm plain (CC off, IP off) | 21.4 s | 22.4 s | **14.8 s** | 16.6 s |
| 3 warm IP, hit | 1.65 s | 1.75 s | 1.68 s | 3.3 s |
| 3 warm IP, miss (run 0, same JVM; single run) | 25.1 s | 20.0 s | **11.5 s** | 13.5 s |
| 3 warm IP, fresh client JVM (single run, traced) | 19.2 s miss | 20.0 s miss | 14.1 s miss | **2.9 s hit** |
| 4 incremental IP (one build script edited) | 14.1 s | 16.5 s | **10.2 s** | 11.4 s |
| 7 full invalidation IP (settings script edited) | 16.6 s | 15.9 s | **11.5 s** | 11.6 s |

Variants: `builders` = today's IDE; `tasks` = ported models from task bundles, remaining builders still run (an
unchanged IDE could do this); `tasks-only` = no builder at all (the floor; needs the IDE changes of M5);
`launcher` = plain task run plus client-side reading (D14). Cold runs are not comparable with M0 (disk filled
during the chain, `results/m4-NOTES.md`).

## What was measured and how

- Harness: `harness/README.md`. Runs inside one client JVM (`--repeat`) because of the classloader UUID in the cache
  key; reports phase marks, task outcomes, cache lines and whole-machine stall gaps.
- Per-model costs: build operation traces of a warm plain sync and a warm miss (`results/m0-baseline/models.md`,
  `results/m4-*/<scenario>-models.md`).
- Comparison: byte dumps of every model in both modes, canonical Ion text (`prototype/compare-models.sh`), and
  consumption-order deserialization for the cross-referencing dependency models (`prototype/compare-ordered.sh`).

## Findings and blockers (chronological detail in `LOG.md`)

- Whole-action result caching under IP (`DefaultConfigurationCache.loadOrCreateModel`), independent of the opt-in
  per-model cache; key includes the Tooling API client classloader UUID. A plain task run's key does not.
- Gradle regression: `isolated-projects.caching=tooling` fails after a cache load on private/final attribute rule
  classes (`ImmutableAttributesSchemaCodec` no longer matches `ValidatingAction`); unreported upstream.
- Dependency resolution cannot be deferred past the configuration cache store (`ArtifactCollectionCodec`).
- A project build script is a build-scoped input (`onScriptFileResolved`), so IP's per-project entry update is not
  reached from the IDE on 9.9.0-milestone-2, not even for a plain task run.
- `@Nested` task inputs are materialized during task graph calculation, serially; model payloads must be
  `@Internal` with a fingerprint `@Input`, or the captures run one after another (20 s on gradle/gradle).
- Intermittent whole-machine stalls of ~100 s on this sandbox (detected via idle-daemon health-check gaps).
- IntelliJ's Ion serializers are not thread-safe and keep object tables across documents; readers must be fresh
  per document on the consumer side, or read all documents of a class in order (as the IDE does).
- `PrepareKotlinIdeaImportTaskModelBuilder` updates the start parameter without synchronization while IntelliJ
  requests it in parallel (potential lost prepare tasks on large Kotlin builds; not verified in the IDE).
- `GradleBuildScriptClasspathModel` needs the parent project's model from the shared builder context; not ported.
- Reading another project's source-set output dirs realizes that project's lazily registered tasks; done concurrently
  from many captures without the project's lock it fails (`TaskCreationException`) and silently drops the dependency
  model for that run. IntelliJ's task warm-up request hides this in the builder world; the task version realizes the
  state in each project's own `afterProject` hook (D16).
- Nine gradle/gradle projects remove `apiStubElements`; `ProjectExtensionsDataBuilderImpl` fails on them in both modes.

## Not measured

- IDE-side phases in a real IDE with a display: the IDE runs headless here (`idea.sh warmup`, D20); its stage and step
  timings come from the warm-up log, with indexing running concurrently.
- Scenario 6 (incremental with the per-model cache): blocked by the Gradle regression (D10).
- The IDE-side assembly of `IdeaProject` from the `ideaModule` payloads and of the Kotlin plugin's script models from
  the `kotlinDslScript` payloads (M5); the payloads themselves exist and match (D17).
