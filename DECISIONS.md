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

### D6: refocus the comparison on the cache-miss and incremental cases (confirmed by the owner, 2026-10-15)

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
remaining open question. The owner confirmed (b) on 2026-10-15: "b is the most interesting case".
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

### D12: models without an Ion serializer go into the bundle Java-serialized, tagged as such

What: `ModelPayload` carries a format: `ion` for models with an IntelliJ `SerializationService`, `java` for the
rest (`DefaultDependencyAccessorsModel`, `VersionCatalogsModel`, `IntelliJSettings`, manifest, kapt, ...), which are
written with `ObjectOutputStream` and read back by the consumer with the IDE classes on its classloader.
Why: IntelliJ does the same today: `ToolingSerializerConverter.convert` catches
`SerializationServiceNotFoundException` and passes the object through, and the Tooling API Java-serializes it.
Writing new Ion serializers for ten tiny models buys nothing for the measurement. `design/task-contract.md`
("Java serialization is not used") is amended to "not used for models that have an Ion serializer".
Rejected: small Ion or JSON serializers now (deferred; a real implementation in the fork should add them).

### D13: the dependency model is captured at configuration time too; cross-project index effects are documented, not reimplemented

What: `GradleSourceSetDependencyModel`, `ProjectDependencies` and `KotlinGradleModel` join `PortedModels` and are
captured with their IntelliJ builders at task configuration time (D8). Before the dependency builder runs, the
capture runs `GradleSourceSetArtifactIndexBuilder` for the same project, so the shared index in the per-capture
context holds this project's own jar → source set entries. Entries of *other* projects are not available (IP), so
the parts of the model that depend on them (runtime output-dir file dependencies and artifact sources of project
dependencies, classes-dir → jar substitution) differ from the builder output; the comparison records how many
dependencies are affected and the report states that a complete port needs the consumer-side index processing
described in `design/dependency-task.md`, which is IDE-side transport work (M5) and does not change timings.
Why: LOG 2026-10-15, resolution cannot be deferred past the configuration cache store, so execution-time
resolution with declarative inputs (the original dependency-task design) is not implementable; what remains to
measure is the configuration-time capture against the builders, which the D8 pattern gives for the price of a
list entry. Rewriting the 800-line resolver on the `ResolutionResult` API to run at execution would resolve at
store time anyway.
Rejected: a star dependency of every `ideDependencyModel` on all `ideProjectModel` tasks to read the other
projects' index files at execution (allowed by the contract, but the data it needs is produced at the same
configuration time and the coupling is exactly what IP forbids in spirit).

### D14: Gradle-owned models are in scope for the task version; measure the all-tasks floor first

What: the owner widened the scope on 2026-10-15: `IdeaProject` and `KotlinDslScriptsModel` (and anything else
that forces full configuration) are to become tasks too, so that a sync is a pure task run and only the affected
projects are configured on an incremental re-sync. Step 1 (now): a harness mode `--tasks-only` that requests no
builder model at all and runs only the model tasks, to measure the floor of the task version for the miss, hit,
incremental and full-invalidation scenarios. Step 2: per-project substitutes for the two models (module content
and dependencies are already in the ported models; the script classpath per project is new) and the IDE-side
assembly, which is a resolver change and goes into M5.
Why: with any build-level builder left, every incremental sync pays full configuration plus those builders, which
is exactly the cost the thesis wants to remove; the floor tells whether the IDE-side rewrite is worth it before it
is written. The "resolvers untouched" rule of README.md is relaxed for `IdeaProject` by the owner.

### D15: M4 gate margin and what counts

