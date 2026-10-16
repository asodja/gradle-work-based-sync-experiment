Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4-tasks/1-trace-log.txt`

Overall span: 44222 ms, 628625 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 18166 | 18166 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 11776 | 11662 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 3634 | 3634 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 546 | 503 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 389 | 29 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 64 | 4 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 41 | 18 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 38 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 34 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 33 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 31 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 29 | 4 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 29 | 1 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 7 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 7 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 6 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 6 | 1 | 0 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 1 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 547 ms, default: 21800 ms, project: 12497 ms

## Configure project (287 projects, sum 16992 ms)

| Project | ms |
|---|---|
| : | 1240 |
| :ant | 984 |
| :build-logic-settings:architecture-docs | 708 |
| :build-logic | 497 |
| :docs | 426 |
| :build-logic:binary-compatibility | 317 |
| :bean-serialization-services | 308 |
| :build-logic-commons:basics | 293 |
| :architecture-test | 275 |
| :declarative-dsl-core | 245 |
| :kotlin-dsl | 198 |
| :distributions-full | 144 |
| :declarative-dsl-evaluator | 142 |
| :build-logic-settings | 141 |
| :kotlin-dsl-integ-tests | 137 |
| :wrapper-main | 135 |
| :performance | 127 |
| :build-cache | 124 |
| :precondition-tester | 119 |
| :base-services | 118 |
| :distributions-basics | 117 |
| :kotlin-dsl-plugins | 116 |
| :tooling-api | 111 |
| :process-services-base | 106 |
| :public-api | 101 |
| :internal-build-reports | 95 |
| :api-metadata | 79 |
| :enterprise-plugin-performance | 74 |
| :ant-impl | 66 |
| :encryption-services | 65 |
| :build-cache-example-client | 63 |
| :configuration-cache | 62 |
| :classloaders | 56 |
| :internal-distribution-testing | 56 |
| :ant-api | 55 |
| :build-cache-local | 55 |
| :core-kotlin-extensions | 55 |
| :build-state | 54 |
| :core | 52 |
| :graph-serialization | 52 |

## Tasks (651 executed, sum 64602 ms)

| Task | ms |
|---|---|
| :antlr:ideProjectModel | 1292 |
| :ant-impl:ideProjectModel | 1004 |
| :docs:ideProjectModel | 959 |
| :api-metadata:ideProjectModel | 956 |
| :ant-worker:ideProjectModel | 952 |
| :architecture-test:ideProjectModel | 860 |
| :ant:ideProjectModel | 820 |
| :build-logic:buildquality:ideProjectModel | 690 |
| :build-logic:binary-compatibility:ideProjectModel | 689 |
| :enterprise-plugin-performance:ideProjectModel | 686 |
| :build-logic-commons:basics:ideProjectModel | 658 |
| :plugins-application:ideProjectModel | 647 |
| :performance:ideProjectModel | 641 |
| :bean-serialization-services:ideProjectModel | 591 |
| :core:ideProjectModel | 585 |
| :code-quality:ideProjectModel | 545 |
| :dependency-management:ideProjectModel | 545 |
| :build-logic-commons:build-platform-plugin:ideProjectModel | 537 |
| :tooling-api:ideProjectModel | 533 |
| :build-cache:ideProjectModel | 513 |
| :configuration-cache:ideProjectModel | 462 |
| :classpath:ideProjectModel | 439 |
| :build-logic-settings:architecture-docs:ideProjectModel | 434 |
| :kotlin-dsl-tooling-builders:ideProjectModel | 431 |
| :toolchains-jvm:ideProjectModel | 413 |
| :language-java:ideProjectModel | 408 |
| :build-init:ideProjectModel | 405 |
| :integ-test:ideProjectModel | 391 |
| :plugins-java:ideProjectModel | 389 |
| :declarative-dsl-provider:ideProjectModel | 376 |
| :internal-performance-testing:ideProjectModel | 370 |
| :build-configuration:ideProjectModel | 366 |
| :build-cache-core:ideProjectModel | 365 |
| :wrapper-main:ideProjectModel | 346 |
| :tooling-native:ideProjectModel | 338 |
| :declarative-dsl-tooling-builders:ideProjectModel | 330 |
| :base-diagnostics:ideProjectModel | 323 |
| :build-cache-local:ideProjectModel | 317 |
| :ivy:ideProjectModel | 310 |
| :base-services:ideProjectModel | 307 |
