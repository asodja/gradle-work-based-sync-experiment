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

### D6: refocus the comparison on the cache-miss and incremental cases (assumption while the owner question is open)

What: scenario 3 (warm, no change, IP on) is kept as a scenario but is no longer the headline: with Gradle 9.9
under IP, a no-change re-sync in the same client process is served whole from the configuration cache entry
(gradle/gradle: 1–2 s, no builder runs; LOG 2026-10-15). The comparison that can still show a difference is
(a) the warm miss (first sync in a new client JVM, i.e. after an IDE restart, or any fingerprint invalidation):
builders rebuild every model (~21 s warm on gradle/gradle) while tasks would only re-run work whose inputs
changed, since task history survives a configuration cache miss; and (b) incremental re-sync after one build
script edit (scenario 4), where builders without the opt-in per-model cache rebuild everything, builders with
`-Dorg.gradle.internal.isolated-projects.caching=tooling` rebuild the affected projects' models (new scenario 6:
incremental + model cache), and tasks re-run the affected projects' tasks. The M4 gate margin will be defined
against scenarios 4 and 6 and against the warm miss, not against scenario 3.
Why: the plan's premise that builders re-run on every warm sync is false for the no-change case; the
experiment can still answer whether tasks beat the opt-in per-model cache on invalidation, which is the
remaining open question. The owner is asked to confirm (PROGRESS.md, "Blocked on owner").
Rejected: stopping at the M0 gate (the gate's criterion is met on a miss, 48 s summed / ~14 s wall of
per-project IntelliJ models; it is only moot on a hit).

### D7: the Gradle-side prototype lives in `prototype/` of this repo, not in an IntelliJ fork, until M5

What: `prototype/` is a small Gradle build that compiles the model tasks, the request model builder and the
comparison tests against the installed IDE's `gradle-tooling-extension-{api,impl}.jar`, `kotlin-gradle-tooling.jar`
and the Gradle API of the measured distribution (its wrapper is 9.9.0-milestone-2, same as the benchmark). The
harness puts the prototype jar on IntelliJ's init-script classpath next to the IDE jars. Packages are kept as they
would be in `plugins/gradle/tooling-extension-impl` so the code moves into the fork for M5 unchanged.
Why: no `intellij-community` fork exists under the owner's GitHub account (checked 2026-10-15), a shallow clone
plus a Bazel plugin build costs 5–8 GB and an unverified build on aarch64 with 16 GB free, and M2–M4 only need
the daemon-side classes. The `port-model` subagent brief ("commit on the fork branch") is read as "commit in
`prototype/` on `main`" until the fork exists.
Rejected: cloning JetBrains/intellij-community now (needed only for M5 and for IDE-side transport).

### D8: cheap models are captured at task configuration time with the existing builder code; only expensive models do work at execution

What: a model task's configuration action (run lazily when the task is realized for the graph, i.e. after all
projects are configured, the same moment builders run today) calls the IntelliJ builder (`buildAll`) with a
minimal `ModelBuilderContext`, serializes the result with IntelliJ's `SerializationService` and stores the bytes as
an `@Input`. The task action only writes the bytes to the output file. This covers `GradleSourceSetModel` (M2) and
the whole mechanical group (M3). Expensive models whose cost is the work itself, `GradleSourceSetDependencyModel`
and `KotlinGradleModel`, capture declarative inputs at configuration (dependency notations, repositories, source
set names, paths) and resolve at execution, so a reconfiguration that leaves those inputs unchanged (comment edit,
build-logic edit) skips the resolution.
Why: the M0 profile (`results/m0-baseline/models.md`) shows the per-project cost is concentrated in the
dependency model (30 s summed) and `KotlinGradleModel` (6 s); the source set model costs 18 ms per project on
average. Reusing the builder code makes those ports mechanical and the comparison test exact. The task approach
can only beat IP's own caches where execution-time work is skipped on reconfiguration, so that is where the
engineering goes.
Rejected: `@Nested` input beans for the source set model (more code, same fingerprint quality, no measurable
gain); execution-time recomputation of the source set model (needs CC-serializable captures of `Jar` task
internals, which the contract forbids).

### D9: task grouping: `ideProjectModel` and `ideDependencyModel` per project, bundle files

What: two tasks per project. `ideProjectModel` bundles every cheap model of the project (M2: `GradleSourceSetModel`
only; M3 adds `ExternalProject` and the mechanical group) into `<build dir>/ide/models/ideProjectModel.bin`;
`ideDependencyModel` writes the dependency model (and, as part of it, this project's artifact-index entries:
jar path → source set name and output dirs) into `ideDependencyModel.bin`. Bundle format: count, then per entry
model class name, byte length, bytes. The shared `GradleSourceSetArtifactIndex` is not a file another task reads:
the consumer (harness/IDE, build-finished phase) sees every project's entries and does the cross-project lookup
that `GradleSourceSetDependencyResolver.resolveSourceSetOutputDirsRuntimeFileDependencies` does today.
Why: input overlap (cheap models change together with the build script), failure isolation (a dependency
resolution failure must not lose the source set model), incrementality (dependencies are the separately
invalidated part), and 2 × 254 tasks instead of 15 × 254. Consumer-side lookup keeps tasks free of
cross-project inputs, which IP forbids anyway.
Rejected: one task per model (task count, 15 snapshots of the same project); one task per project (a resolution
failure would lose everything); a model-task-on-model-task dependency for the index (cross-project coupling).

### D10: scenario 6 is measured later on a distribution built from the pinned commit with the codec fix

What: the builder-side incremental competitor (scenario 6) is not measurable on stock 9.9.0-milestone-2 (Gradle
bug, LOG 2026-10-15). Before M4, build `binDist` from `dedc5d14f2d` plus the two-line fix in
`ImmutableAttributesSchemaCodec`/`DefaultCompatibilityRuleChain` (and the disambiguation chain), point the benchmark
worktree's wrapper at it (`design/building.md`) and rerun all scenarios for both versions on that distribution so
the M4 comparison is same-distribution. Until then the M2 and M3 work continues on milestone-2. The owner is asked
to file the bug upstream (PROGRESS.md), since it affects everyone who turns the model cache on.
Why: the fix only changes which instantiator rebuilds a rule after a cache load, not what is cached or how much
work runs, so timings are unaffected; mixing two patched jars into the milestone-2 distribution would mix
commits. Measuring only on the fixture would leave the central competitor unmeasured on the benchmark build.
Rejected: an init-script consumer rule that pre-empts KGP's rule (changes the measured build, fragile); dropping
scenario 6.

### D11: the mechanical M3 ports are entries in `PortedModels`, done by the lead session

What: with D8 a mechanical port is one line in `PortedModels.PROJECT_MODEL_TASK` plus a comparison run; the
`port-model` subagent (Opus, one model per invocation, its own commit and LOG entry) is not used for them.
Comparison results are recorded per model in LOG.md in one entry for the group.
Why: the policy in `design/models-and-effort.md` assumed each port is a rewrite of a builder; it is not any
more. Spawning 13 agents for 13 one-line edits would cost more than the edits and fragment the comparison.
The hard group (`GradleSourceSetDependencyModel`, `KotlinGradleModel`, `ProjectDependencies`) stays with the
lead session as planned.
