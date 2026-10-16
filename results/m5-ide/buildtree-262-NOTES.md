# GradleBuild as a task: the `ideBuildTree` bundle and the single-request sync (D29, 2026-10-17)

Owner: "Could we also implement GradleBuild as task and see what we gain?" Design from
`gradlebuild-as-task-NOTES.md`, resilience out of scope (D28).

## What was built

- Gradle side (`prototype`): `BuildTreePayload` (identity path, root dir, Gradle version, project tree with name, path,
  directory and build tree path, included builds by name and root dir, and for the root build the
  `GradleDslBaseScriptModel` data). Captured in the init script's `gradle.settingsEvaluated` from the settings'
  `ProjectDescriptor` tree, keyed by the build's identity path (under Isolated Projects the `Gradle` object a project
  sees is not the settings' instance, so a `Gradle`-keyed map missed). The root project of every build registers
  `ideBuildTree` (writes the payload as a one-entry bundle, `@Input` = canonical text) and the aggregate `ideModels`
  (depends on the tree task, on `<project>:ideProjectModel` of every project by path, and on
  `includedBuild(name).task(":ideModels")` of every included build). The base-script model is produced by Gradle's own
  `GradleDslBaseScriptModelBuilder` called reflectively (its model classes are package-private; accessibility flag).
  Isolated Projects accepted the string-path `dependsOn` across projects and the included-build task references.
- IDE side (plugin, `-Dwbs.transport.tree=task`): the plain run requests `:ideModels` only. `BuildTreeModels` reads
  the root tree bundle under the IDE's project directory, follows the included builds' root dirs, resolves each
  finishing `ideProjectModel` task's project directory for the streaming reads (longest matching identity path),
  re-reads the trees after the run (a tree task's finish event invalidates what streaming read before: the first
  attempt read the previous run's bundle mid-run), and synthesizes the `GradleBuild` view with Gradle's own model POJOs
  (`DefaultGradleBuild`, `BasicGradleProject`, `DefaultBuildIdentifier`, `DefaultProjectIdentifier`) through
  `ProtocolToModelAdapter`, so `DefaultGradleLightBuild.convertGradleBuilds` and the resolver are unchanged. The
  base-script model becomes a `java.lang.reflect.Proxy` over the Tooling API interfaces, wrapped by the IDE's own
  `GradleDslBaseScriptModelHolder`. No `GradleBuild` request, no base-script request: one Gradle operation per sync.
- Fidelity: the IDE's external project storage after a tree-mode restart equals the model-mode one, **1370 of 1370**
  files (`storage-diff.txt`); 4 builds, 5756 models in both.

## Restart (hit) measurement, 2026.2.2, three rounds round-robin (`chain-hit.out`)

| Configuration | Gradle side | IntelliJ side | data import stage | whole import |
|---|---|---|---|---|
| model request, cold daemon | 10.8 / 10.2 / 10.5 → **10.5 s** | 19.9 / 20.5 / 18.8 → 19.9 s | 17.1 / 14.6 / 15.7 → 15.7 s | 30.7 / 30.7 / 29.3 → **30.7 s** |
| tree task, cold daemon | 10.3 / 10.2 / 11.7 → **10.3 s** | 21.6 / 20.7 / 20.4 → 20.7 s | 15.6 / 14.8 / 16.0 → 15.6 s | 31.9 / 30.9 / 32.2 → **31.9 s** |
| model request, warm daemon | 9.9 / 10.4 / 10.3 → **10.3 s** | 21.4 / 19.8 / 21.2 → 21.2 s | 15.4 / 15.2 / 15.7 → 15.4 s | 31.3 / 30.2 / 31.5 → **31.3 s** |
| tree task, warm daemon | 10.0 / 12.0 / 9.6 → **10.0 s** | 19.7 / 21.6 / 22.6 → 21.6 s | 15.6 / 15.6 / 17.4 → 15.6 s | 29.6 / 33.6 / 32.2 → **32.2 s** |

No measurable difference: the Gradle side is 10.0–10.5 s in every configuration. The first chain (tree task but the
base-script model still requested) showed why: that request went from 72–94 ms to 1.8–2.2 s once it was the first
operation on the connection, so the first contact with the daemon per sync costs about 1.8 s whichever operation
makes it, and with everything in the task run the run pays it. The two requests were never the expense.

## Forced miss (settings script content changed before every run, cold daemon), three rounds round-robin (`chain-miss.out`)

| Configuration | Gradle side | IntelliJ side | of which data import stage | whole import |
|---|---|---|---|---|
| model request (miss) | 41.9 / 42.0 / 41.4 → **41.9 s** | 13.7 / 14.3 / 13.4 → **13.7 s** | 10.3 / 10.7 / 10.0 → **10.3 s** | 55.6 / 56.3 / 54.8 → **55.6 s** |
| tree task (miss) | 43.0 / 41.3 / 40.5 → **41.3 s** | 12.9 / 13.3 / 12.6 → **12.9 s** | 9.9 / 10.2 / 9.6 → **9.9 s** | 55.9 / 54.6 / 53.1 → **54.6 s** |
| speed-up tree task (miss) vs model request (miss) | −1 % | −6 % | −4 % | −2 % |

Same picture on a miss: the task run (37.0–38.7 s, a store) dominates the Gradle side in both modes, the IntelliJ side
is identical, and the removed requests do not show. The tree task is up to date on a settings-comment change (its
input is the project tree), so it costs nothing on these misses either.

## Reading

Implementing `GradleBuild` as a task works and is clean (one Gradle operation per sync, included builds by their real
names, identical IDE storage), but it saves no measurable time on this machine, on a hit or on a miss. The model
request stays the default; the tree mode remains available as `-Dwbs.transport.tree=task`.
