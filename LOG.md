# Log

Dated, append-only. One entry per measurement, finding or decision. Newest at the bottom.

## 2026-10-15

- Plan and context written from the preparatory investigation (see design/). No code in this repo yet.
- Disk cleanup with owner approval: removed worktree `gradle-Upgrade-GradlePluginDevelopmentExtension.automatedPublishing`
  (PR head `8779a723a32` is on `origin/gradle10/provider-api-migration`), `~/.cache/JetBrains/Toolbox/{backup,download}`,
  and scratch dirs of finished sessions under `/tmp/claude-1000`. Free space 9.3 GB → 28 GB.
- Environment check for M0: no display and no Xvfb on this machine, so no IDE sync is possible here
  (DECISIONS D1). gradle/gradle master at `dedc5d14f2d` pins wrapper `gradle-9.9.0-milestone-2`
  (already cached) and daemon toolchain 25 (`gradle/gradle-daemon-jvm.properties`); default JDK here is
  25.0.4. Benchmark worktree created: `~/projects/gradle-wbs-benchmark` at `dedc5d14f2d`, detached.
- The fetch-model build operation (`DefaultBuildTreeModelCreator`) has no details class, so
  `-Dorg.gradle.internal.operations.trace.filter` cannot isolate it; profiling runs record the full trace
  (DECISIONS D3). `org.gradle.internal.operations.trace` is an `InternalOption`, so it can be passed as a
  build `-D` argument without changing daemon JVM args.
- IntelliJ IDEA 2025.3 (253.28294.334) is the latest release; in 2025.3 the phases are
  `PROJECT_LOADED_PHASE`, `PROJECT_MODEL_PHASE`, `SOURCE_SET_MODEL_PHASE`, `DEPENDENCY_MODEL_PHASE`,
  `ADDITIONAL_MODEL_PHASE` (no base-script phase yet; `design/context.md` describes master).
- First build in the benchmark worktree (`./gradlew help -g ~/.gradle-wbs --no-build-cache --no-scan`):
  BUILD SUCCESSFUL in 25 min, 270 tasks executed (all build-logic compilation), CC entry stored, no IP
  problems. `~/.gradle-wbs` is 2.0 GB afterwards. The daemon ran on JDK 25 (toolchain 25 from
  `gradle-daemon-jvm.properties`).
- Harness smoke test on `harness/fixture` (3 Java projects, CC + IP on, Gradle 9.9.0-milestone-2): all 22
  `projectResolve` extensions of the installed IDE instantiate in a plain JVM and yield their providers
  (no fallback needed); 85 models arrive over the five phases; ~3.5 s warm with a new JVM per run.
- **Finding (CC key):** the configuration cache key of a tooling build action hashes the serialized action
  payload (`DefaultBuildTreeActionExecutor.payloadHashProvider` → `AbstractToolingModelRequirements.appendKeyTo`),
  and that payload carries the client classloader's random UUID (`ClientSidePayloadClassLoaderRegistry`,
  `ClassLoaderDetails.uuid`). So every new TAPI client JVM gets a new key and can never reuse an entry; the
  IDE reuses one classloader per process, so entries are reused within an IDE session and lost on restart.
  The harness therefore repeats runs inside one JVM (`--repeat`).
- **Finding (whole-action caching):** on the fixture with CC + IP on and a reused entry, the entire phased
  action result is served from the cache: identical 85 models and byte counts, build time 60 ms, harness total
  96 ms (vs ~3.5 s on a miss). No model builder runs. So Gradle 9.9 already makes a no-change re-sync near
  free under IP, independent of the opt-in per-model cache. The `-Porg.gradle.kotlin.dsl.provider.cid`
  property IntelliJ changes on every sync is explicitly ignored by the fingerprint (`Workarounds.kt`).
  Consequence for the plan: the headline comparison (scenario 3) is against this whole-action cache, and the
  decisive question becomes what invalidates it on gradle/gradle and how incremental re-sync (scenario 4)
  behaves. To be confirmed on gradle/gradle before applying the M0 gate.
- Mechanism of the whole-action caching (verified in the pinned source): IP mode registers
  `ConfigurationCacheBuildTreeLifecycleControllerFactory` (`BuildTreeModelControllerServices.kt`), whose model
  creator wraps `fromBuildModel` in `DefaultConfigurationCache.loadOrCreateModel`: loaded entry → return the
  saved action result, no builders run; miss → run, then save unless the result has failures. `design/context.md`
  §3 corrected accordingly (it previously described only the opt-in per-model cache).
- **First harness sync of gradle/gradle** (`dedc5d14f2d`, 9.9.0-milestone-2, project defaults CC + IP on, fresh
  `~/.gradle-wbs` dependency cache, traced): BUILD SUCCESSFUL in 26 min 9 s, harness total 1570 s. Cold with all
  dependency downloads through the proxy, so a floor only. 4 builds (root + 3 build-logic builds), 287 projects,
  5755 models, 113 MB serialized: PROJECT_MODEL 287 models / 0.2 MB, SOURCE_SET 287 / 3.1 MB, DEPENDENCY 287 /
  28.9 MB, ADDITIONAL 4894 / 81 MB (IdeaProject + KotlinDslScriptsModel dominate the bytes). Phase ends (s from
  action start): projects loaded 124, project model 125, source sets 154, dependencies 1106, additional 1570.
  Tasks: 27 executed (`checkKotlinCompilerVersion` in build-logic), 271 up-to-date, 66 skipped. No IP problems
  reported; "Could not resolve Gradle distribution sources" warning; CC entry stored. Per-model table (parallel,
  overlapping wall time): `results/m0-baseline/first-cold-sync-models.md`. Top by summed fetch time:
  GradleSourceSetDependencyModel 7250 s (max 623 s, downloads), KotlinGradleModel 868 s, KotlinDslScriptsModel
  345 s (4 builds), GradleSourceSetModel 162 s, DummyModel 112 s (first request, waits for configuration),
  GradleBuild 11 s; everything else < 1 s per model summed over 287 projects. Configure project: sum 369 s, root
  57 s. Operation trace of this sync: 911 MB, 668k operations (too heavy for timed runs, as decided in D3).

- **M0 baseline, scenarios 1, 2, 4, 5** (builder version, gradle/gradle `dedc5d14f2d`, 9.9.0-milestone-2, 3 timed runs each after a
  warm-up run 0 where the script does one). Median total_ms (min-max): 1 cold 34696 (34520-38185); 2 warm plain 16895 (16691-18532);
  3 warm IP 1445 (1055-2330, run earlier by the lead session); 4 incremental 14933 (14306-15963); 5 IP + model cache 1278
  (1234-1864). Tasks: scenarios 1, 2, 4 executed=27 skipped=66 up-to-date=271; scenarios 3, 5 up-to-date=1. All runs status=OK, no
  IP problems in the logs. Details: `results/m0-baseline/NOTES.md`, table: `results/m0-baseline/summary.md`.
- **Scenario 6 (incremental, IP + opt-in per-model cache `isolated-projects.caching=tooling`) fails on Gradle
  9.9.0-milestone-2**: run 0 and the traced run (fresh client JVM, entry stored) succeed in 20–23 s, but every
  incremental run from the same JVM after the one-line edit ends in `CONFIGURE FAILED` after < 1 s: "Could not
  resolve project ':build-logic-settings:…' … Could not create an instance of type
  org.jetbrains.kotlin.gradle.plugin.mpp.KotlinUsages$KotlinUsagesCompatibility … Class … is private", then
  "Configuration cache entry updated for no projects, no projects up-to-date" (`results/m0-baseline/6-{1,2,3}.txt.build.log`).
  The harness marks them `status=FAILED`; summarize.py excludes them. Without the option (scenario 4) the same edit
  invalidates the entry and the sync succeeds in 14.9 s. Handed to the investigator subagent; until resolved the
  builder-side competitor for the incremental case cannot be measured on this Gradle version.
- M0 cost tables written: `results/m0-baseline/models.md` (scenario totals, per-phase wall times, per-model sums for
  scenarios 2, 3-miss, 4). Operation traces (5.9 GB) deleted after aggregation; the `*-models.md` files remain.
  Free disk 16 GB.
- **M2 first end-to-end on the fixture** (`prototype/`, harness `--tasks`): `ideProjectModel` registered in every
  project from the init script via `gradle.lifecycle.beforeProject`, scheduled per project by
  `IdeModelTaskRequestBuilder` in the projects-loaded phase, executed (4 tasks), bundle files read by the harness in
  the source set phase and fed to IntelliJ's consumer. `GradleSourceSetModel` bytes for the Java projects were
  byte-identical to the builder output in one run; in another the only difference was the order of the
  `sources` map entries (SOURCE/RESOURCE), which also differs between two builder runs: IntelliJ serializes that
  map in hash order. The comparison therefore canonicalizes (sorted struct fields, map entries sorted by key;
  `harness` `IonText --canonical`, `prototype/compare-models.sh`). Expected, accepted differences in task mode:
  `GradleTaskModel` and `IdeaProject` list the new `ideProjectModel` task (group "other", not public).
- **Scenario 6 failure explained (investigator, code-verified on `dedc5d14f2d`, identical to `v9.9.0-M2` in every
  cited file).** `isolated-projects.caching=tooling` turns on, besides the per-model cache, the per-project
  component cache (`BuildTreeModelControllerServices.kt:176` → `ConfigurationCacheAwareLocalComponentCache` →
  `ProjectMetadataController`), which serializes each project's `ImmutableAttributesSchema`. Since `db79ac69208`
  (2026-07-16) `DefaultCompatibilityRuleChain.createAction` wraps rules in a private `ValidatingAction`, so
  `ImmutableAttributesSchemaCodec`'s rule branch (`:85-95`) no longer matches and rules are written as opaque beans;
  the bean's `Instantiator` field is restored by `ServicesCodec` as the global `InstanceGenerator` service
  (`GlobalScopeServices.java:171-174`, `decorateLenient()`), whose class generator rejects private and final classes
  (`AsmBackedClassGenerator.java:380-390`). The normal path uses `instantiatorFactory.inject()` which takes the
  `NoOpBuilder` shortcut. KGP's `KotlinUsages.KotlinUsagesCompatibility` is private → every resolution after a cache
  load that reaches it fails; any final Kotlin rule class would too. Not fixed on master; `gh search issues` finds
  nothing (2 queries). Smallest fix: expose the delegate of `ValidatingAction` and match it in the codec's
  `writeStrategy` (2 jars: dependency-management, dependency-management-serialization-codecs). See DECISIONS D10.
- **M2 on gradle/gradle, task mode (`ideProjectModel` = GradleSourceSetModel), one client JVM, 3 runs:**
  run 0 (miss): 135.7 s, of which 120 s before projects loaded (bug: my request provider asked the 287 projects
  for `IdeModelTaskRequest` sequentially, and in the projects-loaded phase each project-scoped request configures
  its project, so configuration ran one project at a time; fixed with `GradleModelProviderUtil.buildModels`, the
  parallel path IntelliJ's providers use; re-measured below). 314 tasks executed (287 model tasks + 27 build-logic
  checks), CC entry stored, no IP problems. Run 1 (no change): **2.2 s**, "Configuration cache entry reused",
  `tasks={up-to-date=288}` (287 model tasks + prepareKotlinBuildScriptModel). Run 2 (comment appended to
  `subprojects/core/build.gradle.kts`): 19.7 s, entry stored, `up-to-date=558` (all 287 model tasks up to date
  because the serialized model did not change; `:core` was reconfigured, the remaining builders ran as before).
  Comparison `prototype/compare-models.sh` (canonical Ion): **285 of 287 `GradleSourceSetModel` identical**;
  `:docs` and `:internal-build-reports` differ only by extra `configurationArtifacts` entries with empty values on
  the builder side (`__$$asciidoctorj_*$$__d/_r` and similar), i.e. configurations created while tasks executed
  (builders run after task execution, the task captures at graph time). Verdict: acceptable, no information lost.
  M2 gate (task up to date in all projects on the warm IP run, entry reused, no IP problems): **passed**.
