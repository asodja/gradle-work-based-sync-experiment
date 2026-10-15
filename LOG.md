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
