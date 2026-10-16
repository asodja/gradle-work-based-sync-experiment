Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4d-tasks/2-trace-log.txt`

Overall span: 33447 ms, 615696 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 10941 | 10941 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 352 | 352 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 299 | 15 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 36 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 34 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 22 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 21 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 21 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 15 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 6 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 5 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 1 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 1 ms, default: 11293 ms, project: 537 ms

## Configure project (287 projects, sum 8003 ms)

| Project | ms |
|---|---|
| : | 307 |
| :ant | 213 |
| :docs | 136 |
| :build-logic | 122 |
| :java-platform | 89 |
| :wrapper-shared | 79 |
| :enterprise-logging | 75 |
| :plugins-distribution | 72 |
| :performance | 70 |
| :declarative-dsl-core | 67 |
| :serialization | 65 |
| :kotlin-dsl-integ-tests | 56 |
| :client-services | 51 |
| :kotlin-dsl | 48 |
| :kotlin-dsl-plugins | 39 |
| :problems | 37 |
| :bean-serialization-services | 36 |
| :configuration-cache | 36 |
| :declarative-dsl-evaluator | 36 |
| :build-logic:jvm | 35 |
| :enterprise | 35 |
| :kotlin-dsl-tooling-builders | 34 |
| :declarative-dsl-tooling-builders | 33 |
| :graph-serialization | 33 |
| :kotlin-dsl-provider-plugins | 33 |
| :toolchains-jvm | 33 |
| :wrapper-main | 33 |
| :core-kotlin-extensions | 32 |
| :core-serialization-codecs | 32 |
| :dependency-management-serialization-codecs | 32 |
| :precondition-tester | 32 |
| :stdlib-kotlin-extensions | 32 |
| :build-cache | 31 |
| :configuration-cache-base | 31 |
| :configuration-problems-base | 31 |
| :core | 31 |
| :declarative-dsl-provider | 31 |
| :declarative-dsl-tooling-models | 31 |
| :enterprise-operations | 31 |
| :kotlin-dsl-tooling-model-impls | 31 |

## Tasks (651 executed, sum 159167 ms)

| Task | ms |
|---|---|
| :build-logic:ideProjectModel | 14711 |
| :build-logic:binary-compatibility:ideProjectModel | 14710 |
| :build-logic:buildquality:ideProjectModel | 14710 |
| :build-logic:dependency-modules:ideProjectModel | 14708 |
| :build-logic:documentation:ideProjectModel | 14708 |
| :build-logic-commons:basics:ideProjectModel | 14707 |
| :build-logic:build-update-utils:ideProjectModel | 14705 |
| :build-logic:idea:ideProjectModel | 14704 |
| :docs:ideProjectModel | 604 |
| :antlr:ideProjectModel | 457 |
| :enterprise-plugin-performance:ideProjectModel | 421 |
| :code-quality:ideProjectModel | 415 |
| :ant-impl:ideProjectModel | 410 |
| :configuration-cache:ideProjectModel | 400 |
| :dependency-management:ideProjectModel | 385 |
| :plugin-development:ideProjectModel | 380 |
| :model-core:ideProjectModel | 360 |
| :software-diagnostics:ideProjectModel | 358 |
| :ide-plugins:ideProjectModel | 320 |
| :ivy:ideProjectModel | 320 |
| :tooling-native:ideProjectModel | 320 |
| :integ-test:ideProjectModel | 319 |
| :testing-jvm:ideProjectModel | 308 |
| :core:ideProjectModel | 299 |
| :internal-integ-testing:ideProjectModel | 299 |
| :ide-native:ideProjectModel | 296 |
| :plugins-jvm-test-fixtures:ideProjectModel | 290 |
| :java-platform:ideProjectModel | 283 |
| :build-configuration:ideProjectModel | 282 |
| :build-cache-http:ideProjectModel | 278 |
| :plugins-java-base:ideProjectModel | 278 |
| :plugin-use:ideProjectModel | 276 |
| :declarative-dsl-provider:ideProjectModel | 274 |
| :plugins-application:ideProjectModel | 272 |
| :plugins-jvm-test-suite:ideProjectModel | 268 |
| :maven:ideProjectModel | 254 |
| :tooling-api:ideProjectModel | 251 |
| :platform-base:ideProjectModel | 250 |
| :testing-base:ideProjectModel | 248 |
| :base-diagnostics:ideProjectModel | 246 |