- Re-measured task mode on gradle/gradle after the parallel request fix: miss 25.0 s (projects loaded 6.9 s,
  builders 22–23 s in the same session), hit 1.5 s, entry reused. But only 212 of 287 `ideProjectModel` tasks were
  scheduled: the per-project requests now run in parallel and `IdeModelTaskRequestBuilder` did an unsynchronized
  read-modify-write of the root build's `StartParameter.taskNames`, losing updates. Fixed with a lock. Note:
  `PrepareKotlinIdeaImportTaskModelBuilder` does the same unsynchronized update and IntelliJ requests it in
  parallel too; with many Kotlin projects it can lose prepare tasks the same way (potential IntelliJ bug, not
  verified in the IDE).
- **Intermittent whole-machine stalls.** Three warm misses took 66 s, 136 s and 113 s instead of 21–25 s
  (builder scenario 3 run 0; task mode gg-m2 run 0 and gg-m2c run 0), each with the extra ~100 s inside one
  phase. In the daemon logs of *idle* daemons the 10-second "periodic daemon health check" stopped for 103–112 s
  during exactly those windows, so every JVM on the machine stood still: an environment stall (host or sandbox),
  not Gradle or the tasks. A traced task-mode miss right after the 113 s run took 22.2 s. Mitigations: fewer JVMs
  (stopped the harness-build daemons and compiler workers, ~4.5 GB RSS), the harness now reports
  `stall_max_gap_s` per run (idle-daemon health-check gaps during the run) and `summarize.py` shows it, so
  affected runs can be excluded. Attribution of the first 136 s run to a sequential request loop was only
  partly right: the loop was real and is fixed, but the 100 s was this stall.
