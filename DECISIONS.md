# Decisions

One entry per decision: what, why, alternatives rejected. Decisions already made before this repo existed
are in `design/context.md` under "Decisions already made".

(none yet)

## 2026-10-15

### D1: M0 numbers come from the harness, not from an installed IDE

What: the IDE sync of M0 is replaced by the M1 harness; M0 and M1 are done together. IDE phase timings
(`GRADLE_CALL`, `PROJECT_RESOLVERS`) are deferred to M5 or to the owner on a machine with a display.
Why: this machine has no display and no Xvfb, so IntelliJ cannot run here. The harness runs the same
`GradleModelFetchAction` with the same providers, so the model request profile is the same.
Rejected: installing Xvfb (no root), running IntelliJ headless (no supported headless sync entry point).

### D2: measure against the wrapper's own distribution (9.9.0-milestone-2), not a `binDist` build

What: the benchmark worktree is synced with the distribution its wrapper pins, `gradle-9.9.0-milestone-2`,
already in `~/.gradle/wrapper/dists`. No Gradle distribution is built unless the Gradle fork needs a change.
Why: that is what the wrapper, and therefore every IDE user of gradle/gradle, syncs with; build-logic
compiles against it. Building `binDist` costs tens of minutes and 2 GB for no measurement gain, and the
request logger it would carry is replaced by the build operation trace (D3). `design/measurement.md`'s
"distribution from the same commit" control is relaxed accordingly.
Rejected: `binDist` from `dedc5d14f2d` (kept as the fallback if a Gradle-side change becomes necessary).

### D3: per-model costs from the build operation trace, not from a patched `DefaultBuildTreeModelCreator`

What: a dedicated profiling run per scenario passes `-Dorg.gradle.internal.operations.trace=<path>` as a
build argument (it is an `InternalOption`, so no daemon JVM arg change) and the harness aggregates the
"Fetch model '<name>' for <target>" operations. Timed runs are untraced.
Why: works with a stock distribution (D2). The fetch-model operation has no details class, so the trace
filter cannot select it and the full trace is too heavy to leave on during timed runs.

### D4: separate Gradle user home `~/.gradle-wbs` for the benchmark

What: all benchmark invocations use `-g ~/.gradle-wbs`; the wrapper distribution and dependencies are
downloaded into it once.
Why: `~/.gradle` is shared with other sessions; `gradle --stop` in scenario 1 would kill their daemons and
theirs would kill ours. Dependency downloads cost one cold run.

### D5: IntelliJ 2025.3 (branch 253, build 253.28294.334) is the IDE version for jars and the fork

What: tooling jars come from `~/tools/ideaIC/` (IDEA 2025.3 for Linux aarch64); the fork branch for M3 is
based on the matching 253 branch so built plugin jars can be dropped into this installation.
Why: it is the current release; `design/context.md` was gathered from master, so paths may differ and are
verified against the jars before use.
