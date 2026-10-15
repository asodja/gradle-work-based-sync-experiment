# Progress

Keep this file current. A resuming agent continues from here alone.

## Current milestone

M0 + M1 together: baseline on gradle/gradle measured with the harness (no IDE can run here, DECISIONS D1).
In progress.

## Environment

This machine; details and disk budget in `design/environment.md`. Fill in as things are created:

- Benchmark worktree of gradle/gradle: `~/projects/gradle-wbs-benchmark`, detached at `dedc5d14f2d` (master of 2026-10-14)
- Gradle distribution used for measurements: the worktree's own wrapper distribution `gradle-9.9.0-milestone-2`
  (DECISIONS D2); Gradle user home for all benchmark runs: `~/.gradle-wbs` (D4)
- IntelliJ for tooling jars: `~/tools/ideaIC/idea-IU-253.28294.334` (IDEA 2025.3, unified installer, Linux aarch64; D5).
  Harness classpath uses only `plugins/gradle`, `plugins/gradle-java`, `plugins/Kotlin` and `lib`, so Ultimate-only
  resolvers are not loaded.
- IntelliJ Community fork: not yet cloned (M3)
- Disk free at last check: 28 GB (2026-10-15, after cleanup)

## Done

(nothing yet)

## Done

- M1 harness works end to end on `harness/fixture` (see `harness/README.md`): real `GradleModelFetchAction`, providers
  from the IDE's 22 `projectResolve` extensions, IntelliJ's init scripts, phase timings, model counts, task outcomes,
  CC lines; `--repeat` runs in one JVM (needed for CC entry reuse, LOG 2026-10-15); `--before-run` hook; trace
  aggregation (`aggregate_trace.py`) and `summarize.py` verified on fixture output.
- Benchmark worktree warmed up (`./gradlew help`, 25 min, build-logic compiled, `~/.gradle-wbs` 2.5 GB).

## In flight

- First harness sync of gradle/gradle, in the background, with the operation trace:
  output `scratchpad/logs/gg-first.txt` (+ `.build.log`, `.console`), trace `scratchpad/logs/gg-first-trace-log.txt`
  of session `03bfd696`. It started by downloading the distribution into `~/.gradle-wbs/wrapper/dists` because the
  harness used `useBuildDistribution()`; for scenario runs pass `--installation` or accept the one-off download.
- Not yet applied: the M0 gate. Key open question from the fixture: with CC + IP on and a reused entry the whole
  action result is served from the cache (60 ms). Whether gradle/gradle ever reuses the entry between syncs, and
  what scenario 4 costs there, decides the experiment (LOG 2026-10-15).

## Next step

1. Read the first gradle/gradle sync result; fix whatever fails (likely candidates: proxy/dependency downloads,
   IP problems from IntelliJ builders, memory).
2. Run `harness/run-scenarios.sh m0-baseline` in the background (benchmark-runner subagent), scenarios 1-5.
3. Fill `results/m0-baseline/models.md` from the scenario 2 trace (sequential, honest per-model costs) and the
   scenario 3 trace, apply the M0 gate, update `design/model-inventory.md` cost columns, and revisit
   `design/measurement.md` in the light of the whole-action caching finding (what does "warm, IP" mean for
   builders when the entry is reused: that is the real baseline for tasks).

## Blocked on owner

(none)

## Gates applied

| Gate | Criterion | Outcome | Date |
|---|---|---|---|
| after M0 | per-project IntelliJ models sum to > 3 s on warm sync | | |
| after M2 | task up to date on all 254 projects in the warm IP run, no IP problems | | |
| after M4 | warm IP task sync faster than the warm IP builder sync by the margin fixed in LOG.md | | |
