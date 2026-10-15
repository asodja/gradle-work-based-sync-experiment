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
