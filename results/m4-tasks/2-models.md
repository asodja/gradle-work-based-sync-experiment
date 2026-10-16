Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4-tasks/2-trace-log.txt`

Overall span: 23249 ms, 618602 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 8511 | 8511 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 7290 | 7192 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 419 | 383 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 386 | 386 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 352 | 29 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 35 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 33 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 29 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 25 | 4 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 21 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 16 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 6 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 5 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 419 ms, default: 8897 ms, project: 7889 ms

## Configure project (287 projects, sum 7310 ms)

| Project | ms |
|---|---|
| : | 326 |
| :ant | 263 |
| :docs | 145 |
| :build-logic | 86 |
| :problems-impl | 82 |
| :jvm-compiler-worker | 81 |
| :test-suites-base | 80 |
| :declarative-dsl-core | 67 |
| :enterprise-operations | 67 |
| :performance | 64 |
| :composite-builds | 51 |
| :project-features-api | 44 |
| :kotlin-dsl | 38 |
| :kotlin-dsl-integ-tests | 36 |
| :problems-rendering | 34 |
| :declarative-dsl-evaluator | 33 |
| :kotlin-dsl-plugins | 33 |
| :project-features | 33 |
| :build-logic-commons:basics | 32 |
| :core-serialization-codecs | 32 |
| :precondition-tester | 32 |
| :wrapper-main | 32 |
| :bean-serialization-services | 31 |
| :configuration-cache | 31 |
| :project-features-demos | 31 |
| :toolchains-jvm | 31 |
| :graph-serialization | 30 |
| :kotlin-dsl-tooling-builders | 30 |
| :declarative-dsl-tooling-builders | 29 |
| :declarative-dsl-tooling-models | 29 |
| :dependency-management-serialization-codecs | 29 |
| :encryption-services | 29 |
| :problems | 29 |
| :process-services-base | 29 |
| :core-kotlin-extensions | 28 |
| :graph-isolation | 28 |
| :isolated-action-services | 28 |
| :kotlin-dsl-tooling-model-impls | 28 |
| :tooling-api | 28 |
| :declarative-dsl-provider | 27 |

## Tasks (651 executed, sum 36995 ms)

| Task | ms |
|---|---|
| :docs:ideProjectModel | 507 |
| :antlr:ideProjectModel | 486 |
| :dependency-management:ideProjectModel | 441 |
| :kotlin-dsl-tooling-builders:ideProjectModel | 437 |
| :ant-impl:ideProjectModel | 428 |
| :enterprise-plugin-performance:ideProjectModel | 416 |
| :kotlin-dsl-integ-tests:ideProjectModel | 353 |
| :ant-api:ideProjectModel | 340 |
| :plugins-java:ideProjectModel | 331 |
| :language-native:ideProjectModel | 330 |
| :maven:ideProjectModel | 319 |
| :language-java:ideProjectModel | 314 |
| :configuration-cache:ideProjectModel | 295 |
| :tooling-api:ideProjectModel | 285 |
| :ant-worker:ideProjectModel | 284 |
| :kotlin-dsl-provider-plugins:ideProjectModel | 272 |
| :build-configuration:ideProjectModel | 269 |
| :classpath:ideProjectModel | 266 |
| :declarative-dsl-tooling-builders:ideProjectModel | 261 |
| :jacoco:ideProjectModel | 261 |
| :kotlin-dsl:ideProjectModel | 261 |
| :precondition-tester:ideProjectModel | 261 |
| :api-metadata:ideProjectModel | 259 |
| :tooling-native:ideProjectModel | 259 |
| :ant:ideProjectModel | 256 |
| :execution:ideProjectModel | 251 |
| :testing-native:ideProjectModel | 250 |
| :architecture-test:ideProjectModel | 248 |
| :code-quality:ideProjectModel | 244 |
| :base-asm:ideProjectModel | 243 |
| :build-cache:ideProjectModel | 238 |
| :smoke-test:ideProjectModel | 237 |
| :java-platform:ideProjectModel | 236 |
| :ivy:ideProjectModel | 235 |
| :resources-gcs:ideProjectModel | 235 |
| :project-features-demos:ideProjectModel | 232 |
| :build-cache-core:ideProjectModel | 229 |
| :performance:ideProjectModel | 222 |
| :plugin-development:ideProjectModel | 222 |
| :declarative-dsl-provider:ideProjectModel | 221 |
