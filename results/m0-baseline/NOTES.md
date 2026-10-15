# m0-baseline notes

- Date: 2026-10-15. Scenarios 1, 2, 4, 5 run 15:59-16:06 UTC (about 7 min total). Scenario 3 ran earlier the same day.
- Machine: gradle-ai-workspace, Ubuntu 26.04, aarch64, Linux 7.0 (design/environment.md). nproc=8, RAM 31 GB (free -g: 9 used, 19 buff/cache, 22 available).
- JDK: openjdk 25.0.4 2026-07-21 (default java; Gradle daemon JVM args from gradle.properties: -Xmx3300m -XX:MaxMetaspaceSize=768m).
- gradle/gradle commit dedc5d14f2d, worktree ~/projects/gradle-wbs-benchmark (git status clean after the runs).
- Gradle distribution: 9.9.0-milestone-2 (the worktree's wrapper version), via the TAPI. Gradle user home ~/.gradle-wbs.
- IntelliJ jars: ~/tools/ideaIC/idea-IU-253.28294.334.
- Experiment repo commit at start of run: 741d608 (baseline builder version).
- Command: `harness/run-scenarios.sh m0-baseline 1 2 4 5 > scratchpad/logs/scenarios-1245.log 2>&1` (log: /tmp/claude-1000/-home-agent-projects-gradle-work-based-sync-experiment/03bfd696-8798-4573-b4b6-36c58133e9f7/scratchpad/logs/scenarios-1245.log).
- Scenario 3 was run earlier the same day by the lead session with `run-scenarios.sh m0-baseline 3`. Its run 0 overlapped with a CPU-heavy trace aggregation (3-0 is not a timed run, but note it).

## Run status

All 12 timed runs and the 4 warm-up runs have status=OK. All four trace runs exited 0. No failed runs.

## total_ms per run (run 0 is the warm-up, not in the medians)

| Scenario | run 0 | run 1 | run 2 | run 3 |
|---|---|---|---|---|
| 1 cold (no run 0) | n/a | 38185 | 34520 | 34696 |
| 2 warm plain | 22612 | 16895 | 16691 | 18532 |
| 4 incremental | 16352 | 15963 | 14933 | 14306 |
| 5 IP + model cache | 18120 | 1864 | 1278 | 1234 |

## Observed

- Cold run 1-1 (38.2 s) is about 3.5 s slower than 1-2 and 1-3 (34.5-34.7 s). Scenario 2 run 3 (18.5 s) is 1.6-1.8 s above runs 1 and 2. Scenario 5 run 1 (1864 ms) is about 600 ms above runs 2 and 3. Scenario 3 spread (1055-2330 ms) is wide. All other siblings are within about 1 s.
- Scenario 5 run 0 took 18.1 s and the timed runs about 1.3 s. Run 0 is the first run with the changed -D flag, so it stored a new CC entry.
- Scenario 4 reports "Configuration cache entry stored" in every timed run (the edit invalidates the entry each time). Tasks: executed=27, skipped=66, up-to-date=271, the same counts as scenarios 1 and 2.
- Scenarios 1, 2 and 4 show the same task counts. Scenarios 3 and 5 show up-to-date=1 and "Reusing configuration cache".
- Build logs contain only the generic "deprecated Gradle features were used" warning with the --warning-mode hint. No "problems were found" text, no Isolated Projects problem reports, no errors, no failed tasks in any build log. (Task names containing "Errors", such as checkKotlinGradlePluginConfigurationErrors, are SKIPPED and are not errors.)
- Daemon restarts: the script stops the daemon before scenario 1 and as required per scenario. ~/.gradle-wbs/daemon/<version>/ holds several daemon logs from earlier sessions. I did not check which belong to this run.
- Scenario 4's edit was restored by the script. Benchmark worktree git status was clean afterwards.
- *-trace-log.txt files (about 660k operations each) are ignored by git (results/**/*-trace-log.txt).