- **M3 first attempt failed on `GradleBuildScriptClasspathModel`**: its builder reads the parent project's model
  from `GradleBuildScriptClasspathCache` in the shared `ModelBuilderContext` (IntelliJ requests projects
  parent-first with one context per build); with one context per task capture the cache is empty and the
  builder throws "Gradle build script class-path model for root project 'gradle' wasn't collected", which made
  the input evaluation of all 254 root-build tasks fail and the CC store discard the entry ("254 problems were
  found storing the configuration cache", `cannot serialize ... DefaultListProperty`). Removed from the ported
  list: the task version should emit only the project's own classpath entries and let the consumer concatenate
  the parent chain (the IDE side is untouched in this experiment, so it stays a builder; cost 0.3 s summed).
  Lesson for the contract: a builder is only capturable as is if it reads no other project's model from the
  context; `javap` scan of the remaining mechanical builders shows no cache use (`ProjectExtensionsDataBuilderImpl`
  and `IntelliJProjectSettingsBuilder` only call `Project.getParent()`).
- Owner decision: continue with D6 (b), the comparison targets cache misses and incremental re-sync, measured
  against Gradle's opt-in per-model cache. Scenario 3 (no change) is reported as a tie.
- **M3 second attempt**: with the Java-serialization fallback (D12) 278 of 287 projects store fine; 9 projects
  (`:installation-beacon`, `:java-api-extractor`, `:java-compiler-plugin`, `:kotlin-dsl*`, `:problems`, `:test-kit`)
  fail with "Configuration with name 'apiStubElements' not found" from
  `ProjectExtensionsDataBuilderImpl.collectConfigurations` (iterates `configurations.names`, then `getByName`):
  these build scripts `configurations.remove(configurations.apiStubElements.get())`, and at graph time the removed
  name is still listed. IntelliJ's `ExtraModelBuilder` catches builder exceptions, reports and returns null; the
  capture now does the same (model absent for that project), and the request builder deletes stale bundle files
  before the tasks run so a missing file is unambiguous. Builder mode produces `GradleExtensions` for those 9
  projects (it runs after task execution), so the comparison will show them as missing in task mode: a timing
  difference to record per model, not a port bug.
- **M3 third attempt**: all three task-mode runs OK (miss 26.7 s, hit 2.9 s with entry reused, incremental 19.6 s),
  `ExternalProject` and `GradleSourceSetModel` 287/287 identical, `GradleTaskModel` differs only by the new
  `ideProjectModel` entry (accepted). But every ADDITIONAL-phase model was lost: four Ion entries per bundle
  (`GradleExtensions`, `ExternalTestsModel`, `AnnotationProcessingModel`, `RepositoryModels`, the
  `org.jetbrains.plugins.gradle.tooling.serialization.*` services) were 24-byte empty Ion documents and the consumer
  aborted the whole bundle on the first one. Cause: IntelliJ's Ion `SerializationService`s keep per-instance write
  state and are not thread-safe (the IDE serializes on a single-thread executor, `idea-tooling-model-converter`);
  I shared one `ToolingSerializer` while the configuration cache store evaluated the task inputs of different
  projects in parallel. Fixed with one serializer per capture; the consumer now fails per entry. Also: the
  stale-file deletion in the request builder made all 287 tasks re-execute on a cache hit (2.9 s vs 1.5 s) and is
  reverted. `GradleExtensions` fails to capture in 54 projects (removed `apiStubElements` configuration still listed
  in `configurations.names` at graph time), to be recorded as missing in task mode.
- **M3 fourth attempt diagnosis**: all runs OK (miss 21.8 s, hit 4.4 s with dumps, incremental 17.4 s) but only the
  first project's `ExternalProject`, `GradleExtensions`, `ExternalTestsModel`, `AnnotationProcessingModel` and
  `RepositoryModels` matched; the others arrived as `{objectID:1}`. Round-trip experiment (`harness` `RoundTrip2`):
  reading three different bundles with one `ToolingSerializer` returned the *same object instance* every time.
  IntelliJ's Ion `SerializationService` readers keep their object table across `read()` calls of one instance and
  resolve a repeated `objectID` to the earlier document's object; the writers likewise turn a second write of the
  same instance into a bare reference. So the consumer handed project 1's model to all 287 projects, and the
  holder's re-serialization of that one object collapsed to a reference. Fix: a fresh `ToolingSerializer` per
  read in the consumer (IntelliJ's IDE side also deserializes each model with its own context). The six models
  without an Ion serializer were "missing" in the dump only because the dump skipped non-byte values; the holder
  passes them through as objects, as in the IDE (D12). The dump now Java-serializes those for comparison.
- **M3 mechanical group, Ion-serialized models, on gradle/gradle (task mode, one JVM, 3 runs):** identical to the
  builder output for every project where the builder produces a model: `ExternalProject` 287/287,
  `GradleSourceSetModel` 287/287, `GradleExtensions` 278/278 (the builder itself yields nothing for the 9 projects
  that removed `apiStubElements`; the 54 capture warnings were those 9 projects × 6 input evaluations),
  `ExternalTestsModel` 287/287, `AnnotationProcessingModel` 256/256, `RepositoryModels` 287/287; `GradleTaskModel`
  differs in all 287 only by the new `ideProjectModel` entry. Six Java-serialized models still to be compared
  (dump tooling). Timings with dumps on: run 0 (fresh JVM, CC miss) 22.6 s with **all 287 model tasks up to date**
  (their outputs from the previous sync were still valid: task history survives a CC miss, which the whole-action
  cache does not); run 1 (hit) 1.8 s, 288 up to date; run 2 (comment edit in `:core`) 16.3 s, all model tasks up to
  date, the remaining builders (dependency, Kotlin, Kotlin DSL) 13 s. The miss is not faster than the builders'
  21–23 s yet because the same remaining builders run and the capture still computes the cheap models at
  configuration time; the hard group decides the outcome.
- **Finding (deferral of resolution is impossible under CC):** `ArtifactCollectionCodec.encode`
  (`platforms/core-configuration/dependency-management-serialization-codecs/.../ArtifactCollectionCodec.kt`) visits
  the artifacts when the entry is stored and writes `FixedFileArtifactSpec`s (only artifact transforms stay lazy),
  so an `ArtifactCollection` or a configuration-backed file collection held by a task is resolved at configuration
  cache store time, never at execution. There is no public or internal API to resolve a configuration at execution
  time without the `Project`. Consequence for `design/dependency-task.md`: the dependency model cannot do its work
  at execution; it is captured at configuration time like the cheap models (D8), so on a cache miss the task
  version resolves exactly as much as the builders do, and on an incremental edit its granularity equals IP's
  per-project reconfiguration, i.e. the opt-in per-model cache. The remaining structural difference between tasks
  and builders is therefore only against IP *without* the opt-in cache (scenario 4: builders rebuild every model,
  tasks recapture the reconfigured project only). Decision D13.
- **M3 mechanical group complete**: with the Java-serialized models now dumped and compared reflectively, every ported
  model is identical to the builder output for every project where the builder produces one (ExternalProject 287,
  GradleSourceSetModel 287, GradleExtensions 278, ExternalTestsModel 287, IntelliJ(Project)Settings 1,
  DependencyAccessorsModel 287, VersionCatalogsModel 287, AnnotationProcessingModel 256, JavaGradleManifestModel 287,
  RepositoryModels 287, ProjectRepositoriesModel 287, KaptGradleModel 287); GradleTaskModel differs only by the new
  task entry. `results/m3-ports/compare-mechanical.md`. Not ported: GradleBuildScriptClasspathModel (parent-model
  dependency). Hard group (dependency model with own-project index, KotlinGradleModel, ProjectDependencies) added
  to the capture list per D13; comparison running.
- **All-tasks floor (`--tasks-only`: no builder requested, only the model tasks; prototype with the hard group, one JVM):**
  miss 33.6 s (projects loaded 8.5 s, then 23.6 s of graph calculation + CC store with the captures + 287 task
  executions); hit 3.1 s (272 tasks re-executed because the serialized bytes differed between captures; with stable
  inputs ~2 s); edit of `subprojects/core/build.gradle.kts` 33.2 s, "Configuration cache entry stored": the entry
  was rebuilt in full, no "updated for N projects"; a comment appended to `gradle.properties` did not invalidate
  (3.4 s, entry reused): Gradle fingerprints the parsed properties. Two consequences: (1) for a tooling build action
  on stock 9.9.0-milestone-2 there is no incremental entry update without `isolated-projects.caching=tooling` (which
  is broken, LOG above), so the incremental scenario is a full re-store for both versions; (2) the capture at store
  time is slower than parallel model building (25 s vs 4 s for the dependency models); tracing to see whether the
  store evaluates the task inputs serially. Scenario 7 changed to append a property, not a comment.
- Hard group comparison: `KotlinGradleModel` 60/60 identical; `GradleSourceSetDependencyModel` and
  `ProjectDependencies` differ in 272 projects, overwhelmingly in Ion `objectID`/`id` numbers: IntelliJ's serializers
  number objects per writer across *all* documents of a sync and the IDE deserializes all models of a class in
  consumption order with one serializer (`GradleIdeaModelHolder.convertModelsWithType`), so a per-document
  comparison is meaningless for these two; `prototype/compare-ordered.sh` (ordered deserialization, reflective
  text) is being added. Task inputs are now a content fingerprint (`ModelText`) instead of the bytes.
- **Traced all-tasks miss (35.6 s)**: "Configure build" 7.8 s (parallel), **"Calculate task graph" 20.1 s**, "Store
  configuration cache state" 1.7 s, "Run tasks" 2.0 s (287 `ideProjectModel` executions in 2 s). Graph calculation
  materializes `@Nested` inputs to infer task dependencies, so the 287 captures (including dependency resolution)
  ran one after another on the single graph-calculation thread. Fix: the up-to-date input is a `@Input` content
  fingerprint string and the payload list is `@Internal`, evaluated at store time (parallel per project under IP);
  one memoized capture feeds both. Re-measuring.
- **Why the incremental edit re-stored everything**: in every mode the bootstrap message is "cannot be reused because
  file 'subprojects/core/build.gradle.kts' has changed" followed by "entry stored", i.e. `CheckedFingerprint.Invalid`
  at build scope, not the per-project `Update` path. For a model-building action the project-scoped fingerprint
  machinery (`ConfigurationCacheFingerprintWriter`, `cacheIntermediateModels`) is tied to
  `isolated-projects.caching`, so without that (broken) option a tooling build action never updates an entry
  incrementally on this Gradle version; a plain task run (`BuildLauncher`, `RunTasksRequirements`) does. Next
  transport to measure: `BuildLauncher.forTasks(...)` for the model tasks plus client-side bundle reading, which is
  also the "pure task run" the owner asked for (D14) and removes the daemon-side re-serialization.
- **Correction**: the two floor runs after the `@Internal` change (32.1 s / 5.6 s / 33.6 s, and the 34.8 s traced
  miss) measured the *old* prototype: its compilation had failed on a syntax slip and my build chain hid the error
  behind `grep … || true`. Fixed; `build-all.sh` now fails loudly and checks that the harness embeds the fresh
  prototype jar. The trace of that old build is still informative: under "Calculate task graph" (19.6 s) sit 3704
  "Resolve dependencies of …" operations (8.7 s) and 36925 task realizations (2.0 s), i.e. the captures ran there.
- **All-tasks floor, corrected build (`--tasks-only`, 17 models captured at store time, fingerprint input, one JVM):**
  miss **14.4 s** (projects loaded 6.7 s, graph + store with the captures + 287 executions + bundle read 7.3 s) vs
  builders 21.9 s; hit **1.9 s**, 287 tasks up to date (two captures of `:core` give identical canonical text and
  fingerprint for all models); edit of `subprojects/core/build.gradle.kts` **13.1 s** vs builders 14.9 s, entry
  re-stored in full (no per-project update for a model action, see above), and 272 tasks re-executed because the
  `ProjectDependencies` model carries per-build node `id`s that change on every recapture; fingerprints now
  ignore fields named `id`. Housekeeping: every client JVM creates a new CC entry of ~100 MB (whole-action result
  included); 81 entries had grown to 7.7 GB and were deleted (`.gradle/configuration-cache` of the benchmark
  worktree); free disk 17 GB.
- **Launcher transport (`--launcher`: GradleBuild model, BuildLauncher task run, bundles read in the client; one JVM,
  gradle/gradle):** miss **15.0 s** (GradleBuild 0.9 s, task run 13.0 s, reading and deserializing 132 MB of bundles
  in the client 0.9 s); hit 4.7 s then 3.1 s (first hit after a store re-hashes the 132 MB of outputs for the
  up-to-date check); edit of `subprojects/core/build.gradle.kts` **12.7 s**, "Calculating task graph as configuration
  cache cannot be reused because file 'subprojects/core/build.gradle.kts' has changed" + entry stored: a full
  re-store even for a plain task run, so on gradle/gradle a project build script is a *build-scoped* input (something
  in its settings/build logic reads project build scripts) and Isolated Projects' per-project update never applies.
  Appending a property to `gradle.properties` did not invalidate either (only properties that are read count);
  scenario 7 now appends a comment to `settings.gradle.kts`. Fixture run in flight to show the per-project update on
  a simple build. All 17 models arrive from the bundles (287/287/287/… as in the comparison).
- **Why no per-project entry update after a build-script edit, in any transport (fixture included):** the change is
  reported as a *build-scoped* invalidation ("cannot be reused because file 'app/build.gradle.kts' has changed" →
  `CheckedFingerprint.Invalid`, full re-store). Project evaluation itself is wrapped in the project-scoped sink
  (`ConfigurationCacheAwareProjectEvaluator`), but resolved script files are also reported through
  `ScriptFileResolvedListener` → `ConfigurationCacheFingerprintWriter.onScriptFileResolved` → `fileObserved`, which
  writes to `sink()` = the build-scoped sink when no project is being evaluated on that thread (script file
  resolution happens when project descriptors are created, at settings time). So every project build script is a
  build-scoped input and the `Update` path (`CheckedFingerprint.Valid` with `invalidProjects`) is only reached when
  the build-scoped check passes, e.g. in scenario 6 where `isolated-projects.caching=tooling` changes what is
  recorded. Verified: the call chain and the messages; not verified: which caller resolves the script file at build
  scope (`DefaultScriptFileResolverListeners` is the broadcast). Consequence for the report: on 9.9.0-milestone-2 an
  IDE sync after a build-script edit reconfigures everything in every variant; the task variants still win there
  because the capture runs in parallel at store time and the models are not re-sent (13.1 s / 12.7 s vs 14.9 s), and
  the structural win on misses stands (14.4 s / 15.0 s vs 21.9 s).
- **M4 comparison matrix** (`results/m4-summary.md`, `results/m4-NOTES.md`; raw runs in `results/m4-{builders,tasks,tasks-only,launcher}/`;
  96 runs, all OK, no stalls). Medians: warm plain 21.4 / 22.4 / **14.8** / 16.6 s; warm hit 1.65 / 1.75 / 1.68 / 3.3 s;
  edit 14.1 / 16.5 / **10.2** / 11.4 s; settings-script invalidation 16.6 / 15.9 / **11.5** / 11.6 s; cold 51.6 / 45.6 /
  30.0 / 29.2 s (builders / tasks / tasks-only / launcher). Warm miss, single runs: run 0 of scenario 3 (same JVM, entry
  stored) 25.1 / 20.0 / **11.5** / 13.5 s; traced run in a fresh JVM 19.2 / 20.0 / 14.1 s, and **2.9 s for the launcher,
  which was a cache hit**: a plain task run's configuration cache key does not include the Tooling API client
  classloader UUID, so the launcher transport reuses the entry across client JVMs (IDE restarts), where every build
  action variant misses (`results/m4-launcher/3-trace.txt`: "Reusing configuration cache").
  Gate D15 (≥ 30 % on scenario 2, warm miss, scenario 4): `tasks` not met (+5 %, +4 %, +17 %: the action still waits
  for the serial `KotlinDslScriptsModel` builder, 7.3 s, and the task run comes on top); `tasks-only` borderline
  (−30 %, −26 % traced / −54 % run 0, −28 %; −31 % on scenario 7); `launcher` −22 % / hit / −19 %, −30 % on scenario 7.
  Reading: the work saving at the floor is a consistent 26–31 % because configuration (8.7–9.3 s warm, 4.5 s after an
  edit) is unchanged and resolution cannot move past the store (D13); the structural wins are the cross-JVM hit
  of a plain task run and not shipping 113–132 MB of models through the action. Cold builders (51.6 s) is far above
  M0 (34.7 s): the chain left 17 GB of trace logs and filled the disk to 100 %; cold numbers are not comparable
  across sessions (`results/m4-NOTES.md`). Trace logs deleted afterwards.
- **M3 hard group closed, D16** (`results/m3-ports/compare-hard.md`). Ordered comparison (fresh dumps, `order.txt`,
  node ids ignored): before the fix `GradleSourceSetDependencyModel` 23/264 and `ProjectDependencies` 265/7; two
  builder dumps are identical (287/287, 272/272), so the differences were real. Cause, read from IntelliJ's
  `GradleSourceSetDependencyModelProvider` and `GradleSourceSetArtifactIndex`: the IDE fills one shared index with
  every project's jar → source set and classes dir → jar entries before it builds any dependency model, and the
  resolver folds a project dependency's classes-dir artifacts back into the jar entry through it; a per-capture
  context knows only its own project, so compile and runtime entries never merge (COMPILE → PROVIDED + RUNTIME),
  sources are empty, runtime output-dir dependencies are missing. Fix: one context per build invocation in a shared
  build service of the root build (`SharedBuilderContext`), the index request run from `gradle.lifecycle.afterProject`.
  First attempt with a static map keyed by `Gradle` failed silently: under IP `project.getGradle()` is a
  per-project wrapper, so every project got its own context (diagnosed with the index size per project on the
  fixture). Result: `GradleSourceSetDependencyModel` **287/287 identical**; `ProjectDependencies` 266/6, the 6 being
  sibling-order permutations of the same nodes (sorted lines identical). A first same-method comparison across
  midnight differed in every project by gradle/gradle's date-stamped version string only. Side effect:
  `GradleSourceSetModel` 285/2 (empty `configurationArtifacts` entries of task-realization-time configurations in
  `:docs` and `:internal-build-reports`). No IP or CC problems; entry stored.
- **Race in the first D16 build, found by the timing re-check** (`results/m4b-tasks-only/`): in 3 of 6 timed runs
  240 `ideProjectModel` tasks re-executed because their dependency-model capture had failed with
  `TaskCreationException: Could not create task ':internal-distribution-testing:copyTestedVersionsInfo'` (and the
  `ExternalTestsModel` of that project with it). Reading another project's `SourceSetOutput.getDirs()` through the
  shared index realizes that project's `output.dir(taskProvider)` task; 240 captures did so concurrently without the
  owning project's lock. This is what IntelliJ's `GradleTaskWarmUpRequest` prevents in the builder world. Fix: each
  project's `afterProject` hook evaluates its source sets' `allJava` source dirs, classes dirs, resources dir and
  output dirs before the index entry is published (`ModelCapture.realizeSourceSetState`). Re-measured
  (`results/m4c-tasks-only/`, `--tasks-only`, final prototype): warm plain **14.6 s** (M4 14.8), hit **1.59 s** (1.68),
  edit **9.4 s** (10.2); all 287 model tasks up to date on repeat in every run, 9 known `apiStubElements` failures
  per run and nothing else. Fidelity with the final jar: `GradleSourceSetDependencyModel` 287/287,
  `ProjectDependencies` 263/9 (sibling-order permutations), two task-mode runs agree on every model but the
  per-run node ids of `ProjectDependencies` (`results/m3-ports/compare-hard.md`).

## 2026-10-16

- **D17 chain stalled for four hours: a deadlock in the Kotlin DSL script capture, not a slow build.** The daemon sat
  at 0 % CPU from 00:59 (`results/m3-ports/d17-stage1-deadlock-excerpt.txt`, the blocked threads of the full dump). Gradle's
  `Stage1BlocksAccessorClassPathGenerator.stage1BlocksAccessorClassPath` is a build-wide `lazy` whose initializer
  takes the root project's model lock; `kotlinBuildScriptModelFor` reads it for every script. At configuration cache
  store time the task inputs of all projects are evaluated in parallel, each thread holding its own project's lock:
  one thread entered the lazy's monitor and waited for the root lock, seven others (the root project's capture among
  them) held their project locks and waited for the monitor. Gradle's lock coordination cannot see a JVM monitor, so
  nothing gave way. The fixture never showed it (3 projects, little contention) and the builder world never can: the
  `KotlinDslScriptsModel` builder runs on one thread under the root lock. Fix: the root project's `afterProject`
  hook calls `prepareForParallelAccess()` (present in 9.9.0-milestone-2 and called by Gradle's own
  `IsolatedProjectsSafeKotlinDslScriptsModelBuilder` for the same reason), so the parallel captures only read the
  initialized value. The hook runs before any capture: the root project is evaluated before its children and
  captures run after configuration. Chain restarted 04:58; the task dump then took 45 s. Lesson for the harness: a
  dump or scenario run needs a timeout (added to the chain: 30 min).
- **IntelliJ's `IdeaProject` Ion reader rejects three of the four builds' documents** (builders dump, text
  conversion): `Invalid type. Required BOOL but found null` for `build-logic`, `build-logic-commons` and
  `build-logic-settings`; the root build's document (255 modules) reads fine. The only field that is null in those
  documents and never in the root one is `gradleModuleVersion` of 52 `IdeaSingleEntryLibraryDependency` entries
  (file dependencies on plugin class dirs, e.g. `build-logic/binary-compatibility/build/groovy-dsl-plugins/output/plugin-classes`).
  Unverified in the IDE: the resolver may read `IdeaProject` through a different path, or never deserializes the
  Ion document of an included build. Worth a look on the IntelliJ side; recorded per build as
  `gradle-owned/ideaModule/<build>/_error.txt` in the dump, the comparison covers the root build.
- **D17 on gradle/gradle, fidelity (final prototype):** `results/m3-ports/compare-gradle-owned.md`. Idea modules
  **255/255 identical** on the root build once the payload reads the `idea` extension where the project applied the
  plugin (gradle/gradle does via `gradlebuild.ide`; its `plugins.withType<IdeaPlugin>` test-source customizations
  and the composite-deduplicated module name come along). The first dump, mirroring only the plugin's defaults,
  had 242/255 modules differing in test source dirs; IntelliJ's resolver reads from `IdeaModule` only the content
  root and the exclude dirs for JVM projects (module-per-source-set), so that difference would not have reached the
  IDE, but reading the extension is both cheaper and exact. Kotlin DSL scripts: all 4 settings scripts and 89
  precompiled script plugins identical; the 287 build scripts differ only by the accessors jar hash (one more
  accessor in the task world). IntelliJ's own `IdeaProject` Ion reader fails on the three included builds, so
  their 36 modules are compared only against themselves (task side present, builder side unreadable).
- **D17 timings (`results/m4d-*`, `results/m4e-*`): the task variants got slower than with the serial builders, and
  the cause is the network.** With the two Gradle-owned captures in every project: `tasks` 23.7 / 2.5 / 22.3 s,
  `tasks-only` 26.1 (24.9–46.6) / 6.0 (3.6–8.7) / 20.2 s, `launcher` 32.2 / 4.4 / 23.7 s (warm plain / hit / edit;
  `m4c` floor without them: 14.6 / 1.6 / 9.4 s). Attribution with per-model capture timing (`-PideModelTasks.timing=1`,
  two warm plain runs with every task re-run): `KotlinDslScriptPayload` **88–99 s** of capture time summed over
  287 projects, slowest captures 9.4–10.6 s (the root project and the first projects to start); next is
  `GradleSourceSetDependencyModel` with 26–33 s, max 0.5 s; everything else under 7 s summed. Thread samples
  during the captures (12 `jstack`s, 5 s apart): 16 of 18 script-capture samples **blocked on
  `SourceDistributionResolutionCache.computeIfAbsent`**, the one running sample inside an HTTPS read
  (`SSLConnectionSocketFactory`, `SessionInputBufferImpl.streamRead`). The script model's source path includes the
  Gradle distribution sources; `SourceDistributionResolver` looks them up on services.gradle.org once per build
  invocation (build-tree-scoped cache, lock held during the lookup; gradle/gradle's snapshot version has no source
  zip there, so it ends in "Could not resolve Gradle distribution sources" every time). On the owner's connection
  today a request to services.gradle.org took 8.5 s (`curl`), Maven Central 1.8 s. So every sync pays ~10 s for a
  lookup that fails, in both worlds: it is most of the 7.3 s the serial `KotlinDslScriptsModel` builder cost in M4.
  In the task world it is worse: all 8 capture threads queue behind the lock, the configuration cache store stalls
  for the duration, and the run-to-run spread follows the connection. Control: the same run with `--offline`
  brought the script capture down to **29 s summed, max 0.84 s** (its other numbers are not comparable: the forced
  re-run recompiled build-logic). Fix in the prototype: the root project's `afterProject` hook resolves the
  distribution sources once (next to the stage-1 accessors warm-up), so the captures hit the cache; the sync still
  pays the lookup once, serially, exactly like the builder. The remaining script-capture cost is ~29 s of CPU over
  8 workers, ~4 s wall at store time. The cache hits (1.6 s → 2.5–6 s) are not network: a hit runs no capture;
  the configuration cache directory grew to 389 MB with the script payloads (41 MB of class and source paths across
  287 projects in the dump), which is the next thing to trim (the path lists repeat the same ~300 entries per
  project). Re-timing with the warm-up: `results/m4f-*`.
- **For Gradle (owner):** the distribution-sources lookup result should be cached persistently per Gradle version
  (positive and negative) in the Gradle user home; today every IDE sync of a build without a source zip pays a
  network round trip, and a slow connection turns the Kotlin DSL script model into the slowest part of the sync.
- **Moving the lookup does not help; removing it does (D18).** `results/m4f-tasks/` (warm-up of the distribution
  sources in the root project's `afterProject`): the script capture itself dropped to 10 s summed, max 0.83 s, but
  the sync got slower, 32.7 s warm plain with **25.4 s before `projectsLoaded`** (9 s in `m4e`): the lookup now ran
  serially during configuration of the first build and the connection had degraded to ~16 s per request. Round
  aborted after the `tasks` variant. Fix: an (empty) `src` directory in the benchmark distribution, which Gradle
  checks before it resolves the distribution sources (`SourcePathProvider.sourceRootsOf`), as the `-all`
  distribution has. Network-free round `results/m4g-*` with all four variants, builders included, since the M4
  builder numbers contain the lookup too. Side finding of the attribution runs: `--rerun-tasks` recompiles
  build-logic in a Kotlin compile daemon that ends at its 3.3 GB heap limit (old generation 99.97 %, 8 full
  collections), so the time before `projectsLoaded` grew 65 → 35 → 295 → 380 s over four such runs; attribution
  runs must force only the model tasks (an edit) and not the build-logic compilation. Daemon killed; superseded
  dumps deleted (16 GB free).
- **D18 round `m4g`, builders network-free: 21.9 / 1.47 / 12.5 s** (warm plain / hit / edit; M4 with the lookup:
  21.4 / 1.65 / 14.1 s); 0 distribution-sources warnings in 15 runs; entries ~50 MB. The `tasks` variant still made
  the lookup in 12 of 15 runs: the prototype's own warm-up called `SourceDistributionResolver.sourceDirs()` directly,
  which does not check the installation's `src` directory (that check is in `SourcePathProvider.sourceRootsOf`, the
  caller). The warm-up now returns when `<gradleHome>/src` exists. Round stopped after `tasks`
  (`results/m4g-NOTES.md`); its entry size (155 MB vs 50 MB for builders) and hit (3.7 s vs 1.47 s) motivate D19
  (deflated payloads), measured in `m4h-*`.
- **D19 round `m4h`** (`results/m4h-NOTES.md`; network-free, deflated payloads): `tasks-only` **15.3 / 2.16 / 11.2 s**,
  `tasks` 17.9 / 7.7 / 13.0 s, `launcher` 19.6 / 5.3 / 16.8 s against network-free builders 21.9 / 1.47 / 12.5 s
  (warm plain / hit / edit). Deflating cut the task-world entry from 155 to 85 MB and the `tasks-only` hit from
  3.7–6 s back to 2.16 s (tight, 2.14–2.65). The `tasks` hit stays erratic (3.0–8.6 s): sampled during the round, the
  daemon's old generation was at 85–97 % of 3.3 GB with a full GC every ~45 s and 2.77 GB used during the launcher
  runs. Fixture after D19: payloads round-trip through bundle and dump (idea 5/5, scripts as before); a 61 KB
  fixture bundle deflates to 25 KB. Script payload: 151,501 path entries over 809 distinct paths explain the ratio.
- **Daemon live set, builders vs tasks-only** (`results/heap-NOTES.md`): after a forced full GC the builders' daemon
  holds **2.34 GB** (5.8 M `LinkedHashMap` entries, 2.5 M `DefaultResolvedDependencyResult`, 227 MB of byte arrays),
  the task world's **1.24 GB** (196 MB of deflated payloads on top of Gradle's own state); both ran at 93–99.9 % old
  generation before the forced GC, and in this series the builders' hits were as erratic as the task world's
  (3.6 s, 2.0–8.5 vs 3.2 s, 2.8–7.2). Correction to the reading above: the erratic hits are full-GC pauses in
  gradle/gradle's 3.3 GB daemon, in both worlds, not a task-world property. Hit measurements need a larger daemon
  heap or GC time subtracted.