What: fixed before reading `results/m4-summary.md`. The task version "beats" the builders when, for the same
scenario and the same client-JVM conditions, its median total is at least 30 % lower (design/plan.md's bar) on the
scenarios where builders do work: scenario 2 (warm plain, CC and IP off), the warm miss (scenario 3's traced run,
fresh JVM) and scenario 4 (edit). Scenario 3 (hit) and scenario 1 (cold) are reported but expected ties: the hit is
the whole-action cache in both versions and the cold run is dominated by daemon start and configuration. The three
task variants are judged separately: `tasks` (ported models from bundles, Gradle-owned models still from builders)
is the state an unchanged IDE could reach; `tasks-only` and `launcher` are the floor and the transport the owner
asked for (D14), which need the IDE-side changes in M5.
Why: the gate must not be tuned to the result; 30 % is the plan's number, and the scenario list follows D6.

### D16: one builder context per build, filled per project when it is evaluated (supersedes the "entries of other projects are not available" part of D13)

What: the captures share one IntelliJ `ModelBuilderContext` per build invocation, held by a shared build service
of the root build (`SharedBuilderContext`), and every project's `GradleSourceSetArtifactBuildRequest` runs from
`gradle.lifecycle.afterProject` into that context. The capture order no longer contains the request. With this the
ordered comparison of `GradleSourceSetDependencyModel` is 287/287 identical (`results/m3-ports/compare-hard.md`).
Why: the M4 ordered comparison showed what the missing index costs: compile-classpath artifacts of project
dependencies stay classes directories instead of being folded into the jar entry (COMPILE split into PROVIDED +
RUNTIME), `projectDependencyArtifactsSources` empty, the runtime output-dir file dependencies missing: 264 of 287
projects differed. IntelliJ fills the index for all projects before any dependency model and reads other projects'
`SourceSet` objects from it; the task version does the same, with Gradle giving the order: resolving a project
dependency inside a capture evaluates the target project first, so its `afterProject` entries exist when the
capture looks them up. A map keyed by `Gradle` did not work: under Isolated Projects every project sees its own
`Gradle` wrapper (`project.getGradle()`), so the key must be the build's shared service. A static map would also
have kept the whole build model alive in the daemon; the service is discarded with the build.
Contract: this is the cross-project read the builders do today (`design/task-contract.md`, "a task reads only its
own project's state"); it happens at configuration time through IntelliJ's context, not through the Project API,
and Isolated Projects reports no problem for it on gradle/gradle (the entry is stored and reused). It is recorded
as a deviation that an IDE-side port would keep until the artifact index becomes a file of its own.
Rejected: `project.getRootProject()`/`getGradle().getRootProject()` as key (IP violation); a per-capture context
(D13's state, the measured 264 differences); a star task dependency on all `ideProjectModel` tasks (D13).

### D17: the Gradle-owned build models become per-project payloads, assembled by the consumer (D14 step 2)

What: two more entries in every project's bundle. `IdeaModulePayload` carries what IntelliJ reads from an
`IdeaModule` and its `GradleProject` (names, dirs, build script, content root with source/test/resource/exclude
dirs, compiler output, the project's source and target compatibility); the root project's payload adds the
build-level fields (project name, current JDK). `KotlinDslScriptPayload` is the model of the project's own
`build.gradle.kts` (classpath, source path, implicit imports, editor reports, exceptions), produced by Gradle's own
single-script builder `KotlinBuildScriptModelBuilder.kotlinBuildScriptModelFor` called reflectively (internal API).
In task mode the harness no longer requests `IdeaProject` or `KotlinDslScriptsModel` from builders. The consumer
derives the build-level `IdeaProject` values the way the idea plugin does (language level and bytecode version =
maximum over the modules, a module carries its own only when it differs), which the harness does when it writes
the comparison text (`GradleOwnedText.finish`); in the IDE this is the M5 resolver change.
Why: the idea plugin cannot be applied from project code under Isolated Projects (its apply reaches the root
project and all projects for module-name de-duplication and the project-level levels; Gradle's `IdeaModelBuilder`
applies it with privileged access to every project), so the payload mirrors the plugin's defaults from the
project's own java plugin state instead. Consequence: `idea { module { } }` customizations are not seen; module
names are the project names (no de-duplication). The settings script, init scripts and precompiled script plugins
of `KotlinDslScriptsModel` stay with the builder (root build, sub-second).
Measured: `results/m3-ports/compare-gradle-owned.md`; timings `results/m4d-*/` (`tasks` variant now without the two
Gradle-owned builders, `tasks-only` and `launcher` with the two extra captures).
Amended 2026-10-16 after the gradle/gradle comparison: (1) when the project applied the idea plugin itself (gradle/gradle
does, through `gradlebuild.ide`), the payload reads `idea.module` as configured, customizations included, and only
mirrors the plugin's defaults otherwise; the language levels are always the raw compatibility values because their
conventions compare all projects. `plugins.withType(IdeaPlugin)` customizations of a build that does not apply the
plugin still never run. (2) The Kotlin DSL payload carries every script the `KotlinDslScriptsModel` builder
attributes to the project: the build script and the precompiled script plugins of a `kotlin-dsl` project, plus the
settings script and the init scripts on the root project. Nothing stays with the builder. (3) The captures compute
the build-wide stage-1 accessors classpath once from the root project's `afterProject`
(`Stage1BlocksAccessorClassPathGenerator.prepareForParallelAccess`, what Gradle's own Isolated-Projects-safe scripts
model builder does before it fans out); without it parallel captures deadlock (LOG 2026-10-16).
(4) Added later the same day: the hook also resolves the Gradle distribution sources once
(`SourceDistributionResolver(rootProject).sourceDirs()`, a build-tree-scoped cache whose lock is held during a
network lookup on services.gradle.org, ~10 s on a slow connection); without it every capture thread queued behind
that lookup and the configuration cache store stalled (LOG 2026-10-16, attribution). The lookup itself is Gradle's
and is paid once per sync in both worlds; a persistent per-version cache in Gradle would remove it.

### D18: the benchmark distribution gets a `src` directory; no sync in the measurement looks up Gradle sources online

What: `~/.gradle-wbs/wrapper/dists/.../gradle-9.9.0-milestone-2/src/` exists (empty but for a README). Gradle's
Kotlin DSL script model takes the Gradle source roots from an existing installation `src` directory and only
otherwise resolves the distribution sources from services.gradle.org (`SourcePathProvider.sourceRootsOf`). All
timings from `results/m4g-*` on are taken this way; `m4-*` to `m4f-*` include the lookup.
Why: the lookup is a network round trip per sync (build-tree cache, lock held during the request), 8–16 s on the
owner's connection on 2026-10-16, and it fails anyway for gradle/gradle's snapshot version. It made the Kotlin DSL
script model the slowest part of both the builder sync (most of the 7.3 s `KotlinDslScriptsModel` builder in M4)
and the task sync (every capture thread queued behind it, LOG 2026-10-16), and it put the day's connection
quality into every number. The `-all` distribution, which the IDE recommends for Kotlin DSL builds, has a `src`
directory and never makes the request, so this is the common IDE configuration, not a shortcut.
Consequence: the per-project source path lacks the Gradle sources (same in both worlds); the fidelity comparison
of the script models is unaffected (both dumps were taken before). The warm-up of the lookup from the root project's
`afterProject` (D17 amendment 4) stays: harmless with the directory, and it keeps the store from stalling without it.

### D19: model payloads are stored deflated

What: `ModelPayload` deflates the serialized model bytes at capture (`Deflater.BEST_SPEED`) and inflates on read;
the bundle file carries the deflated bytes (new magic); the configuration cache entry holds the deflated field. The
fingerprint, the format and every consumer API (`getBytes()`) are unchanged.
Why: the models are mostly file paths that repeat across models and projects: the Kotlin DSL script payloads of
gradle/gradle hold 151,501 class and source path entries over 809 distinct paths (41 MB serialized, 24.9 MB of path
text), the dependency payloads 50 + 29 MB of the same kind. With them the configuration cache directory reached
389 MB per entry set and the no-change hit slowed from 1.6 s to 2.5–6 s (`m4e`); the bundles are also what the
launcher transport reads from disk and what the task world would otherwise send over the wire. Deflate is generic
(every model, no payload-specific format), cheap (a few hundred ms of CPU spread over the parallel captures) and keeps
the per-project independence that a shared path table would break (indices assigned in capture order would change
the fingerprints between runs).
Measured: `results/m4h-*` (same scenarios as `m4g-*`, which has the uncompressed payloads and the network-free setup).

### D20: the IDE runs headless here; M5 measures the real IDE with `idea.sh warmup` (supersedes D1's "cannot run")

What: IntelliJ's headless warm-up mode (`bin/idea.sh warmup --project-dir=<project>`, `java.awt.headless=true`,
config/system/log paths in the session scratchpad via `IDEA_VM_OPTIONS`) opens the project, runs the Gradle sync
through the Gradle plugin's `GradleWarmupConfigurator` (resolve stage, data import, final tasks), indexes, and exits.
Verified 2026-10-16 on the fixture: 17 s end to end, Gradle resolve stage 6.5 s, data import 0.3 s, configuration
cache entry stored, no display. `GRADLE_USER_HOME` and `JAVA_HOME` in the environment select the Gradle user home
(`~/.gradle-wbs`) and the Gradle JVM (`gradleJvm=#JAVA_HOME` in `.idea/gradle.xml`).
Why: D1 rejected a headless run for want of a sync entry point; the warm-up mode is that entry point. The IDE
baseline (`GRADLE_CALL`-level timings, data import) can now be measured here on gradle/gradle, and a modified Gradle
plugin can be measured the same way.
Consequence: M5 no longer waits for a display. The prototype's IDE side goes into a copy of the installed IDE as
patched plugin jars (compiled against the IDE's own jars, as the harness and prototype already are) until the fork
location is known; the fork then receives the same changes as source.

### D21: the IDE side is a small plugin plus one patched IDE class, run from a hard-linked copy of the installed IDE

What: `ide-plugin/` builds (a) the plugin `wbs-task-transport` (a `GradleProjectResolverExtension`: with
`-Dwbs.transport=tasks` it puts the prototype jar on the daemon's init script classpath, adds an init script that
applies `IdeModelTasks` with the IDE's own tooling-extension classpath (`GradleInitScriptUtil.GRADLE_TOOLING_EXTENSION_CLASSES`),
and contributes the request and file providers copied from the harness), and (b) `GradleModelFetchAction` from the
IntelliJ sources with a provider filter (`-Dwbs.transport.drop`, the list printed by `wbs.ide.DropList`). The
installer makes a hard-linked copy of the IDE, overlays the patched class into
`plugins/gradle/lib/gradle-tooling-extension-impl.jar`, and places the plugin in a custom plugins directory.
Why: dropping the built-in providers of the 19 ported models has no extension point; everything else does. The
installed IDE and the patched copy differ in one class and one plugin, so the comparison is clean, and the same
changes port to the fork as source once its location is known.
Verified on the fixture (headless warm-up): 4 model tasks, 4 bundles read, all import stages, import 3.5 s.
Pitfall met on the way: the model-tasks init script needs the IDE's whole tooling-extension classpath (Trove,
Commons, OpenTelemetry included), not the harness's shorter list; IntelliJ's serializer services fail to
instantiate otherwise and the configuration cache store fails with them.

### D22: the Gradle-owned build models are assembled in the transport's file provider, not in the IDE resolver

What: the plugin's file provider, which reads every project's bundle inside IntelliJ's build action, assembles each
build's `IdeaProject` from the `IdeaModulePayload`s and its `KotlinDslScriptsModel` from the `KotlinDslScriptPayload`s
(`IdeaProjectFromPayloads`, `KotlinDslScriptsModelFromPayloads` in the prototype: the Tooling API interfaces
implemented on plain records) and hands them to the IDE under those classes, as a build model and as a project model
of the build's root project respectively, exactly as the two built-in providers do. IntelliJ's Ion serializers
accept the implementations through the interfaces.
Why: the IDE's resolver and the Kotlin plugin consume `IdeaProject` and `KotlinDslScriptsModel` in many places;
reproducing the two objects is a few hundred lines in one place, changing the consumers is not, and the stock
resolver then proves the fidelity itself (fixture: module files identical). The same assembly can move to the
IDE side later when the bundles are read there (no action).
Consequence: all 26 built-in providers of the ported models are dropped (`DropList` without `--keep-gradle-owned`);
the action still runs for the eight small Kotlin models, `GradleBuildScriptClasspathModel` and the Kotlin
`prepare` tasks.

### D23: one Gradle invocation per sync; the IDE builds its model holder state from the bundles (M5 round 2c)

What: with `-Dwbs.transport.run=only` the sync is the plain task run of every project's `ideProjectModel` task (plus
the `GradleBuild` and `BuildEnvironment` requests the IDE makes anyway, sub-second and cache hits). IntelliJ's build
action does not run: the patched `GradleProjectResolver.doResolveProjectInfo` asks the transport plugin for a
`GradleModelHolderState` built from the bundles (`WbsTaskTransportResolverExtension.buildState()`: light builds from
`GradleBuild`, every payload deserialized with a fresh IntelliJ serializer and stored under its model id, the two
Gradle-owned models assembled per build) and adds it to the holder; everything after that is the stock resolver.
Why: the owner's target is one invocation, and the action misses the configuration cache on every IDE restart by
construction (per-process classloader id in its key) while the plain run hits; two invocations also configure the
build twice on a first import. Fidelity is unchanged because the holder sees the same models under the same ids.
Not yet served without the action: the Kotlin `prepare` task triggers (`PrepareKotlinIdeImportTaskModel`,
`KotlinDslScriptAdditionalTask`), to be requested as tasks in the plain run, and the MPP-only Kotlin models (null for
gradle/gradle).
Pitfalls recorded in LOG 2026-10-16: model class id format, Kotlin classes on the plugin's classloader, one fresh Ion
reader per document.


### D24: two IDE import classes patched against quadratic library lookups (owner's request, 2026-10-16)

What: the IDE copy also gets two platform classes overlaid into `lib/app.jar` (`ide-plugin/src/patched/java/...`,
`install-patched-ide.sh`, skip with `WBS_PATCH_IDE_IMPORT=0`):
- `AbstractIdeModifiableModelsProvider.findIdeLibrary` looks the library up by name first (`isRelated` is name
  equality and project library names are unique) instead of scanning `getLibraries()`; `getLibraryByName` is memoized
  for the provider's lifetime (one import), fed by `createLibrary` and cleared by `removeLibrary`.
- `LibraryDependencyDataService.findLibraryOrderEntry` finds the entry of a workspace-model library by comparing
  library ids (plain values) before falling back to the stock scan, which calls `LibraryOrderEntryBridge.getLibrary`
  (a resolution through the workspace storage) on every entry of the module for every lookup. A first attempt that
  skipped entries by name did nothing: the lookups are for unnamed module libraries (file dependencies; 103k of them
  for gradle/gradle, scanning 21.6 million entries), and both names are null there.
Why: the JFR profile of a restart import (LOG 2026-10-16) puts 28 % of the import thread in
`KotlinGradleLibraryDataService.postProcess` → `findIdeLibrary` → the library table bridge rebuilding its library
array (`librariesArrayValue`, invalidated by each `setKind` of the same loop: O(libraries²) with 577 libraries) and
another 28 % in `findLibraryOrderEntry` resolving every entry's library of a module for every module library.
Both fixes keep the stock semantics (same library returned, same entry found) and are candidates to report to
JetBrains. Scope: measurement only; the stock IDE is untouched and the fidelity diff guards the result.

### D25: task state stays the deflated model bytes; object graphs measured and kept as an option (2026-10-16)

What: the owner suggested holding the captured models in the task state as object graphs instead of IntelliJ-serialized
bytes, so the configuration cache's own codecs and string deduplication would remove the repetition the entry is made
of (paths and coordinates repeated per source set, per model and per project), with the task action serializing into
IntelliJ's format when it writes the bundle. Implemented (`CapturedModel`, `IdeModelTask.getModels()`,
`ModelCapture.captures()`/`serialize()`), measured on gradle/gradle with a warm daemon, tasks up to date:

| task state | store (miss) | entry | hit load |
|---|---|---|---|
| deflated bytes (default) | 6.3 / 6.7 s | 56 MB | 1.4–2.5 s |
| object graphs (D25 option, replaced by the per-model state form of D26) | 6.0 / 6.4 s | 68 MB | 1.9 s |

Fidelity identical (6321 of 6321 payload fingerprints), a deleted bundle is recreated from the loaded state in both
forms (fixture and gradle/gradle `:core`), no configuration cache problems.
Why the bytes stay: the store time is the capture (dependency resolution and task realization for the models), not the
serialization, so neither form is faster; and the entry grew by 21 %. The investigation of the codecs
(`results/m5-ide/cc-serialization-NOTES.md`) corrected the first reading: the parallel store shares one string table
across all project files (7.6 MB of distinct strings for the whole entry), so strings are deduplicated; what costs is
the per-object structure the bean codec writes for every dependency record (~50 bytes: tags, identity and class ids,
one tag per field), 57 MB over 287 project files, while deflate inside a payload also compresses the repeated
structure. Nothing in the codec chain compresses and the files are encrypted. Remaining size levers: a compact record
format of our own expanded in the task action (estimate 56 → 20–25 MB), and dropping `ProjectDependencies` (28 % of
the bytes, used only by the Dependency Analyzer) from the sync.
Also found: a changed init-script classpath jar does not invalidate the entry (the first run after the rebuild reused
the old entry, with the old task state under the new class); recorded for Gradle.

### D26: the dependency model travels as a compact record format; state form per model class (2026-10-17)

What: `GradleSourceSetDependencyModel` is captured into `CompactDependencies`, a per-project string table
(`List<String>`) plus one varint record per dependency (`byte[]`), stored in that form in the task state and in the
bundle (entry format `compact-deps`), and expanded into IntelliJ's model objects by the consumer
(`ModelPayload.readModel`, the one reader for every format). The task state is one list of `CapturedModel`s whose
state form is chosen per model class: compact records for the dependency model, the object itself for
`KotlinDslScriptPayload` (lists of paths, which the configuration cache stores as references into its string table),
deflated serialized bytes for the rest. `-PideModelTasks.compact=false` gives the all-bytes state; the D25
`state=objects` option is removed. Measured on gradle/gradle (`results/m5-ide/compact-NOTES.md`):

| | compact | bytes |
|---|---|---|
| configuration cache entry | **39 MB** | 56 MB |
| bundles on disk | **43.1 MB** | 51.9 MB |
| dependency model, all bundles / `:core` | 7.1 MB / 36 KB | 12.7 MB / 101 KB |
| store (warm daemon) | 7.2 s | 6.7 s |
| hit: entry load | 0.22 s | 0.18 s |
| IDE restart import (2 hits each) | 27.7, 28.3 s | 27.9, 29.7 s |

Fidelity: identical canonical text for 287 of 287 projects against Ion bundles; unit test; a deleted bundle is
recreated from the compact state.
Why: the owner asked to try the compact format and whether the bundle format matters (JSON, Ion). The offline estimate
on `:core` settled the format question first: the encoding is worth ~10 % (JSON 89 KB vs Ion 101 KB deflated), the data
model 3× (table plus references, 33 KB). The format is kept because it is a proven size win with no IDE-side change
outside our plugin, and because it makes the entry's size independent of IntelliJ's per-record object shape.
What it does not do: shorten anything measurable on this machine. The hit's load is 0.2 s in both forms, the import
times overlap, the store is the capture. The entry size is a lever for slow disks and small heaps, not for these
numbers. `ProjectDependencies` does not compact (−16 % on `:core`: the graph is the content); its lever remains
dropping it from the eager sync.

### D27: the Mac kit targets IntelliJ IDEA 2026.2.2 (262.10315.125) with per-branch patched sources (2026-10-17)

What: the owner asked for a distribution to try on a Mac, based on build 262.10315.125 (IntelliJ IDEA 2026.2.2), with
a distinguishable icon. A from-source IDE build was ruled out earlier (40–50 GB); a modified official DMG cannot be
produced on Linux. The kit therefore patches a copy of the officially installed app on the Mac: `install-mac.sh` copies
the app, overlays the four patched classes into their jars (found by content: the 2026.2 layout renamed
`plugins/gradle/lib/gradle.jar` to `plugins/gradle-plugin/lib/intellij.gradle.jar` and split `lib/app.jar`), adds the
plugin as a bundled plugin, appends the `-Dwbs.*` options and a paths selector (`IntelliJIdea2026.2-WBS`, own settings
and caches), renames it "IntelliJ IDEA WBS" with a WBS-badged icon (the IDE's SVG plus a badge, rendered by macOS
QuickLook, packed by `iconutil`) and re-signs ad hoc. Architecture-independent (Java classes only). Repo changes: the
`patched` source set selects `src/patched/java` (253) or `src/patched-<branch>/java` (`-PideaBranch=262`, Java 25),
`ideJars()` covers both plugin directories, `install-patched-ide.sh` finds jars by content, the plugin implements the
now abstract `GradleModelConsumer.consumeBuildModel` and requests `GradleDslBaseScriptModel` on the plain-run
connection when the IDE has the holder class (2026.2's Kotlin plugin reads it from the holder state; 2025.3 neither
has nor needs it). Verified headless on this machine: restart hit import 31.6 s on 2026.2.2 (27.7–29.7 s on 2025.3).
Why: the only deliverable that fits the disk and the owner's machine; the class overlay is what the Linux chains have
run since D21. Not verified: a real Mac run (signing, icon rendering, Kotlin script support with the fetched base model).

### D28: resilience is out of scope for the prototype (owner, 2026-10-17)

What: the sync's behaviour on a broken build (partial models from a failing settings or build script, which Gradle's
`GradleBuildBuilder` and the resilient model controllers provide for the stock sync) is not a goal here. A task run
that fails whole is acceptable; resilient task runs are something to add to Gradle later. Consequence: the
`ideBuildTree` design (`results/m5-ide/gradlebuild-as-task-NOTES.md`) needs no fallback to the `GradleBuild` model
request, and no prototype work goes into error recovery.
Why: owner's statement when the fallback was listed as a risk ("resiliency is not a goal for this prototype. We can
add task resiliency later to Gradle").

