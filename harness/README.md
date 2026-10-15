# Harness

Tooling API client that runs IntelliJ's real `GradleModelFetchAction` with the providers the IDE's resolvers
register, loaded from an installed IntelliJ IDEA (`plugins/gradle/lib`, `plugins/gradle-java/lib`,
`plugins/Kotlin/lib`, `lib`). Requirements: `design/measurement.md` ("Harness").

```
./gradlew installDist -PideaHome=~/tools/ideaIC/idea-<version>
build/install/harness/bin/harness --project <build> --gradle-user-home <dir> --idea-home <idea> \
    [--installation <gradle dist>] [--arg <gradle arg>]... [--out <file>]
```

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