- **Hits with an 8 GB daemon heap and a traced hit** (`results/heapx-NOTES.md`): builders 2.34 s (2.18–2.78),
  tasks-only 5.0 (2.67–5.11), tasks 3.3 (3.09–5.99), **no full GC in any series**, so the GC explanation above is
  withdrawn: the spread is elsewhere. Build operation trace of two consecutive `tasks-only` hits in one client JVM
  (3.0 s and 4.0 s): "Check configuration cache fingerprint" 0.61 s in both, "Load configuration cache state"
  **2.03 s vs 3.07 s** (own time 1.75 vs 2.86 s; the per-project "Loading configuration for X" children are
  4–26 ms each), everything after the load 0.3 s. The task world's hit is the load of its 85 MB entry, and that
  load's own time varies by a second between identical runs; the builders' 50 MB entry loads in well under a second
  (1.47 s hit). Next: GC log on the daemon during hits and the entry's file composition.
- **Hits, consolidated** (`results/hit-NOTES.md`): entry composition builders 114 MB (113.8 MB of streamed models,
  replayed on a hit), tasks-only 186 MB (141 MB re-streamed models + 45 MB task state), launcher 52 MB (task state
  only); GC log of a fresh-daemon launcher series, hits only: 8 pauses, 0.12 s in total, pauses over 50 ms: none; tasks-only store + 3 hits: 2.0 s of GC in
  total. First hits after a daemon start are slow (tasks-only 4.5 → 1.8 → 1.3 s, load own time 3.1 → 0.58 → 0.24 s);
  one launcher series with 13.6–22.9 s loads on reused entries was not reproduced (4.5–5.2 s fresh, 2.7–3.0 s warm).
  Parallel client-side bundle reading in the launcher: 0.23 s for 287 bundles (2–3 s before). Consequence for M5:
  the IDE reads bundles itself, no re-streaming through the action.
- **Headless IDE works (D20).** `idea.sh warmup --project-dir=<fixture copy>` with headless AWT, scratchpad config
  and system dirs, `GRADLE_USER_HOME=~/.gradle-wbs`, `JAVA_HOME` = JDK 25, `.idea/gradle.xml` with a LOCAL
  distribution (the fixture's wrapper would download): "Gradle resolve stage started" 08:51:19.37, "BUILD SUCCESSFUL",
  "Configuration cache entry stored", "resolve stage finished" 08:51:25.90 (6.5 s), data import 0.35 s, indexing
  5 s, "IDE Warm-up finished, elapsed 17 s 63 ms". First attempt failed only on syntax (`--project-dir=` is
  required). Started the same for gradle/gradle (`.idea/gradle.xml` added to the worktree, wrapper distribution from
  `~/.gradle-wbs`): IDE baseline for M5.
- **IDE baseline, gradle/gradle, headless, stock plugin** (`results/m5-ide/baseline-NOTES.md`): first import with the
  cache directory deleted: Gradle resolve stage 56.7 s (build successful at 50.2 s, entry stored 50.6 s), data import
  24.5 s, final tasks 2.1 s, import task 1 m 23.7 s; indexing ran concurrently (warm-up mode). Patch point for the
  task transport found: `GradleProjectResolver.doResolveProjectInfo` collects `getModelProviders()` of every resolver
  extension into `GradleModelFetchAction.addProjectImportModelProviders`; a resolver extension can add the prototype
  jar to the init script classpath (`getToolingExtensionsClasses`) and `--init-script` plus properties
  (`getExtraCommandLineArgs`), so the transport side needs only a small plugin; dropping the built-in providers needs
  one patched class (`GradleModelFetchAction.addProjectImportModelProviders`, a filter by class name, as the harness
  does in `IdeProviders.useTaskTransport`).
- **IDE restart is a miss, confirmed in the real IDE** (`results/m5-ide/baseline-NOTES.md`, run 3: fresh IDE system
  dir, entry from run 1 on disk): resolve stage 55.6 s, "Configuration cache entry stored", data import 24.1 s,
  1 m 22.7 s in total, the same as the first import. Run 2 (same system dir) did not sync at all: the warm-up
  configurator skips an already imported project, so IDE runs use a fresh system dir. Work paused here at the
  owner's request; `ide-plugin/` (D21) written, not built.
- **Task transport in the real IDE, fixture (D21):** patched IDE copy (plugin + patched `GradleModelFetchAction`,
  17 built-in providers dropped, Gradle-owned builders kept), headless warm-up: 4 model tasks, 4 bundles, import
  7.8 s; **all 10 `.idea/modules/*.iml` files identical to the stock plugin's** (content roots, source folders, module
  and library dependencies), `misc.xml` differs by a `default="true"` attribute. Two wrong turns first: (1) the model
  tasks init script needs the IDE's full tooling-extension classpath (Trove missing: IntelliJ's serializer services
  failed to instantiate, "Error while saving task graph"); it now takes the IDE's constant plus every resolver
  extension's tooling classes, as the IDE's main init script does. (2) Dropping `BuildModel(IdeaProject)` empties
  the IDE's module list (the resolver iterates `IdeaProject.getModules()`): gradle/gradle imported in 43 s with a
  0.3 s data import and no modules, and 574 captures failed for want of the Kotlin tooling jar (invalid run,
  `ide-warmup-gg-patched1-invalid.out`). The Gradle-owned builders stay until the resolver assembles them from
  the payloads (`DropList --keep-gradle-owned`).
