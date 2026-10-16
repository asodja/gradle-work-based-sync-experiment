# Gradle-side phases of a sync: build operation traces, warm daemon (2026-10-16, 20:37–20:40)

Owner's question: how much of the Gradle part is task graph calculation (to be parallelized), and how much more can
the Gradle part be accelerated. Harness runs on gradle/gradle (287 projects) with the daemon warm (one untraced
warm-up run first), each a configuration cache miss in a fresh client JVM, `-Dorg.gradle.internal.operations.trace`;
`trace-phases.py` extracts the build operations (`phases/*-phases.txt`). Times are wall clock inside the daemon.

| Phase (wall) | stock action (builders) | plain task run (launcher / tasks-only) | launcher, hit |
|---|---|---|---|
| Load build + settings, plugin builds | ~0.3 s + ~1.0 s | 0.3 s + ~1.1 s | – |
| **Configure build** (254 projects, parallel) | 4.5–5.8 s | 5.3–5.4 s (27.5 s summed, ~7× parallel) | – |
| of which `Notify projectsEvaluated listeners` (sequential) | 0.7 s | 1.0–1.2 s | – |
| **Calculate build tree task graph** | 9 ms | **55–62 ms** (+ 0.05–0.12 s per plugin build during configuration) | – |
| Model builders (`Run nested tooling build actions`, parallel) | ~4.2 s wall, 35.0 s summed, 2009 fetches | – | – |
| **Store configuration cache state** (287 projects, parallel) | 9 ms | **6.1–6.8 s wall, 52–57 s summed** | – |
| Run main tasks (287 model tasks up to date) | – | 0.09 s | 0.08 s |
| Check fingerprint + load cache state | – | – | 0.45–0.5 s + 1.4–1.6 s |
| **Build total** | 11.9 / 14.2 s | 13.0 / 14.7 / 15.9 s | 2.1 / 3.4 s |

Task graph calculation is **about 0.06 s** for the 287 model tasks, plus ~0.3 s spread over the `build-logic*`
plugin builds while settings are evaluated. Parallelizing it gains nothing for a sync of this shape. Nothing
sequential of size is left in the miss: configuration and the store both run on all cores; the sequential remainder
is settings evaluation with the plugin builds (~1.3 s), `projectsEvaluated` listeners (0.7–1.2 s; the prototype's
stage-1 accessor warm-up is part of the task-world figure) and the Develocity build-finished hook (0.2 s).

What the store does (children of `Storing configuration for <project>`, summed over 287 projects): `Resolve
dependencies` 20.0 s (3417 resolutions), `Realize task` 5.4 s (36,452 tasks), resolve classpath 1.8 s, resolve files
1.7 s. The model tasks' input fingerprints are computed from the captured models, so on a miss the capture work
happens inside the store, together with the dependency resolution the models need. That is why a warm-daemon miss is
not faster with tasks than with builders here (14.7 vs 11.9–14.2 s): the builders' 35 s of summed model building
becomes 52 s of summed store work. The tasks win on the hit (2.1–3.4 s against a stock miss every time) and, in the
IDE chains with a cold daemon, also on the miss (40 vs 52–63 s), where the builders' code suffers more from the cold
JIT than the configuration path does.

Where the Gradle side can still move:
1. **Daemon warmth is worth 2×.** The same plain run is 27–29 s with a cold daemon (IDE chains, daemon stopped
   before each run) and 13–15 s warm. A JIT/class-data cache for the daemon (CDS, Leyden-style) would halve every
   miss after a daemon restart; nothing else in the list is that large.
2. **Per-project invalidation on a build script edit** (Isolated Projects): would cut the 5.4 s configure and most of
   the 6 s store to the affected projects. Today a script edit invalidates the whole entry.
3. **The store itself**: 6 s wall on a miss, more than configuration; dominated by dependency resolution for the
   model inputs. Candidates: keep resolution results across entries, or hand the result to the client before the
   store finishes (the IDE waits for the build to end).
4. **The hit**: 2.1–3.4 s = fingerprint check 0.5 s + state load 1.5 s + run 0.1 s. The fingerprint check scales with
   the number of inputs; the load with the entry size (56 MB here).
5. Not worth it: task graph calculation (0.06 s), model request round trips (`GradleBuild` 0.25–0.35 s warm).

