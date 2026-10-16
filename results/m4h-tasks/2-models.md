Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4h-tasks/2-trace-log.txt`

Overall span: 17962 ms, 616162 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 9530 | 9530 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 364 | 364 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 300 | 20 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 49 | 4 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 29 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 24 | 4 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 23 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 22 | 1 | 0 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 7 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 2 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 0 ms, default: 9894 ms, project: 559 ms

## Configure project (287 projects, sum 7943 ms)

| Project | ms |
|---|---|
| : | 328 |
| :ant | 212 |
| :docs | 195 |
| :build-logic | 111 |
| :io | 94 |
| :plugins-java-base | 84 |
| :signing | 82 |
| :performance | 66 |
| :declarative-dsl-core | 62 |
| :build-process-services | 57 |
| :problems | 48 |
| :build-logic:binary-compatibility | 45 |
| :build-cache-spi | 44 |
| :configuration-cache | 42 |
| :kotlin-dsl | 41 |
| :precondition-tester | 40 |
| :build-logic-commons:basics | 38 |
| :kotlin-dsl-integ-tests | 37 |
| :declarative-dsl-evaluator | 36 |
| :toolchains-jvm | 36 |
| :declarative-dsl-tooling-builders | 35 |
| :kotlin-dsl-tooling-builders | 35 |
| :kotlin-dsl-plugins | 34 |
| :build-configuration | 33 |
| :build-discovery | 33 |
| :wrapper-main | 33 |
| :declarative-dsl-provider | 32 |
| :declarative-dsl-tooling-models | 32 |
| :dependency-management-serialization-codecs | 32 |
| :functional | 32 |
| :kotlin-dsl-provider-plugins | 32 |
| :bean-serialization-services | 31 |
| :build-init | 31 |
| :configuration-cache-base | 31 |
| :plugin-development | 31 |
| :project-features | 31 |
| :configuration-problems-base | 30 |
| :flow-services | 30 |
| :isolated-action-services | 30 |
| :kotlin-dsl-tooling-model-impls | 30 |

## Tasks (651 executed, sum 45600 ms)

| Task | ms |
|---|---|
| :docs:ideProjectModel | 762 |
| :enterprise-plugin-performance:ideProjectModel | 642 |
| :antlr:ideProjectModel | 636 |
| :configuration-cache:ideProjectModel | 603 |
| :ant-impl:ideProjectModel | 534 |
| :plugins-java:ideProjectModel | 531 |
| :ivy:ideProjectModel | 409 |
| :plugin-development:ideProjectModel | 397 |
| :toolchains-jvm:ideProjectModel | 397 |
| :base-diagnostics:ideProjectModel | 389 |
| :scala:ideProjectModel | 388 |
| :language-java:ideProjectModel | 386 |
| :execution:ideProjectModel | 371 |
| :enterprise:ideProjectModel | 368 |
| :ide:ideProjectModel | 365 |
| :build-configuration:ideProjectModel | 344 |
| :performance:ideProjectModel | 344 |
| :maven:ideProjectModel | 339 |
| :tooling-native:ideProjectModel | 336 |
| :dependency-management:ideProjectModel | 324 |
| :tooling-api:ideProjectModel | 322 |
| :plugins-model-native:ideProjectModel | 321 |
| :base-ide-plugins:ideProjectModel | 318 |
| :model-core:ideProjectModel | 317 |
| :platform-native:ideProjectModel | 316 |
| :base-services:ideProjectModel | 315 |
| :build-init:ideProjectModel | 305 |
| :platform-base:ideProjectModel | 303 |
| :kotlin-dsl:ideProjectModel | 298 |
| :ant:ideProjectModel | 286 |
| :build-cache-http:ideProjectModel | 286 |
| :version-control:ideProjectModel | 286 |
| :core:ideProjectModel | 284 |
| :jacoco:ideProjectModel | 283 |
| :ant-worker:ideProjectModel | 280 |
| :plugins-groovy:ideProjectModel | 278 |
| :groovy-compiler-worker:ideProjectModel | 274 |
| :test-kit:ideProjectModel | 264 |
| :wrapper-main:ideProjectModel | 264 |
| :plugins-java-base:ideProjectModel | 262 |
