# Measurement

## Why warm CC-on is the headline

On a small build the cost is settings evaluation (`GradleBuild`), the Kotlin DSL script model and
first-time dependency resolution. A task port changes none of them, so a cold sync measures the same or
slightly worse. Tasks win in three cases only: a warm re-sync with the configuration cache on (no
configuration, no model computation, only up-to-date checks and file reads), large builds (per-project
cost × projects, parallel execution), and incremental re-sync after a small change. gradle/gradle
provides the second; the scenarios provide the other two.

## Isolated Projects on the baseline: parallel, but not cached

gradle/gradle's `gradle.properties` sets `org.gradle.isolated-projects=true`,
`org.gradle.configuration-cache=true`, `configuration-cache.parallel=true` and `tooling.parallel=true`.
Under IP, model building runs in parallel across projects, but the per-project **model cache is off by
default**: it is governed by the internal option `org.gradle.internal.isolated-projects.caching`
(`BuildModelParametersProvider.kt`), whose default is `false`; only `tooling` or `true` turns on
`isCachingModelBuilding`, which wraps the model creator and tooling controller with the CC-aware
variants (`ConfigurationCacheAwareBuildTreeModelCreator`, `ConfigurationCacheAwareBuildToolingModelController`).
So on the project's defaults every IntelliJ builder runs on every sync, and the baseline for the
headline is uncached builders running in parallel under IP.

Because the task approach competes with that opt-in cache, measure it too as a reference point:
scenario 5 below runs the builder version with `-Dorg.gradle.internal.isolated-projects.caching=tooling`.
If the opt-in cache already delivers most of what tasks deliver, that is a finding.

## gradle/gradle as the benchmark

254 projects with a build script, Kotlin DSL throughout, three included builds (`build-logic-settings`,
`build-logic-commons`, `build-logic`) with 119 precompiled script plugins, 169 projects with extra source
sets (`integTest`, `crossVersionTest`, test fixtures). Controls:

- Sync with the distribution built from the same gradle/gradle commit; build-logic uses APIs of that
  version. Point the wrapper at it (`design/building.md`).
- gradle/gradle's `gradle.properties` enables the configuration cache and Develocity by default. Pin both
  explicitly per scenario (`--configuration-cache` / `--no-configuration-cache`, no scan upload).
- Give the daemon the heap `gradle.properties` asks for. Same machine, same JDK, same day for both versions.
- Disable background noise: no IDE indexing during harness runs, file-system watching on in all runs.

## Scenarios

Each three times; report median and spread.

| # | Scenario | Setup | What it shows |
|---|---|---|---|
| 1 | Cold | `gradle --stop`, CC off, IP off | floor, nothing beats it |
| 2 | Warm, plain | daemon alive, no change, CC off, IP off | per-project model cost, sequential, no caching |
| 3 | Warm, IP | daemon alive, no change, project defaults (CC + IP on, model cache off), entry present | **headline**: builders re-run in parallel vs tasks up to date from the CC entry |
| 4 | Incremental, IP | as 3, then edit one subproject build script (add a comment to `subprojects/core/build.gradle.kts`), restore after | invalidation granularity: how much re-runs on each side |
| 5 | Warm, IP + model cache (builders only) | as 3 plus `-Dorg.gradle.internal.isolated-projects.caching=tooling` | reference: what the existing opt-in model cache gives, to compare with the task version's scenario 3 |

Both versions run 1–4; scenario 5 is for the builder version. Run 3–5 with the project's own
`gradle.properties`, and 1–2 with `--no-configuration-cache -Dorg.gradle.isolated-projects=false`. A
task-version run under 3 or 4 that reports IP problems is a failed run, not a data point; fix the port
first.

## Metrics

- Total wall time of the fetch (harness) and `GRADLE_CALL` / `PROJECT_RESOLVERS` (IDE, M0 and M5).
- Per-model: count, total, max, from the request log (`design/context.md` §5). For the task version, the
  same from task execution times (build operation trace or `--profile`).
- Task version breakdown: configuration time, task execution time, file reading time, time in the
  remaining model builders.
- Scenario 4: number of tasks re-executed vs up to date.
- Memory is not a metric here; note the daemon heap used, nothing more.

## Harness (M1)

A Gradle-built Java TAPI client in `harness/`:

- Classpath: Gradle Tooling API of the measured version plus the installed IDE's
  `plugins/gradle/lib/*.jar` and `plugins/Kotlin/lib/*.jar` (for `GradleModelFetchAction`, providers,
  model classes, serializers).
- Runs `GradleModelFetchAction` with the providers `CommonGradleProjectResolverExtension`,
  `JavaGradleProjectResolver` and the Kotlin resolvers register, via `BuildActionExecuter` with
  `projectsLoaded`/`buildFinished`, `-Didea.resolveSourceSetDependencies=true` and the init script IntelliJ
  uses to inject the tooling extension (copy it from an IDE sync log or reconstruct from
  `GradleExecutionHelper`).
- Prints the totals and aggregates `~/model-requests.log`.
- A shell runner for the four scenarios, writing `results/<label>/<scenario>-<run>.txt`.
- Validation: harness total ≈ IDE `GRADLE_CALL` from M0 within ~10 %.

## Reporting

`results/REPORT.md`: one table per version with the four scenarios (median, min, max), the per-model top
ten for each version, the task-version breakdown, the incremental scenario detail, and a "blockers"
section. State what was not measured. Raw numbers stay in `results/<label>/`.
