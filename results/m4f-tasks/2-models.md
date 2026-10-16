Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4f-tasks/2-trace-log.txt`

Overall span: 28995 ms, 616035 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 11123 | 11123 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 9846 | 9846 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 301 | 14 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 42 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 36 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 33 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 27 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 23 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 21 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 6 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 1 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 1 ms, default: 20969 ms, project: 568 ms

## Configure project (287 projects, sum 17718 ms)

| Project | ms |
|---|---|
| :build-logic-settings | 9501 |
| : | 321 |
| :ant | 241 |
| :docs | 190 |
| :request-handler-worker | 116 |
| :build-logic | 98 |
| :performance | 72 |
| :declarative-dsl-core | 66 |
| :base-services | 61 |
| :launcher | 60 |
| :docs-asciidoctor-extensions-base | 53 |
| :guava-serialization-codecs | 52 |
| :versioned-cache | 51 |
| :ant-api | 50 |
| :bean-serialization-services | 49 |
| :architecture-test | 47 |
| :build-init-specs | 47 |
| :kotlin-dsl | 43 |
| :build-logic-settings:architecture-docs | 42 |
| :distributions-native | 42 |
| :distributions-publishing | 42 |
| :build-cache | 41 |
| :security | 40 |
| :kotlin-dsl-integ-tests | 39 |
| :graph-serialization | 38 |
| :build-discovery-impl | 37 |
| :concurrent | 37 |
| :build-cache-packaging | 36 |
| :configuration-cache | 34 |
| :graph-isolation | 34 |
| :kotlin-dsl-plugins | 34 |
| :kotlin-dsl-tooling-builders | 34 |
| :precondition-tester | 34 |
| :serialization | 34 |
| :wrapper-main | 34 |
| :declarative-dsl-evaluator | 33 |
| :declarative-dsl-tooling-builders | 33 |
| :tooling-api | 33 |
| :dependency-management-serialization-codecs | 32 |
| :encryption-services | 32 |

## Tasks (651 executed, sum 43377 ms)

| Task | ms |
|---|---|
| :antlr:ideProjectModel | 615 |
| :ant-impl:ideProjectModel | 609 |
| :docs:ideProjectModel | 595 |
| :core:ideProjectModel | 456 |
| :code-quality:ideProjectModel | 444 |
| :execution:ideProjectModel | 431 |
| :kotlin-dsl-tooling-builders:ideProjectModel | 429 |
| :model-core:ideProjectModel | 427 |
| :base-diagnostics:ideProjectModel | 424 |
| :integ-test:ideProjectModel | 423 |
| :tooling-native:ideProjectModel | 404 |
| :plugins-application:ideProjectModel | 402 |
| :maven:ideProjectModel | 398 |
| :internal-performance-testing:ideProjectModel | 385 |
| :dependency-management:ideProjectModel | 365 |
| :toolchains-jvm:ideProjectModel | 350 |
| :enterprise-plugin-performance:ideProjectModel | 323 |
| :tooling-api:ideProjectModel | 320 |
| :plugins-java:ideProjectModel | 312 |
| :build-cache-http:ideProjectModel | 311 |
| :build-cache:ideProjectModel | 295 |
| :ide:ideProjectModel | 293 |
| :performance:ideProjectModel | 284 |
| :ivy:ideProjectModel | 283 |
| :base-ide-plugins:ideProjectModel | 278 |
| :java-platform:ideProjectModel | 278 |
| :build-init:ideProjectModel | 276 |
| :platform-jvm:ideProjectModel | 276 |
| :plugins-model-native:ideProjectModel | 276 |
| :plugins-jvm-test-fixtures:ideProjectModel | 276 |
| :internal-integ-testing:ideProjectModel | 275 |
| :file-watching:ideProjectModel | 269 |
| :kotlin-dsl:ideProjectModel | 266 |
| :enterprise:ideProjectModel | 261 |
| :ant-worker:ideProjectModel | 258 |
| :logging:ideProjectModel | 258 |
| :ant:ideProjectModel | 252 |
| :language-java:ideProjectModel | 246 |
| :wrapper-main:ideProjectModel | 245 |
| :declarative-dsl-provider:ideProjectModel | 244 |
