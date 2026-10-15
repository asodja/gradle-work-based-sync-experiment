# Measurement

## Why warm CC-on is the headline

On a small build the cost is settings evaluation (`GradleBuild`), the Kotlin DSL script model and
first-time dependency resolution. A task port changes none of them, so a cold sync measures the same or
slightly worse. Tasks win in three cases only: a warm re-sync with the configuration cache on (no
configuration, no model computation, only up-to-date checks and file reads), large builds (per-project
cost × projects, parallel execution), and incremental re-sync after a small change. gradle/gradle
provides the second; the scenarios provide the other two.

## The baseline already caches models: Isolated Projects

gradle/gradle's `gradle.properties` sets `org.gradle.isolated-projects=true`,
`org.gradle.configuration-cache=true`, `configuration-cache.parallel=true` and `tooling.parallel=true`.
With IP on, Gradle already caches each project's tooling models in the CC entry
(`BuildModelParameters.isCachingModelBuilding`) and serves them without configuring the project on a
warm sync. So on this build the comparison is **tasks vs IP-cached model builders**, and the expected gap
is smaller than against uncached builders. What tasks can still win: finer invalidation (a task re-runs
when its inputs change, an IP model is invalidated with its project and anything coupled to it),
parallel execution through the scheduler, and no model-building phase at all on a full CC hit. What they
can lose: task scheduling and up-to-date-check overhead for 254 × N tasks, and file reading.

Measure both baselines: IP on (the project's default, the realistic one) and IP off with CC off (the
"plain" builder path, for the magnitude of the per-project model cost). Report the task version against
both and say which is which.

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
| 2 | Warm, plain | daemon alive, no change, CC off, IP off | per-project model cost without any caching |
| 3 | Warm, IP | daemon alive, no change, CC + IP on (project defaults), entry present | **headline**: builders served from the IP model cache vs tasks up to date from the CC entry |
| 4 | Incremental, IP | as 3, then edit one subproject build script (add a comment to `subprojects/core/build.gradle.kts`), restore after | invalidation granularity: how much re-runs on each side |

Both versions run all four. Run each with the project's own `gradle.properties` for 3 and 4, and with
`--no-configuration-cache -Dorg.gradle.isolated-projects=false` for 1 and 2. A task-version run under
3 or 4 that reports IP problems is a failed run, not a data point; fix the port first.

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
