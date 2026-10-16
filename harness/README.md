# Harness

Tooling API client that runs IntelliJ's real `GradleModelFetchAction` with the providers the IDE's resolvers
register, loaded from an installed IntelliJ IDEA (`plugins/gradle/lib`, `plugins/gradle-java/lib`,
`plugins/Kotlin/lib`, `lib`). Requirements: `design/measurement.md` ("Harness").

```
(cd ../prototype && ./gradlew jar -PideaHome=~/tools/ideaIC/idea-<version>)   # the model tasks jar the harness embeds
./gradlew installDist -PideaHome=~/tools/ideaIC/idea-<version>
build/install/harness/bin/harness --project <build> --gradle-user-home <dir> --idea-home <idea> \
    [--installation <gradle dist>] [--arg <gradle arg>]... [--jvm-arg <daemon jvm arg>]... \
    [--out <file>] [--repeat N --first-run N [--before-run <shell>]] \
    [--tasks --extra-tooling-jar ../prototype/build/libs/ide-model-tasks.jar] [--dump-models <dir>]
```

- `--repeat N`: N syncs in one client JVM. Needed for configuration cache reuse: the entry key hashes the
  serialized build action, which carries the client classloader's UUID, so a new JVM per run can never hit.
  `--out` must then contain `{run}`; `--first-run 0` makes run 0 the warm-up. `--before-run` runs a shell command
  before each run with `$RUN` set (used for the incremental edit).
- `--tasks`: the task-based transport. Adds an init script that registers the model tasks (`IdeModelTasks.apply`),
  requests `IdeModelTaskRequest` per project in the projects-loaded phase (schedules the tasks), stops requesting the
  ported models (`PortedModels`) from builders and reads the bundle files instead. `--extra-tooling-jar` puts the
  prototype jar on IntelliJ's init-script classpath.
- `--dump-models <dir>`: writes every received model's serialized bytes to `<dir>/<class id>/<build id>/<project>.bin`
  for `../prototype/compare-models.sh` (per document) and `../prototype/compare-ordered.sh` (consumption order, for the
  two dependency models), plus `<dir>/order.txt`. The Gradle-owned `IdeaProject` / `KotlinDslScriptsModel` and their
  per-project task payloads are also written as text under `<dir>/gradle-owned/` for `../prototype/compare-gradle-owned.sh`.
- `--drop-arg-prefix P`: drops resolver-added arguments starting with P (e.g. the Kotlin correlation id).
- Every run's summary ends with `stall_max_gap_s`: the largest gap between the 10-second health checks of any daemon
  in the user home during the run; values far above 10 mean the whole machine stalled (LOG 2026-10-15).
- `IonText [--canonical] <file>...`: prints binary Ion model dumps as text (`java -cp "build/install/harness/lib/*" wbs.harness.IonText`).

- `run-scenarios.sh <label> [1 2 3 4 5]`: the scenarios of `design/measurement.md`, three timed runs each plus one
  traced profiling run, into `results/<label>/`.
- `aggregate_trace.py <trace>-log.txt`: per-model fetch cost from a build operation trace.
- `summarize.py results/<label>`: median/min/max table per scenario.
- `fixture/`: a 3-project Java build (CC + IP on) for smoke tests; run it with `--installation` pointing at the
  Gradle distribution to test.

The harness is a plain JVM, so it cannot validate against IntelliJ's `GRADLE_CALL` on this machine (no display);
that comparison is deferred to M5 (DECISIONS D1).

Do not run `installDist` while a harness JVM is running: the jar is replaced in place and the running JVM fails
with `NoClassDefFoundError` the next time it loads a class.
