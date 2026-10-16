# m4d: first timing round of D17 (2026-10-16, 04:59–05:15), intermediate prototype

Prototype state: idea-module payload mirroring the plugin's defaults, Kotlin DSL payload of the build script only,
stage-1 accessors warm-up in place (the earlier chain deadlocked without it, LOG 2026-10-16). Scenarios 2, 3, 4,
three runs each; configuration cache directory deleted before each variant; trace logs deleted.

| Variant | Warm plain (2) | IP hit (3) | Edit (4) | m4c floor for comparison (`--tasks-only`, no Gradle-owned captures) |
|---|---|---|---|---|
| tasks | 25.5 s | 3.6 s | 17.8 s | — (M4 `tasks` with the two builders: 22.4 / 1.75 / 16.5 s) |
| tasks-only | 20.6 s | 3.0 s | 22.1 s | 14.6 / 1.59 / 9.4 s |
| launcher | 34.3 s (24–56) | 6.5 s (1.4–8.0) | 14.4 s | M4: 16.6 / 3.3 / 11.4 s |

Reading: adding the two Gradle-owned captures to every project made the task variants **slower than with the
serial builders**, by 6–13 s, and the spread of the launcher runs (24–56 s on scenario 2) says the daemon was
struggling, not only working more. The idea-module capture is trivial (project state only). The Kotlin DSL script
capture calls Gradle's `KotlinBuildScriptModelBuilder` per project, which per script resolves the script
classpath, the type-safe accessors, and the **source path**: the sources of every buildscript dependency of the
project and of all its parents (`sourcePathFor(sourceLookupScriptHandlers)`, a fresh `ArtifactResolutionQuery` per
script, no sharing between scripts) plus the Gradle distribution sources lookup (`SourceDistributionResolver`,
fails here without network, per script). The serial `KotlinDslScriptsModel` builder does the same work in 7.3 s on
one thread under the root project lock; done from 287 parallel captures that each hold their own project lock and
resolve the parents' configurations, the work is the same but the lock traffic is not. Attribution is the next
step (`-PideModelTasks.timing=1`, per-model capture time; thread samples during the run). Superseded by `m4e-*`
(final prototype: all scripts per project, idea extension read when applied).
