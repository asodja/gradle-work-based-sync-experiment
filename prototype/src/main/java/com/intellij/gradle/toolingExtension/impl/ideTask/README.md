# ide-model-tasks: the Gradle side of the work-based sync

One jar, put on the IDE's init script classpath. The init script ends with
`com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTasks.apply(gradle)`; nothing is applied as a plugin.

## Flow

1. `IdeModelTasks.apply` hooks the build lifecycle: `settingsEvaluated` captures the build structure
   (`capture.BuildTreeCapture`), `lifecycle.beforeProject` registers the tasks, `lifecycle.afterProject` fills the
   shared builder context, `taskGraph.whenReady` validates task dependencies (`design/task-contract.md`).
2. Every project gets `ideProjectModel` (`tasks.IdeProjectModelTask`). Its inputs are evaluated when the task is
   realized for the graph: `capture.ProjectModelCapture` runs the IntelliJ model builders and the Gradle-owned
   captures (`capture.GradleOwnedModelCapture`) and turns each model into a `CapturedModel`. The fingerprint of all
   captured models is the task's `@Input`; the models are `@Internal` state stored in the configuration cache entry.
3. Each root project gets `ideBuildTree` (build structure, base script model) and `ideModels`, the aggregate that
   depends on every model task of the build tree. The IDE runs `:ideModels` only.
4. A task's action writes its models as one bundle file, `<build dir>/ide/models/<task>.bin`
   (`payload.ModelBundle`, `payload.ModelPayload`). The IDE plugin (`../ide-plugin`) or the harness reads the
   bundles and rebuilds the IDE's models (`consumer.*`).

## Packages

| Package | Loaded in | Holds |
|---|---|---|
| `ideTask` | daemon | `IdeModelTasks` (entry point), `PortedModels` (which model classes travel as tasks), the `IdeModelTaskRequest` model builder used by the older build-action mode |
| `ideTask.tasks` | daemon | the task types |
| `ideTask.capture` | daemon | everything that computes a model at configuration time |
| `ideTask.payload` | daemon and IDE | the bundle file format and the per-model payload forms; no Gradle API |
| `ideTask.consumer` | IDE and harness | Tooling API model implementations assembled from payloads |

Naming: tasks are `Ide…Task`, captures `…Capture`, payload forms `…Payload`, consumers `…FromPayloads`.

## Build

`./gradlew build -PideaHome=<IntelliJ install>` (compiles against the IDE's Gradle plugin jars; tests need them too).
The jar is `build/libs/ide-model-tasks.jar`; `../ide-plugin` and `../harness` take it from there.
`compare-*.sh` are the builder-vs-task comparison scripts of M3 (`design/task-contract.md`, "Comparison test").