## Why the store costs 6 s: baseline runs without the model tasks (20:48–20:50)

| Run (warm daemon, miss) | configure | store configuration cache state | entry size | total |
|---|---|---|---|---|
| `help` (root project only) | 7.1 s | **18 ms** | – | 7 s |
| a no-op task registered in every project (`wbsNoop`, 287 tasks, init script) | 11.6 s (settings script recompiled after the init-script change, `Load build` 3 s) | **31 ms** | **1.7 MB** (2.7 MB in 4 KB blocks) | 15 s |
| the 287 `ideProjectModel` tasks | 5.3–5.4 s | **6.1–6.8 s** (52–57 s summed) | **56 MB** | 13–16 s |

Storing gradle/gradle's configured state with a task in every project costs 31 ms and 1.7 MB, of which the build
fingerprint (the configuration inputs: scripts, files read, properties) is 1.28 MB, the string table 0.21 MB, the
project fingerprint 0.09 MB, the root work graph 0.05 MB and the 254 per-project work files 37 KB together (146 B
each: one task node without inputs). The 3 MB quoted before was `du` block rounding. The 6 s and 56 MB are the
model tasks: their `@Input` fingerprints are computed from the captured models, so the capture runs inside the store
(dependency resolution 20 s summed over 3417 configurations, 36k task realizations 5.4 s, classpath and file
resolution 3.5 s: the same work the IntelliJ builders do, 35 s summed in the stock action), and the payloads
themselves are task state, so the 56 MB of deflated models are serialized into the entry on top of it (the remaining
~23 s summed). Disabling the configuration cache would not remove the capture (it would run at task execution, 8.6 s
wall when all 287 tasks execute), would lose the hit (2–3 s → 13–15 s on every sync) and would lose Isolated Projects'
parallel configuration. What would shrink the store: keep the payloads out of the entry (write the bundle at capture
time, leave only the fingerprint as task state: saves the serialization share, up to ~2–3 s wall, entry a few MB);
cheaper models (`GradleSourceSetDependencyModel` is 59 % of the capture work; an IDE-side contract change); and, on
Gradle's side, resolution results shared across entries or a store that completes after the client has its result.

## Why the entries are big: the models repeat the same information per project (20:55)

All 287 bundles: 6321 payloads, **202 MB inflated, 48.8 MB deflated** (the 56 MB entry is these plus ~3 MB of task
graph state). By model class (inflated / deflated per payload / xz over all payloads of the class together, which
removes repetition across projects):

| Model | inflated | deflated per payload (as stored) | xz across projects |
|---|---|---|---|
| GradleSourceSetDependencyModel | 80.2 MB | 12.7 MB | 1.6 MB |
| ProjectDependencies | 47.0 MB | 11.7 MB | 5.1 MB |
| KotlinDslScriptPayload (build script classpath + source path per script) | 40.3 MB | 9.1 MB | **0.05 MB** |
| GradleBuildScriptClasspathModel (the same classpath again) | 14.3 MB | 3.3 MB | **0.02 MB** |
| GradleTaskModel | 8.6 MB | 1.7 MB | 0.2 MB |
| all others together | ~11 MB | ~3 MB | – |

Reading: a quarter of the stored bytes is the build script classpath (several hundred jar paths) and source path,
repeated in two models for each of the 287 projects although it is the same for nearly all of them (two projects'
payloads differ only in the script paths). The dependency models carry one record per dependency per source set per
scope, with artifact, sources and javadoc paths, and the same artifacts recur in most projects; per project they are
286 KB and 177 KB on average. IntelliJ's Ion serialization already shares strings within one document, deflate takes
each payload down 4–5×, but neither sees across payloads (deflate's window is 32 KB, a preset dictionary did not help).
The stock sync streams the same 200 MB through the Tooling API and stores 114 MB per entry.

What would shrink it: a per-build dictionary (one classpath/artifact table in the root bundle, payloads referencing
it) would cut the stored size to roughly 7 MB and the per-project deserialization with it; that is a change to the
model contract on the IDE side (the build script classpath as a build-level model, dependency records referencing a
shared artifact table). Compression alone cannot do it per payload. Keeping the payloads out of the configuration
cache entry (build-cache variant) removes the serialization share of the store, not the size the IDE reads.