- **M5 first numbers and fidelity in the real IDE** (`results/m5-ide/baseline-NOTES.md`): gradle/gradle, fresh IDE
  state, empty cache, task transport for the 17 IntelliJ models: resolve stage **47.2 s** vs stock 56.7 / 51.6 s
  (two runs), Gradle build **41.1 s** vs 50.2 / 46.3 s, import 1 m 11.5 s vs 1 m 23.7 / 1 m 18.8 s. Fidelity: the IDE's
  external storage after the import, **1370 files identical** between stock and task transport, 0 different.
- **Plain model-task run in the IDE, fixture (M5 round 2a):** `-Dwbs.transport.run=plain` makes the plugin's
  `preImportCheck` fetch `GradleBuild` and run every `ideProjectModel` task as a plain `BuildLauncher` run on a
  second connection before the build action; the file providers then read the bundles and the request provider
  schedules nothing. Three pitfalls: the IDE's path-mapper init script must be prepended (the classpath snippet calls
  `mapPath()`), `GradleExecutionHelper.prepareForExecution` applies the sync's empty task list to any operation
  ("Cannot locate matching tasks for an empty path", settings applied by hand instead), and a blank entry in the
  settings' arguments is read as a task path. Result on the fixture, two IDE processes: run A stored a task-graph
  entry and wrote the bundles; run B (restart) **reused the entry for the `GradleBuild` request and for the task
  run** (daemon log: "Configuration cache entry reused" twice), only the build action stored again; project files
  identical to stock (10/10). gradle/gradle pair running.
- **Plain model-task run in the IDE, gradle/gradle (M5 round 2a)**: restart (fresh IDE state, entries present):
  plain run 10.5 s with `GradleBuild` and the task run both reused, action 29.4 s (miss), resolve 46.4 s, import
  1 m 10.7 s (stock restart 51.8–55.6 s / 1 m 19–23 s). First import: plain run 36.9 s + action 15.7 s = 59.9 s resolve.
  One plain run took 579 s (daemon-side) before the run's output was consumed; not reproduced since
  (`results/m5-ide/baseline-NOTES.md`). Next: port the remaining models so the action can go.
- **M5 round 2b, fixture: the Gradle-owned builders are gone.** The file provider assembles each build's
  `IdeaProject` from the `IdeaModulePayload`s (`IdeaProjectFromPayloads`: TAPI interfaces implemented on plain
  records, language levels = max over modules, module settings only where they differ, Gradle project tree
  without tasks, empty dependencies) and the `KotlinDslScriptsModel` from the `KotlinDslScriptPayload`s, and hands
  them to the IDE under those classes (build model, and project model of the build's root project as the Kotlin
  provider does). IntelliJ's own Ion services serialize them (offline check: 6.5 KB `IdeaProject`, reads back as
  `InternalIdeaProject` with 4 modules). With all 26 built-in providers dropped, the fixture import writes
  `.idea/modules/*.iml` and `misc.xml` **identical to the stock plugin's** (11/11). A first comparison showed
  directory-prefixed module names: stale `.idea/modules.xml` entries from earlier imports made the IDE
  de-duplicate; fixture imports now start from an `.idea` with only `gradle.xml`.
- **M5 round 2b on gradle/gradle** (`results/m5-ide/baseline-NOTES.md`): all 26 built-in providers dropped, the
  Gradle-owned models assembled from the payloads, plain pre-run. Restart: plain run 11.8 s (287 tasks up-to-date,
  entry reused), action 33.6 s (miss: the first configuration in a fresh daemon), resolve 53.3 s, import 1 m 20 s.
  First import: 62.6 + 13.6 s, resolve 84.4 s. Conclusion: the action must go entirely for the restart gain; with it
  present the plain pre-run only adds a second invocation. The session ended during run B; its stages were read from
  the IDE log.
- **2b fidelity on gradle/gradle, first result 1178/1370 identical, 192 module files different**: libraries that are
  project-level without sources in the stock import came out module-level with sources attached. Not the assembled
  `IdeaProject` (module order identical, no dependency use in source-set mode): the bundles produced by the plain run
  carry library sources (163 of 164 libraries of `:architecture-test`), those produced inside the action do not
  (same for the builders' models, `source: null` throughout). The IDE prepends `ijIdeaPluginConfigurator.gradle` to
  its action when source download is off; it sets the idea plugin's `downloadSources`/`downloadJavadoc` to false,
  which IntelliJ's dependency resolver reads through its download policy. The plain run lacked that script and so
  resolved every sources jar in the cache (extra work too). Fixed: the plain run adds the same script when
  `GradleExecutionSettings.isDownloadSources()` is false. Rerun of the storage diff in progress.
- **Owner (2026-10-16): the target is one Gradle invocation per sync.** The two-invocation setup of round 2b (plain run
  plus action) was a stepping stone; round 2c removes the action.
- **2b fidelity, second and third pass.** With the configurator init script in the plain run: 1279/1370 identical, 91
  different, all "sources attached to a module-level library" in 2b where stock has none, in 22 integTest/test/
  testFixtures modules and a few main/jmh ones; plain-run store down from 62.6 to 41.6 s (no sources resolution).
  Cause of the rest: IntelliJ's dependency resolver reads the build-level download policy from the shared builder
  context; a parallel capture that runs before the owning project's policy capture falls back to the default
  ("Gradle dependency download policy ... wasn't collected", seen since the first IDE runs). Fix: the policy is
  collected in every project's `afterProject` hook, before any capture. Also ported (round 2c, mechanical): the six
  Kotlin compiler-plugin models, `KotlinMPPGradleModel` and `GradleBuildScriptClasspathModel`; IntelliJ builders
  exist for all (fixture: no missing-builder warnings), drop list 35 providers. Rerun of the storage diff in progress.
- **The last 91 differences were the Gradle cache, not the transport.** IntelliJ's resolver attaches sources to any
  library whose model has none by looking for a sources jar next to the binary in the Gradle cache
  (`attachSourcesAndJavadocFromGradleCacheIfNeeded`, both worlds). The first round-2b plain run, before it got the
  idea-plugin configurator script, resolved sources for every library and left **266 sources jars** in
  `~/.gradle-wbs/caches` after the stock storage snapshot had been taken; every later import, stock or task, attaches
  them. The fresh plain-run bundles carry no sources at all (0 of 164 for `:architecture-test`), the policy
  collection at `afterProject` was not the cause (kept: cheap and correct), and a stock import of today is being
  diffed against the 2c storage. Lesson for the measurement notes: the dependency cache is an input of the IDE's
  project model; fidelity diffs must pair runs taken against the same cache content.
- **Round 2c fidelity on gradle/gradle: identical.** Stock import of today vs the 2c import (35 built-in providers
  dropped, `IdeaProject` and `KotlinDslScriptsModel` assembled from payloads, the nine small models ported): **1370 of
  1370 storage files identical**; the old stock snapshot differs from today's stock in exactly the 91 files the
  cache's new sources jars touch.
- **M5 round 2c, one Gradle invocation, works on the fixture (D23).** With `-Dwbs.transport.run=only` the patched
  `GradleProjectResolver` skips IntelliJ's build action; the plugin's plain task run produces the bundles and
  `buildState()` turns them into the holder's `GradleModelHolderState` (light builds from `GradleBuild`, every payload
  under its model id, `IdeaProject` and `KotlinDslScriptsModel` assembled per build). Three things had to be right:
  the model id's class part is `SimpleName + name.hashCode()`; Java-serialized Kotlin models need the plugin to
  depend (optionally) on the Kotlin plugin so their classes load; and every Ion payload must be read with a fresh
  IntelliJ serializer, because the holder's shared reader expects object ids to continue across documents while each
  bundle was written by its own writer (the first wrong read made every module resolve to the root's
  `ExternalProject`, "Attempt to add module with already existing id"). Fixture: no action, plain run 285 ms (hit),
  import 2.5 s, 11/11 project files identical to stock. gradle/gradle pair and storage diff running.
- **M5 round 2c on gradle/gradle: one invocation, identical project, restart 13.5 s.** First import: plain run
  36.6 s + state 3 s, resolve 41.0 s, import 1 m 4 s (stock 52–57 s / 1 m 19–24 s). Restart: plain run **7.7 s (hit)**,
  resolve **13.5 s**, import **41.3 s** (stock 52–56 s / 1 m 19–23 s). Storage after the restart run vs today's stock
  import: 1370/1370 identical. Cache entry 56 MB (stock 114 MB). 6321 payloads became 5755 models, the stock sync's
  count.