### D29: the build structure and the base-script model come from a root task; the sync is one Gradle operation (2026-10-17)

What: `ideBuildTree` in the root project of every build writes a `BuildTreePayload` (settings descriptor tree,
included builds, identity path, and for the root build Gradle's `GradleDslBaseScriptModel` produced by Gradle's own
builder); the aggregate `ideModels` depends on it, on every project's `ideProjectModel` by path and on the included
builds' aggregates. With `-Dwbs.transport.tree=task` the plugin runs `:ideModels` only, resolves projects for the
streaming reads from the tree bundles, synthesizes the Tooling API `GradleBuild` view with Gradle's model POJOs and
`ProtocolToModelAdapter`, and hands the base-script model to the IDE's holder as a proxy. Kept as an option, not the
default (`results/m5-ide/buildtree-262-NOTES.md`).
Why: the owner asked to implement it and measure. Fidelity is identical (1370/1370 storage files). The restart hit on
2026.2.2 is unchanged within noise (model 30.7 / 31.3 s vs tree 31.9 / 32.2 s, cold / warm daemon; Gradle side
10.0–10.5 s everywhere): the first operation on the connection costs about 1.8 s whichever it is, and the two removed
requests cost 0.3 s and 0.08 s after it. The forced miss shows the same (55.6 vs 54.6 s medians). What it does buy: one Gradle operation per sync (simpler, and the included
builds are addressed by their real names instead of directory names), at the price of a failing task run on a broken
settings script instead of Gradle's partial `GradleBuild` (accepted, D28). Isolated Projects accepted the string-path
`dependsOn` across projects and `includedBuild(name).task()`. Two IP/Gradle facts learned: a project's `Gradle` object
is not the settings' instance (key per-build state by identity path), and Gradle's base-script model classes are
package-private (reflection needs the accessibility flag).

