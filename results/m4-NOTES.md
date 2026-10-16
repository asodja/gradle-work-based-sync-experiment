# m4 notes

- Date: 2026-10-15, 20:42–21:13 UTC, one chain (`m4-builders`, `m4-tasks`, `m4-tasks-only`, `m4-launcher`, each
  `run-scenarios.sh <label> 1 2 3 4 7`; `.gradle/configuration-cache` of the benchmark worktree deleted before each label).
- Machine: as `design/environment.md` (aarch64, 8 cores, 31 GB, JDK 25.0.4, daemon `-Xmx3300m`). gradle/gradle
  `dedc5d14f2d`, Gradle 9.9.0-milestone-2 via the TAPI, Gradle user home `~/.gradle-wbs`, IntelliJ jars
  `~/tools/ideaIC/idea-IU-253.28294.334`. Experiment repo at `1de626c` (prototype and harness as committed there).
- Task variants: `WBS_EXTRA="--tasks|--tasks-only|--launcher --extra-tooling-jar prototype/build/libs/ide-model-tasks.jar"`.
- Edit files: scenario 4 appends a comment to `subprojects/core/build.gradle.kts`; scenario 7 appends a comment to
  `settings.gradle.kts` (not `gradle.properties`, whose unread properties do not invalidate the entry). Both restored
  by the script; the worktree is clean afterwards.
- Run status: 96/96 `status=OK`, every build log free of failures, CC problems and IP problems.
- **Caveat, cold runs**: builders cold is 51.6 s here vs 34.7 s in M0 (same commit, same harness); the model phase
  took 27 s instead of 14 s. The chain wrote 17 GB of build operation traces (not deleted by the subagent) and filled
  the disk to 100 % during the later labels, which evicts the page cache that cold runs depend on. Cold numbers are
  reported but not used for the gate (D15) and should not be compared across sessions.
- Trace logs (`*-trace-log.txt`, ~900 MB each) were deleted after aggregation; the aggregated `<s>-models.md` tables
  are kept.
- The benchmark-runner subagent completed the runs but did not write the summary, notes or LOG entry; those were
  written by the lead session from the raw files.