- **IDE data import breakdown (owner's question).** From the warm-up log timestamps: library dependency data 12.5–12.8 s,
  project repository data 6.4–7.5 s, final tasks 2.6 s, all others under 1.5 s; identical in both worlds; indexing
  (17.5–18.9 s) ran concurrently and inflates them. Improvement path enabled by tasks: skip unchanged projects in the
  import using the bundles' fingerprints (`results/m5-ide/baseline-NOTES.md`).
- **Streaming bundle reads (owner: "more in parallel, as soon as a task is complete").** The plain run's task-finish
  events now trigger the read and deserialization of that project's bundle while the run continues; `buildState()` only
  waits for reads still in flight. Fixture: 4 of 4 bundles read during the run, project files identical. gradle/gradle
  pair running to measure the gain against 41.0 s (store) and 13.5 s (restart) resolve stages.
- **Streaming reads measured on gradle/gradle**: store 37.6 s resolve / 58.6 s import (−3.4 / −5.6 s), restart no gain
  (all 287 up-to-date tasks complete in a burst at the run's end; state still 4.3 s after it), storage identical.
- **Owner's check: does anything still configure the build in the single-invocation mode?** Daemon log of the restart
  run: exactly two builds, the `GradleBuild` model request ("Configuration cache entry reused") and the task run
  ("Reusing configuration cache", 10 s, entry reused); no build action, no "Creating tooling model", no task graph
  calculation, so no project configuration at all after a restart. On a first import the task run configures once
  (store); the `GradleBuild` request is built from the loaded project hierarchy without configuring projects
  (`BuildEnvironment` is settings-only).
- **Capture parallelism (owner: "can we build more models in parallel?")**: timeline of all 6321 captures in one
  store run on gradle/gradle (`-PideModelTasks.timing=1` with start time and thread): the capture phase spans 8.6 s
  of wall time for 67.7 s of summed work, average parallelism 7.86 on 8 cores, all eight worker threads 6.0–8.0 s
  busy, longest per-project chain 1.5 s (`:dependency-management`), in-flight captures saturate from the first
  second. `GradleSourceSetDependencyModel` is 59 % of the work (39.7 s), then source set 6.5, task model 5.8,
  ProjectDependencies 4.7, buildscript classpath 3.4, Kotlin DSL script 2.9 s. No headroom at this core count: the
  phase is CPU-bound on Gradle's worker pool and would scale with cores; within a project the models must stay
  sequential (they mutate project state under its lock). This run also showed 48 s before `projectsLoaded` after a
  prototype rebuild (308 tasks up to date, no compilation): the changed init-script classpath jar most likely
  invalidated the Kotlin DSL script compilation of the build scripts; worth verifying, it would mean a slow first
  sync after every IDE or plugin update, for the stock sync as well.
- **IDE import hot spots** (167 jstack samples of the IDE during an import): library dependency step =
  `LibraryDependencyDataService.findLibraryOrderEntry` / `LibraryOrderEntryBridge.getLibrary` and workspace-storage
  index updates; repository step = `ProjectModifiableLibraryTableBridgeImpl.librariesArrayValue` recomputed per
  operation over 577 project libraries (quadratic smell) while the library table commits. Platform-side; details in
  `results/m5-ide/baseline-NOTES.md`. The sampled run was a store because the prototype rebuild invalidated the plain
  run's entry, unlike the earlier observation after another rebuild; the init-script-classpath invalidation question
  stays open.

- **JFR profile of a restart import (owner: test a fix for the two IDE hot spots).** `-XX:StartFlightRecording`
  on the headless IDE, 10331 execution samples, 540 on the importing thread inside the data import stage (26.5 s).
  Grouped by data-service frame: `KotlinGradleLibraryDataService.postProcess` 153 (28 %), all of it
  `IdeModifiableModelsProvider.findIdeLibrary` → `getLibraries()` → `ProjectModifiableLibraryTableBridgeImpl.librariesArrayValue`
  rebuilt from the entity storage: the loop calls `setKind` on each library's modifiable model, which invalidates the
  table's cached array before the next lookup (O(n²) over 577 project libraries; this is the "project repository
  data" time: the step label lags behind the Kotlin post-processing); `LibraryDependencyDataService.findLibraryOrderEntry`
  149 (28 %), `LibraryOrderEntryBridge.getLibrary` resolving each entry's library through the storage
  (`MutableEntityStorageImpl.resolve`, `SymbolicIdInternalIndex`, `IdeModifiableModelsProviderImpl$1.getLibrary` →
  `getLibraryByName`); then `ProjectDataManagerImpl.commit` 67 (workspace model commit), `setLibraryScope` 45
  (`ModuleEntityData.updateLinksIndex`), `importMissingModuleLibraryOrderEntry` 37, `LibraryDataService.registerPaths`
  39. Sources fetched from the 253 branch into the scratchpad (`ij-src/`); `isRelated()` is name equality.
- **D24 patch, restart imports (fresh system dir, cache kept), two runs + one profiled run:** data import stage
  **23.0 / 18.6 / 21.9 s** (unpatched 26.5–27 s), whole import **37.6 / 38.4 / 38.5 s** (41.3–44.9 s), storage after
  run 2 vs stock **1370/1370 identical**. Per step: project repository data **2.7–3.3 s** (6.4–7.5 s before; the
  `findIdeLibrary` fix), library dependency data **11.2–13.5 s, unchanged**: the profile of the patched run still shows
  `findLibraryOrderEntry` → `getLibrary()` (126 samples), so the name filter does not skip the entries it meets.
  Counters in the patched method explain it: **103,090 lookups, 21.6 million entries scanned, 21.5 million
  `getLibrary()` resolutions**; the searched library is unnamed in 101,954 lookups (module libraries for file
  dependencies, `importMissingModuleLibraryOrderEntry` with an empty internal name) and 7.5 million of the entries are
  unnamed too, so name comparison cannot separate them. The stock code resolves every entry of the module (210 on
  average) for every module library it adds.
- **D24b: `findLibraryOrderEntry` by library id** (`LibraryBridge.getLibraryId()` against each
  `LibraryOrderEntryBridge`'s dependency item, values only; stock scan as fallback). Restart imports: data import
  stage **16.9 / 17.2 s** (26.5–27 s unpatched, −36 %), whole import **30.9 / 33.9 s** (41.3–44.9 s, −25 %), storage vs
  stock **1370/1370 identical**. Steps (run B4): library dependency data **9.0 s** (10.6–13.5 s), project repository
  data 2.9 s (6.4–7.5 s), library data 1.8 s, module dependency 1.0 s, source set data 1.1 s. The dependency step keeps
  most of its time elsewhere (workspace-storage index updates per entity, library entity creation, path registration):
  profiled next.
- **Second profile with D24b** (data import stage 18.0 s, import 33.8 s): library dependency data 9.6 s, 97 samples
  on the import thread: `setLibraryScope` → `LibraryOrderEntryBridge.setScope` 39 % (each scope change replaces the
  module entity's dependency list and `ModuleEntityData.updateLinksIndex` re-indexes all of the module's links:
  O(dependencies) per change, so O(dependencies²) per module; the bridge already skips no-op changes),
  `LibraryDataService.registerPaths` 21 % (VFS lookups and root registration per module library),
  `ModifiableModuleLibraryTableBridge.createLibrary` 13 % (`generateLibraryEntityName` checks the candidate name
  against the existing ones), `findLibraryOrderEntry` 5 %. The remaining cost is in the Kotlin bridges of the
  workspace model (not reachable from the Java classes here, no Kotlin compiler in this environment): third item for
  the JetBrains list. Repository step 2.9 s: `rearrangeOrderEntries`, Kotlin facets; no single hot spot left.
- **Stable three-way comparison, restart scenario, 3 rounds round-robin (owner's request)**
  (`results/m5-ide/stable-NOTES.md`). Medians: stock Gradle 51.8 s / IntelliJ 27.1 s / total 1 m 20.4 s; tasks (D23)
  10.3 / 27.2 / 37.6 s; tasks + D24 fixes 10.0 / 19.0 / 29.2 s. Speed-up vs stock: tasks −80 % Gradle, 0 % IntelliJ,
  −53 % total; tasks-fixed −81 % / −30 % / −64 %. The fixes alone: −30 % on the IntelliJ side, −22 % end to end.
  Steps: library dependency 12.4–13.3 → 8.0–8.2 s, project repository 7.5–8.3 → 2.6–3.2 s. Disk after the runs 8.7 GB
  (the stock runs stored three CC entries, cc dir 587 MB).
- **Forced configuration cache miss in the IDE (owner: "test it with a cc miss")**, settings script content
  changed before every run, 3 rounds round-robin (`results/m5-ide/miss-NOTES.md`). Medians: stock Gradle 63.1 s /
  IntelliJ 28.8 s / total 1 m 32.4 s; tasks 40.7 / 23.0 / 1 m 3.6 s; tasks + fixes 40.2 / 12.7 / 52.9 s. Speed-up vs
  stock: tasks −35 % Gradle, −20 % IntelliJ, −31 % total; tasks-fixed −36 % / −56 % / −43 %. On the miss the plain run
  configures all 287 projects (27 s) but all 287 model tasks stay up to date (fingerprints unchanged), so the saving
  is the model-building work, not the configuration; the `GradleBuild` request costs another 10 s because it
  re-evaluates the changed settings script too. Settings file restored (git clean). The CC dir now holds only entries
  for the modified settings contents: the next restart measurement starts with a store.
- **Cache miss without a script change (owner's question)**, configuration cache dir emptied before every run, 3
  rounds round-robin (`results/m5-ide/emptycc-NOTES.md`). Medians: stock Gradle 54.5 s / IntelliJ 26.4 s / total
  1 m 21.1 s; tasks 39.1 / 22.0 / 1 m 1.1 s; tasks + fixes 39.2 / 13.1 / 52.3 s. Speed-up vs stock: tasks −28 % Gradle,
  −16 % IntelliJ, −25 % total; tasks-fixed −28 % / −50 % / −35 %. All 287 model tasks up to date again (execution history
  is outside the CC dir). The stock miss costs the same as its restart miss (54.5 vs 51.8 s); the settings-script miss
  adds ~9 s of script recompilation to stock and ~1.5 s to the task run (whose `GradleBuild` request pays most of it).
  CC dir afterwards 56 MB (one task-world entry, a hit for the next restart measurement), disk 9.0 GB free.
- **Stock sync profiled (JFR, restart scenario, 1 m 23 s import)** for the IDE-side investigation. During the
  55 s resolve stage the IDE's own threads are idle but for 37 resolver and 34 deserialization samples: that stage is
  Gradle time, nothing to gain on the IDE side there. Its data import (25 s) has the same two hot spots the D24 patch
  removes: `findLibraryOrderEntry` → `getLibrary` 111 of 229 import-thread samples in the dependency step,
  `findIdeLibrary` → `getLibraries` 170 of 191 in the repository step. Whole-import attribution (all threads): the data
  import stage is one thread of data services against seven threads of warm-up indexing and PSI parsing (538 vs 1849
  samples in stock, 215 vs 1525 in the patched run); 3 s after the data import are the workspace model storage write.
- **Warm-index re-import** (system dir of a finished import reused without its `external_build_system`, two runs,
  tasks-fixed): the import ran again (scan 6 s instead of 17–19 s of indexing), data import stage **10.9 / 10.0 s**
  (15.8 s with cold indexes), library dependency step 5.3 / 4.8 s (9.0 s), whole import 29.2 / 27.8 s. About 5 s of
  the data import in all headless series is contention with the warm-up indexing; a real IDE after a restart has its
  indexes, so the IDE side there is ~11 s data import plus ~3 s of storage write and setup.
- **Gradle phases from build operation traces, warm daemon (owner: how much is task graph calculation)**
  (`results/m5-ide/phases-NOTES.md`). `Calculate build tree task graph` is 55–62 ms for the 287 model tasks (plus
  ~0.3 s in the plugin builds); nothing to parallelize for the sync. A miss is configure 5.3–5.8 s (parallel) plus,
  for the task run, the configuration cache store 6.1–6.8 s wall / 52–57 s summed (dependency resolution 20 s and
  36k task realizations for the model inputs), or, for the stock action, the model builders 4.2 s wall / 35 s summed.
  Warm-daemon totals: builders 11.9–14.2 s, plain task run 13.0–15.9 s, launcher hit 2.1–3.4 s. The cold-daemon
  IDE runs were 2× these (27–29 s plain run): daemon warmth is the largest remaining Gradle-side lever, then
  per-project invalidation, then the store.
- **Why the IDE chains' Gradle side was ~40 s and the traces show 13–15 s** (owner's question). The chains stop the
  daemon before every run; the resolve stage then contains the daemon start (2–3 s), the `GradleBuild` and
  `BuildEnvironment` requests on a cold daemon (7–10 s on a miss, settings evaluated and the script recompiled), the
  cold-JIT plain run (27–29 s against 13–15 s warm), state assembly (2 s) and the IDE's stage work (2 s), all while the
  warm-up indexing takes seven of the eight cores. The traces are the daemon's build operations alone, after a warm-up
  run in the same daemon, with no IDE process. Consequence for the miss comparison: with a warm daemon the stock
  action and the task run are within noise (11.9–14.2 vs 13.0–15.9 s); the cold-daemon advantage of the task run
  (40 vs 52–63 s in the IDE chains) comes from the builders' code warming up more slowly than the configuration
  path. The M4 "warm miss" harness numbers (builders 25.1 vs tasks-only 11.5 s) were run 0 of their series and
  carry the same JIT-state uncertainty. REPORT point 14 carries the caveat now.
- **Store cost decomposed (owner: are our entries too complex, should we disable the store?)**: baseline runs on
  gradle/gradle with a warm daemon: `help` stores in 18 ms, a no-op task in every project stores in 31 ms / 1.7 MB (1.3 MB of it the build fingerprint); the
  287 model tasks store in 6.1–6.8 s / 56 MB. The store time is the model capture (resolution 20 s + realization 5.4 s
  + classpath/files 3.5 s summed) running inside the store because the task fingerprints derive from the models, plus
  serializing the 56 MB of payloads held as task state. Not Gradle's state serialization. Options in
  `results/m5-ide/phases-NOTES.md`; disabling the cache would lose the hit and parallel configuration for no gain on
  the miss.
- **Why the entries are big (owner's question)**: 6321 payloads, 202 MB inflated, 48.8 MB deflated. A quarter is the
  build script classpath and source path repeated per project in two models (KotlinDslScriptPayload 40 MB,
  GradleBuildScriptClasspathModel 14 MB inflated; xz over all projects: 0.07 MB), the rest mostly the two dependency
  models with one record per dependency per source set per scope (80 + 47 MB inflated; 1.6 + 5.1 MB with cross-project
  compression). The information is per-build, stored per project; a shared table in the root bundle would bring the
  stored size to ~7 MB (`results/m5-ide/phases-NOTES.md`).
- **D25 tried: models as object graphs in the task state** (`CapturedModel`, serialization moved into the task action,
  `-PideModelTasks.state=objects`). gradle/gradle, warm daemon, tasks up to date, two misses each: store bytes 6.7 / 6.3 s
  vs objects 6.4 / 6.0 s, entry **56 vs 68 MB**, hit load 2.5 / 1.9 s. Fidelity: 6321/6321 payload fingerprints
  unchanged. A deleted bundle is recreated from the loaded state on a hit (fixture `:lib`, gradle/gradle `:core`). The
  configuration cache's string deduplication did not shrink the entry: the strings are shared (one 7.6 MB table per
  entry) but the bean codec writes ~50 bytes of structure per dependency record, 57 MB over 287 project files, and
  per payload deflate is denser (corrected reading, see the next entry). Bytes remain the default; the serialization
  share of the store is small (the capture dominates).
- **Gradle finding: the configuration cache entry survived a change of the init-script classpath jar.** The first run
  after rebuilding `ide-model-tasks.jar` (new task class shape) printed "Reusing configuration cache", 287 tasks up to
  date, and loaded the old task state under the new class; a changed jar on an init script's classpath is not part of
  the entry's fingerprint on 9.9.0-milestone-2. For the IDE this means a plugin update needs an explicit invalidation
  (a changing property or `--rerun-tasks`) or the first sync after the update runs with stale task state. Answers the
  open question in PROGRESS (the 48 s before `projectsLoaded` after a rebuild was something else).
- **CC codecs and input snapshotting investigated** (read-only, Gradle 9.9.0-milestone-2 sources;
  `results/m5-ide/cc-serialization-NOTES.md`). Task state is written field by field by `BeanCodec` regardless of
  `@Internal` (`TaskNodeCodec.kt:121-123`, `BeanSchema.kt:142-168`); `ListProperty` values from `project.provider` are
  fixed at store (`ProviderCodecs.kt:106-136`), which is why the capture runs inside the store; `byte[]` is raw
  (`KryoBackedEncoder.java:114-115`); Java serialization hooks are replayed, never stored as blobs; one string table
  per entry (`DefaultConfigurationCacheIO.kt:265-281, 638-643`); files encrypted (`DefaultEncryptionService.kt:44`), so
  deflate before the store is the only compression point. `@Nested` is unpacked in the single-threaded
  `DefaultExecutionPlan.discoverNodeRelationships` loop (the 20 s of D16). Native resolution results
  (`getRootComponent()`, `getResolvedArtifacts()`) are CC-compatible but resolved at store and the same order of size
  as the Ion payloads, and would need IntelliJ's resolver reimplemented in the action. `ValueSource`/`BuildService`
  recreate values at execution only from parameters, without `Project`: not a fit. Options ranked in the notes: drop
  `ProjectDependencies` from the sync (used only by the Dependency Analyzer; 28 % of the bytes, 4.7 s summed capture),
  a compact own record format (entry 56 → ~20–25 MB, estimate), nothing that shortens the 6 s store.

## 2026-10-17

- **D26: compact record format for the dependency model** (`CompactDependencies`: string table + varint records;
  state form per model class; `ModelPayload.readModel` as the one reader; `results/m5-ide/compact-NOTES.md`). Offline
  estimate on `:core` first: Ion deflated 101 KB, plain JSON of the same shape 89 KB (format −12 %), table + references
  33 KB (−68 %); `ProjectDependencies` 107 → 90 KB only. Measured on gradle/gradle, warm daemon: entry **39 vs 56 MB**,
  bundles 43.1 vs 51.9 MB, dependency model 7.1 vs 12.7 MB (−44 %; `:core` 36 vs 101 KB), store 7.2 vs 6.7 s, hit load
  0.22 vs 0.18 s. IDE restart scenario, two hits each: import 27.7 / 28.3 s compact vs 27.9 / 29.7 s bytes, resolve
  stage 9.7 vs 10.0 s, bundle reading after the run 0.25–0.38 s both. Fidelity: canonical text identical for 287/287
  projects against Ion bundles (the first comparison in the chain was compact vs compact: the up-to-date tasks had not
  rewritten the bundles, fixed by deleting them before the bytes run); a deleted `:core` bundle is recreated from the
  compact state. Reading: a size win (−30 % entry, −17 % bundles), not a time win on this machine; the encoding (Ion,
  JSON, varints) is worth ~10 %, the data model (tables, references, build-level scope) is what shrinks the bytes.
  Measurement scripts and outputs in `results/m5-ide/compact/`.
- **Gradle-owned models as tasks (investigation, `results/m5-ide/gradlebuild-as-task-NOTES.md`).** `BuildEnvironment`
  never reaches the daemon: `ProviderConnection.run` (launcher, lines 160-174) answers it in the client from the build
  layout and `gradle.properties`, so the cold-daemon cost attributed on 2026-10-16 to "GradleBuild and BuildEnvironment"
  is GradleBuild alone. IDE 253.28294.334 does not request `GradleDslBaseScriptModel`; the Kotlin plugin derives script
  definitions from `<gradleHome>/lib`; 2026.2 fetches it first inside the action and its Kotlin sync contributor reads
  it from the holder state. `GradleBuild` can be replaced by a per-build `ideBuildTree` task (descriptor tree at
  `projectsLoaded`, `gradle.includedBuilds`, identity path) fanned out with `IncludedBuild.task()`; the IDE only needs
  `DefaultGradleLightBuild`'s fields, synthesizable with `DefaultBuildIdentifier`/`DefaultProjectIdentifier`. Saving:
  the 0.25–0.35 s warm request; on a cold daemon only the duplicate entry check and settings load, not the daemon
  start. Open: IP legality of string-path `dependsOn`, buildSrc addressing, resilience fallback.
- **D27: Mac kit on IntelliJ IDEA 2026.2.2 (262.10315.125).** The four patches ported to the 262 sources
  (`ide-plugin/src/patched-262/java`, `-PideaBranch=262`; 8-36 changed lines each applied with offsets, no rejects;
  the IDE's sources need Java 25), jar layout found by content (`plugins/gradle-plugin/lib/intellij.gradle.jar`,
  `lib/intellij.platform.externalSystem.impl.jar`), `GradleModelConsumer.consumeBuildModel` abstract in 2026.2,
  `GradleDslBaseScriptModel` requested on the plain-run connection for 2026.2's Kotlin plugin. Headless smoke on this
  machine, gradle/gradle, fresh IDE config (download-sources on, so the first import resolved sources): first import
  2 m 16 s (plain task run 116.6 s, cold daemon, store), restart hit **31.6 s** (plain task run 3 s, 287 bundles,
  5755 models), no plugin errors (one IDE NPE at project close in `GradleConnectorServiceImpl.dispose`, unrelated).
  Kit (376 KB): patched classes, plugin jars, `wbs.vmoptions`, badged icon SVG, `install-mac.sh` (copies the app,
  overlays jars with `zip`, bundles the plugin, appends the options, renames, renders the icon with QuickLook +
  `iconutil`, re-signs ad hoc), README. Not run on a Mac here.
- **2026.2.2 stock vs tasks + fixes, restart scenario, 3 rounds** (`results/m5-ide/stable-262-NOTES.md`): stock
  48.2 s Gradle / 24.8 s IntelliJ / **1 m 13.5 s**; tasks + fixes 10.3 / 22.0 / **32.3 s** (−79 % / −11 % / −56 %).
  The `GradleDslBaseScriptModel` request that 2026.2 added costs 81–95 ms warm, nothing else is requested in the
  single-invocation mode (the action's bootstrap incl. `GradleDaemonClassLoaderModel` never runs). First three
  patched rounds showed the import fixes gaining only 2.8 s (data import 18.8 s, project repository data 5.7 s); a JFR
  run put 229 of 293 samples of that step in the unmemoized `findIdeLibrary` scan: `install-patched-ide.sh` had
  re-copied `lib/intellij.platform.externalSystem.impl.jar` for its second overlay (both platform classes share that
  jar in 2026.2), wiping the memoized lookup. Script fixed (one copy per jar), re-measured rounds 4–6: tasks + fixes
  10.5 / 21.6 / **31.8 s** (−78 % / −13 % / −57 %), data import 16.1 s, project repository data 3.0 s, library
  dependency data 8.6 s: the same picture as 2025.3. The Mac kit's installer was never affected.
- **2026.2.2 forced miss (settings script changed per run) and tasks without fixes, 3 rounds each**
  (`results/m5-ide/miss-262-NOTES.md`): miss stock 53.3 / 25.3 / **1 m 19.2 s**, tasks 41.3 / 22.2 / **1 m 3.6 s**
  (−20 %), tasks + fixes 41.3 / 13.5 / **54.8 s** (−31 %). Restart hits for tasks without fixes: 10.9 / 27.0 /
  **38.0 s** (−48 %). The IntelliJ side is slower on a hit than on a miss for the same configuration (21.6 vs 13.5 s
  with the fixes) because the headless warm-up indexing still runs when a 10 s Gradle side hands over; the miss column
  is the warm-index number. Table of changes added to `results/REPORT.md`.
- **D29: GradleBuild as a task** (`results/m5-ide/buildtree-262-NOTES.md`). `ideBuildTree` root task per build
  (settings descriptor tree + included builds + Gradle's base-script model via its own builder, reflectively),
  aggregate `ideModels`, plugin option `-Dwbs.transport.tree=task` (one `:ideModels` request, build view synthesized
  with Gradle's model POJOs and `ProtocolToModelAdapter`). Fidelity 1370/1370 storage files. Restart hit, 2026.2.2,
  3 rounds: model request 30.7 s cold / 31.3 s warm vs tree task 31.9 / 32.2 s; Gradle side 10.0–10.5 s in all four.
  The intermediate chain (tree task, base-script model still requested) explained it: that request went from 72–94 ms
  to 1.8–2.2 s as the first operation on the connection; the first daemon contact costs ~1.8 s whichever operation
  makes it. Found on the way: under IP a project's `Gradle` is not the settings' instance (key per-build state by
  identity path); `DefaultGradleDslBaseScriptModel` & co. are package-private; the plugin must re-read the tree bundle
  after the run (streaming had read the previous run's). Forced miss (settings script changed per run), 3 rounds:
  model request 55.6 / 56.3 / 54.9 s vs tree task 55.9 / 54.6 / 53.1 s, task run 37.0–38.7 s in both: no measurable
  difference on a miss either. Tree mode stays an option; the model request remains the default.
- **Mac kit, first run on the owner's Mac: patched classes failed to load** (`NoClassDefFoundError` for
  `GradleModelFetchAction` in the Gradle plugin's classloader). Cause: the IDE's jars carry a lookup index (an
  `__index__` entry, 40 KB, plus a 5-byte archive comment marker) that IntelliJ's own zip reader (`ImmutableZipFile`,
  `platform-loader.jar`) trusts for entry offsets; Info-ZIP's `zip -u` keeps both while moving entries, so the IDE read
  the patched class at a stale offset. The JDK's `jar` tool, used on Linux, drops the comment, so the reader fell back
  to the central directory there. Fix: `JarOverlay.java` in the kit, run with the IDE's bundled JBR from source,
  rewrites the jar without the index and comment, entries stored uncompressed like the originals; the Linux copy
  patched the same way syncs (287 projects, 5756 models, no class-load errors). Kit repackaged.
- **Mac kit, second and third run:** macOS App Management refused the JBR's write into the copied bundle ("Operation
  not permitted"; the overlay now runs in a temp dir with the source app's JBR and `cp` writes back), then the IDE did
  not load the plugin from `Contents/plugins`: 2026.2 ships `plugins/plugin-classpath.txt`, a precomputed index of
  the bundled plugins, and does not scan that directory for others. The kit installs the plugin into the copy's user
  plugin directory (`~/Library/Application Support/JetBrains/IntelliJIdea2026.2-WBS/plugins`) instead. Fourth run:
  still not loaded, because the macOS launcher appends `-Didea.paths.selector` from `product-info.json`'s
  `dataDirectoryName` after the vmoptions, so the copy used the shared `IntelliJIdea2026.2` directories; the kit now
  sets `dataDirectoryName` to `IntelliJIdea2026.2-WBS` in the copy's `Contents/Resources/product-info.json`. Fifth run:
  the launcher takes the selector from the explicit `-Didea.paths.selector=…` entries of `product-info.json`'s launch
  arguments (4 occurrences), rewritten too; the kit then worked on the owner's Mac (Apple Silicon): first sync fine,
  the tree mode (D29) enabled by default in the kit afterwards.
- **Owner's Mac, second sync in the WBS copy:** `IllegalStateException: removeContentEntry: removed content entry url
  '…/platforms/software/ant/build/generated/sources/annotationProcessor/java/main' still exists after removing`
  (`ContentRootDataService.importData:253` → `ModifiableRootModelBridgeImpl.removeContentEntry`), i.e. the module held
  two content entries with that URL before the re-sync removed one. Not reproducible here: the headless warm-up only
  imports a new project, never re-syncs. Checked instead on 2026.2.2 (`results/m5-ide/kit-262/mac-resync/`): fresh
  imports by stock and by the patched IDE write identical content roots, no module has a duplicated content URL, with
  or without that generated directory present (neither IDE lists it as a content root on Linux); the model dump
  comparison builders vs bundles is identical for `GradleSourceSetDependencyModel` (287/287), `ExternalProject`
  (287/287), `AnnotationProcessingModel` (256/256), and `GradleSourceSetModel` except the two known benign
  `configurationArtifacts` differences. The patched-vs-stock storage differs only in 75 module files where a library
  (jackson-annotations 2.22, hamcrest 3.0, jackson-core 2.22.3) is module-level in one and project-level in the other:
  the IDE's conflict rule for same-named libraries with different roots depends on module processing order, not on
  model content. Most likely cause of the Mac error: the project model had been built by the stock Toolbox IDE and by
  the first kit attempts that ran with the shared `IntelliJIdea2026.2` config; suggested check: invalidate the WBS
  copy's caches and sync twice. Still there afterwards. Added `wbsResync` (an `appStarter` in the plugin:
  `idea.sh wbsResync <project> [count]`, headless re-sync of an imported project, the warm-up command only imports new
  projects and `--configure-project` does not refresh linked ones). Two re-syncs on 2026.2.2 with the patched IDE and
  the generated directory populated: no error, no duplicated content URL in any of 1367 modules. Mechanism from the
  262 source (`ContentRootDataService.findOrCreateContentRoot`, lines 296-302): existing content entries are matched
  through `filePathToContentEntryMap`, built only from entries whose directory exists (`contentEntry.getFile() != null`),
  so an entry for a missing directory is never matched and every re-sync adds a duplicate; `contentEntriesMap.remove(url)`
  then shields the stale one. When the URL later leaves the data, `removeContentEntry` on one duplicate trips the
  bridge's "still exists after removing" check. Trigger on the Mac: the annotation processor output directory, present
  only after a build, listed by that checkout's model (not by gradle/gradle 9.9-m2 here). An IDE issue independent of
  the transport; the kit README documents the reset (delete the WBS caches dir) and the cause.
- **Init script order nondeterminism (found through the re-sync run's cache miss):** the plugin's init script content
  follows `GradleProjectResolverExtension.getToolingExtensionsClasses()`, which return `Set.of(...)` with per-JVM
  iteration order, so the generated classpath order can differ between IDE processes; Gradle then reports
  "init script 'wbsModelTasks2.gradle' has changed" and misses the entry after an IDE restart. The classes are now sorted
  by name before the script is generated.
- **gradle-profiler `idea-sync` works with the kit** (`results/m5-ide/profiler-262-NOTES.md`, `profiler/`): the
  sandbox replaces the app's vmoptions (`IDEA_VM_OPTIONS`) and plugin dir, so the runner puts the `-Dwbs.*` options
  into `ide-jvm-args` and copies the plugin into `<sandbox>/plugins` (the profiler leaves other plugins alone); its
  agent instruments only the stock build action, so WBS shows 0 ms Gradle time (totals comparable). Headless on this
  machine, gradle/gradle, same-session iterations: settings-script miss stock 70.8 / 65.9 / 65.3 s vs WBS 43.2 / 41.9 /
  41.7 s (−36 %); hit (both hit the cache in-session) stock 35.0 / 51.5 / 50.7 s vs WBS 25.3 / 39.5 / 40.1 s.
- **Owner's Mac: Kotlin scripts show "Code insight is unavailable (Script Configurations not loaded)" in the WBS
  IDE.** The Kotlin plugin reads the assembled `KotlinDslScriptsModel` through `ProjectResolverContext.getBuildModel`
  (2026.2, `KotlinDslScriptModelProcessorKt`) or `getRootModel` (2025.3), i.e. under a build model id, while the
  transport registered it under the root project's project model id (as the comment in `TaskTransport` assumed from
  `GradleClassBuildModelProvider`). Never caught: the headless runs cannot show code insight, and the harness compared
  the model's content, not its id. Now registered under both ids (`buildState()`, and `consumeBuildModel` in the
  `--tasks` transport). Still unavailable afterwards: the second cause was that the single-invocation mode never ran the
  IDE's fetch-result listener and hence no sync contributor (D30). Fixed by feeding the state through the IDE's result
  bridge; costs measured: `all` phases blow up the legacy import (lib deps 24 s, project repos 23 s), `scripts` only adds
  the Kotlin script contributor: 44-62 s on a fresh IDE, re-sync 67 s vs 27 s without (JFR: workspace VirtualFileIndex
  updates for script classpath URLs in `GradleKotlinScriptEntityProvider`, and lock waits). Default `scripts`; `none`
  restores the previous speed without script insight. `wbsResync`, phase timestamps (`[wbs] sync contributors: …`).
  Back-to-back comparison, warm daemon (`results/m5-ide/kit-262/phases/`): `none` import 33.9 s, re-sync 34.9 s;
  `scripts` import 52.6 s (contributor 23.3 s), re-sync 42.5 s (contributor 5.8 s, the second pass is incremental).
  So the script insight costs ~19 s on a fresh IDE session and ~8 s per later re-sync on gradle/gradle here; the earlier
  44-67 s figures were taken with the warm-up indexing running alongside.
- **Kit diagnostics (2026-10-17 night).** The owner's gradle-profiler run on the Mac failed with "no plain model task run
  happened before buildState()". That is the secondary symptom: `preImportCheck` caught the plain run's exception,
  printed it to stderr and let the sync continue into `buildState()`. Neither the Mac app nor the profiler sandbox keeps
  stderr, so the cause was lost. Changes: in `only` mode the sync now fails in `preImportCheck` with
  `[wbs] plain model task run failed after N ms: <cause>` (cause attached); every `[wbs]` line goes through `WbsLog`
  (stderr + IntelliJ logger `#wbs`, so idea.log has them; profiler: `<sandbox>/logs/idea.log`). `wbsResync` retries while
  the sync started by opening the project is running (it failed with "Another 'Sync project' task is currently running"
  on the fixture). Smoke-tested headless on the fixture: both syncs succeeded, 12 `#wbs` lines in idea.log. Kit repacked.
  Difference between the owner's working IDE sync and the profiler run worth checking first: the profiler's IDE plugin
  sets the Gradle JVM of a freshly linked project to `#JAVA_HOME`.
- **Module reorganization (D31, 2026-10-18).** `prototype` split into `ideTask.{tasks,capture,payload,consumer}` with
  role-based names; prototype tests, harness and IDE plugin build; `prototype/README.md` written. Two things the move
  surfaced: (1) `PortedModels` spelled the two payload class names as string literals, which the package move left stale
  (now `X.class.getName()`); (2) a kit upgrade reused the project's stale state. With the new jar on the init script's
  classpath, a fresh daemon still printed "Reusing configuration cache" and every model task UP-TO-DATE, so the bundles on
  disk kept the old class names and the IDE failed with "build tree bundles missing". Reproduced from the command line:
  the configuration cache fingerprints the init script's *text*, not the content of the jars on its classpath, and
  `-Pwbs.v=1` did not invalidate either (only properties the build reads are inputs). A changed init script text does
  ("cannot be reused because content of 1st init script has changed"), so the plugin now writes
  `// ide-model-tasks <sha256 prefix of the jar>` into the generated script: one reconfiguration per plugin version, then
  hits again (fixture: 7.9 s miss, 78 ms hit, contributors ran). `wbsResync` now waits 5 s for the open-project sync and
  for any running RESOLVE_PROJECT task before its own sync (a concurrent one was rejected or stopped the first one's
  connection). Kit repacked with the new jars and drop lists.
- **Build-action mode removed (owner's request, 2026-10-18).** The request model (`IdeModelTaskRequestBuilder`,
  `IdeModelTaskRequest`, `DefaultIdeModelTaskRequest`, the `ModelBuilderService` registration) existed only for the mode
  where the tasks are scheduled from inside IntelliJ's build action; since D23 the sync is one plain task run. Gone with
  it: the IDE plugin's `TaskTransport` (request and file providers) and the `plain`/default run modes (the plugin always
  runs the tasks in `preImportCheck`; `-Dwbs.transport.run=only` stays as the patched resolver's switch), and the
  harness's `--tasks`/`--tasks-only` action modes with `IdeTasksTransport` (`--launcher` remains; `JavaSerialized.read`
  keeps the dump readers working). Kept: `DropList`, which generates the `-Dwbs.transport.drop` value the patched
  `GradleModelFetchAction` applies. Prototype tests, harness and plugin build; fixture smoke: 5.5 s miss, contributors ran.
- **Owner's gradle-profiler run on the Mac (2026-10-18):** "class ...capture.BuilderContextService$Inject_ cannot be cast to
  class ...capture.BuilderContextService" with two `InstrumentingVisitableURLClassLoader`s: the init script's classpath
  was loaded twice in one build and the shared build service, keyed by name only, was registered by one loader and read by
  the other. Not reproducible here (fixture and gradle/gradle, 9.9.0-milestone-2 on both sides, 287 projects in 27.6 s on
  a miss); the origin of the second loader is open (full stack requested). Fix regardless: the service name carries the
  identity hash of the class's loader, so every loader registers its own context; a context cannot be shared across
  loaders anyway (IntelliJ's classes come from the same loader). Kit repacked.
