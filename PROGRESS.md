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
- IntelliJ for tooling jars: IDEA 2025.3 (253.28294.334) Linux aarch64, unpacking into `~/tools/ideaIC/` (D5)
- IntelliJ Community fork: not yet cloned (M3)
- Disk free at last check: 28 GB (2026-10-15, after cleanup)

## Done

(nothing yet)

## In flight

- `harness/`: Gradle build (`build.gradle.kts`, TAPI 9.9.0-milestone-2 + IDE jars via `-PideaHome`),
  `aggregate_trace.py` (trace -> per-model table), `run-scenarios.sh` (scenarios 1-5, traced profiling run per
  scenario). Missing: `src/main/java/wbs/harness/SyncHarness.java`, the TAPI client itself. Its CLI is fixed by
  `run-scenarios.sh`: `--project --gradle-user-home --idea-home [--installation] [--arg X]... [--out file]`.
- First `./gradlew help -g ~/.gradle-wbs --no-build-cache --no-scan` in the benchmark worktree, to populate
  `~/.gradle-wbs` (log: session scratchpad `logs/warmup-help.log`).

## Next step

1. Write `SyncHarness.java` against the 2025.3 jars (provider list mirrors `CommonGradleProjectResolverExtension`,
   `JavaGradleProjectResolver`, manifest/repositories resolvers, Kotlin resolvers; init script assembled from the
   same resources IntelliJ uses: MapperInit, IdeaPluginConfigurator, ClassPathExtensionInitScript +
   RegistryProcessor + JetGradlePlugin + Init). Smoke-test on a small fixture, then on gradle/gradle.
2. Run `harness/run-scenarios.sh m0-baseline` in the background (benchmark-runner subagent).
3. Fill `results/m0-baseline/models.md`, apply the M0 gate, update `design/model-inventory.md` cost columns.

## Blocked on owner

(none)

## Gates applied

| Gate | Criterion | Outcome | Date |
|---|---|---|---|
| after M0 | per-project IntelliJ models sum to > 3 s on warm sync | | |
| after M2 | task up to date on all 254 projects in the warm IP run, no IP problems | | |
| after M4 | warm IP task sync faster than the warm IP builder sync by the margin fixed in LOG.md | | |
