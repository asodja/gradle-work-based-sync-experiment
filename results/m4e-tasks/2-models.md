Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4e-tasks/2-trace-log.txt`

Overall span: 27732 ms, 615815 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 10175 | 10175 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 390 | 390 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 314 | 17 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 34 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 33 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 28 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 28 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 25 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 23 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 21 | 2 | 0 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 7 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 5 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 5 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 2 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 0 ms, default: 10565 ms, project: 564 ms

## Configure project (287 projects, sum 8423 ms)

| Project | ms |
|---|---|
| : | 355 |
| :ant | 220 |
| :docs | 155 |
| :build-logic | 114 |
| :performance | 114 |
| :wrapper-main | 88 |
| :declarative-dsl-core | 84 |
| :samples | 82 |
| :instrumentation-declarations | 75 |
| :jacoco | 67 |
| :dependency-management | 64 |
| :base-services | 56 |
| :build-discovery | 55 |
| :flow-services | 52 |
| :kotlin-dsl-tooling-model-impls | 51 |
| :kotlin-dsl-integ-tests | 50 |
| :kotlin-dsl | 41 |
| :plugins-groovy | 40 |
| :architecture-test | 38 |
| :distributions-basics | 38 |
| :precondition-tester | 38 |
| :distributions-full | 37 |
| :build-logic-settings:architecture-docs | 36 |
| :base-ide-plugins | 36 |
| :core-kotlin-extensions | 36 |
| :core-serialization-codecs | 36 |
| :ivy | 36 |
| :build-logic:idea | 35 |
| :kotlin-dsl-plugins | 35 |
| :language-groovy | 35 |
| :language-java | 35 |
| :project-features-demos | 35 |
| :toolchains-jvm | 35 |
| :configuration-cache | 34 |
| :declarative-dsl-evaluator | 34 |
| :dependency-management-serialization-codecs | 34 |
| :isolated-ant-builder | 34 |
| :jacoco-workers | 34 |
| :bean-serialization-services | 33 |
| :declarative-dsl-tooling-builders | 33 |

## Tasks (651 executed, sum 143950 ms)

| Task | ms |
|---|---|
| :ant-impl:ideProjectModel | 9334 |
| :antlr:ideProjectModel | 9301 |
| :ant:ideProjectModel | 9184 |
| :ideProjectModel | 9090 |
| :build-logic:ideProjectModel | 9071 |
| :build-logic:binary-compatibility:ideProjectModel | 9067 |
| :ant-api:ideProjectModel | 9064 |
| :ant-worker:ideProjectModel | 9060 |
| :api-metadata:ideProjectModel | 9057 |
| :build-logic-commons:ideProjectModel | 9056 |
| :build-logic-settings:ideProjectModel | 9050 |
| :configuration-cache:ideProjectModel | 682 |
| :plugins-application:ideProjectModel | 667 |
| :docs:ideProjectModel | 486 |
| :language-java:ideProjectModel | 444 |
| :model-core:ideProjectModel | 430 |
| :kotlin-dsl-tooling-builders:ideProjectModel | 393 |
| :internal-performance-testing:ideProjectModel | 380 |
| :platform-native:ideProjectModel | 372 |
| :scala:ideProjectModel | 371 |
| :enterprise-plugin-performance:ideProjectModel | 344 |
| :software-diagnostics:ideProjectModel | 344 |
| :dependency-management:ideProjectModel | 343 |
| :testing-native:ideProjectModel | 339 |
| :tooling-native:ideProjectModel | 333 |
| :integ-test:ideProjectModel | 326 |
| :kotlin-dsl:ideProjectModel | 324 |
| :plugins-java-library:ideProjectModel | 322 |
| :declarative-dsl-provider:ideProjectModel | 305 |
| :code-quality:ideProjectModel | 300 |
| :build-cache:ideProjectModel | 298 |
| :kotlin-dsl-integ-tests:ideProjectModel | 296 |
| :maven:ideProjectModel | 296 |
| :language-groovy:ideProjectModel | 294 |
| :core:ideProjectModel | 291 |
| :plugins-java-base:ideProjectModel | 290 |
| :testing-jvm:ideProjectModel | 278 |
| :build-init:ideProjectModel | 276 |
| :precondition-tester:ideProjectModel | 274 |
| :java-platform:ideProjectModel | 271 |