### D30: the bundle state is delivered through the IDE's fetch-result bridge; script phases on by default (2026-10-17)

What: the owner's Mac showed "Code insight is unavailable (Script Configurations not loaded)" in `.gradle.kts` files.
Two causes: the assembled `KotlinDslScriptsModel` was registered under the root project's model id while the Kotlin
plugin reads it as a build model (`getBuildModel`/`getRootModel`); and the single-invocation mode replaced the build
action with `models.addState(state)`, so the IDE's `GradleModelFetchActionListener` (created by
`GradleSyncProjectConfigurator.createModelFetchResultHandler`) never ran and with it none of the per-phase sync
contributors, which in 2026.2 include the Kotlin DSL base-script and script-model contributors. The patched resolver now
feeds the state through `GradleModelFetchActionResultHandlerBridge` as the action's streamed results would (first phase
with all models, the others empty), under `-Dwbs.transport.phases`: `scripts` (default: only the two script phases),
`all`, `none`. Measured on this machine (gradle/gradle, 2026.2.2): `all` turned the data import's library dependency and
project repository steps from 4.4 / 2.7 s into 24 / 23 s (the contributors populate the workspace model a second time);
`scripts` keeps the import at 15 s; the Kotlin script contributor costs 23 s on a fresh IDE session and 6 s on a later
re-sync with a warm daemon (import 52.6 vs 33.9 s, re-sync 42.5 vs 34.9 s), 44-62 s when the IDE's warm-up indexing
runs alongside (JFR: `GradleKotlinScriptEntityProvider` → workspace `VirtualFileIndex` updates and `MutableWorkspaceSet` copies
for the scripts' classpath URLs, plus read-lock waits), work that a stock sync overlaps with its 40 s build action.
Why: correctness first for the kit (the owner uses Kotlin scripts); the cost is documented and switchable. Next lever:
deliver the script phase in the background after the sync, or make the script entities cheaper (IDE-side: one shared
classpath per build instead of per script).
