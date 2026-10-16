Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4g-tasks/2-trace-log.txt`

Overall span: 27352 ms, 616141 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 10896 | 10896 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 8763 | 8763 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 305 | 20 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 45 | 6 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 29 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 21 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 20 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 17 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 0 ms, default: 19659 ms, project: 550 ms

## Configure project (287 projects, sum 17771 ms)

| Project | ms |
|---|---|
| :build-logic-settings | 10594 |
| : | 300 |
| :ant | 202 |
| :docs | 142 |
| :build-logic | 89 |
| :scala-compiler-worker | 89 |
| :normalization | 77 |
| :ide-native | 74 |
| :declarative-dsl-tooling-builders | 70 |
| :performance | 66 |
| :declarative-dsl-core | 57 |
| :build-init-specs | 44 |
| :snapshots | 40 |
| :kotlin-dsl | 35 |
| :base-compiler-worker | 34 |
| :isolated-action-services | 34 |
| :kotlin-dsl-integ-tests | 34 |
| :precondition-tester | 34 |
| :declarative-dsl-evaluator | 32 |
| :base-diagnostics | 31 |
| :bean-serialization-services | 31 |
| :kotlin-dsl-plugins | 31 |
| :kotlin-dsl-tooling-builders | 31 |
| :toolchains-jvm | 31 |
| :build-logic-settings:architecture-docs | 30 |
| :configuration-cache | 30 |
| :flow-services | 30 |
| :wrapper-main | 30 |
| :build-cache | 29 |
| :core-serialization-codecs | 29 |
| :declarative-dsl-tooling-models | 29 |
| :plugins-java | 29 |
| :project-features-api | 29 |
| :soak | 29 |
| :architecture-test | 28 |
| :configuration-problems-base | 28 |
| :core-kotlin-extensions | 28 |
| :declarative-dsl-provider | 28 |
| :encryption-services | 28 |
| :graph-isolation | 28 |

## Tasks (651 executed, sum 41172 ms)

| Task | ms |
|---|---|
| :docs:ideProjectModel | 647 |
| :antlr:ideProjectModel | 439 |
| :plugins-java:ideProjectModel | 431 |
| :ant-impl:ideProjectModel | 419 |
| :plugins-application:ideProjectModel | 418 |
| :tooling-api:ideProjectModel | 404 |
| :configuration-cache:ideProjectModel | 395 |
| :execution:ideProjectModel | 388 |
| :maven:ideProjectModel | 368 |
| :plugins-groovy:ideProjectModel | 364 |
| :enterprise-plugin-performance:ideProjectModel | 360 |
| :declarative-dsl-provider:ideProjectModel | 358 |
| :dependency-management:ideProjectModel | 357 |
| :model-core:ideProjectModel | 356 |
| :performance:ideProjectModel | 330 |
| :integ-test:ideProjectModel | 327 |
| :kotlin-dsl:ideProjectModel | 317 |
| :version-control:ideProjectModel | 302 |
| :build-cache-http:ideProjectModel | 301 |
| :language-native:ideProjectModel | 295 |
| :ide-native:ideProjectModel | 292 |
| :java-platform:ideProjectModel | 289 |
| :bean-serialization-services:ideProjectModel | 281 |
| :code-quality:ideProjectModel | 280 |
| :software-diagnostics:ideProjectModel | 273 |
| :build-init:ideProjectModel | 264 |
| :plugin-use:ideProjectModel | 263 |
| :project-features-demos:ideProjectModel | 261 |
| :toolchains-jvm-shared:ideProjectModel | 261 |
| :file-watching:ideProjectModel | 260 |
| :ivy:ideProjectModel | 258 |
| :base-diagnostics:ideProjectModel | 255 |
| :jacoco:ideProjectModel | 255 |
| :ant:ideProjectModel | 253 |
| :toolchains-jvm:ideProjectModel | 248 |
| :build-configuration:ideProjectModel | 247 |
| :enterprise:ideProjectModel | 243 |
| :resources-http:ideProjectModel | 242 |
| :internal-performance-testing:ideProjectModel | 237 |
| :ant-api:ideProjectModel | 235 |
