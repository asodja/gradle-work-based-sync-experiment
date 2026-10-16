Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4d-tasks-only/2-trace-log.txt`

Overall span: 27364 ms, 608803 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 9362 | 9362 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 281 | 281 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 54 | 4 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 9643 ms, project: 54 ms

## Configure project (287 projects, sum 6997 ms)

| Project | ms |
|---|---|
| : | 271 |
| :ant | 245 |
| :docs | 136 |
| :problems-api | 87 |
| :build-logic | 83 |
| :javadoc | 78 |
| :time | 75 |
| :enterprise-logging | 62 |
| :performance | 61 |
| :declarative-dsl-core | 58 |
| :configuration-cache | 57 |
| :project-features-demos | 43 |
| :kotlin-dsl | 38 |
| :kotlin-dsl-integ-tests | 34 |
| :declarative-dsl-evaluator | 31 |
| :project-features | 31 |
| :toolchains-jvm | 31 |
| :declarative-dsl-tooling-models | 30 |
| :kotlin-dsl-tooling-builders | 30 |
| :precondition-tester | 30 |
| :project-features-api | 30 |
| :wrapper-main | 30 |
| :bean-serialization-services | 29 |
| :configuration-cache-base | 29 |
| :configuration-problems-base | 29 |
| :problems-rendering | 29 |
| :architecture-test | 28 |
| :core-kotlin-extensions | 28 |
| :kotlin-dsl-plugins | 28 |
| :kotlin-dsl-tooling-model-impls | 28 |
| :core-serialization-codecs | 27 |
| :declarative-dsl-tooling-builders | 27 |
| :dependency-management-serialization-codecs | 27 |
| :encryption-services | 27 |
| :problems | 27 |
| :problems-impl | 27 |
| :process-services-base | 27 |
| :base-compiler-worker | 26 |
| :build-cache | 26 |
| :core | 26 |

## Tasks (650 executed, sum 149157 ms)

| Task | ms |
|---|---|
| :ant-worker:ideProjectModel | 11450 |
| :build-logic-settings:architecture-docs:ideProjectModel | 11214 |
| :build-logic-settings:build-environment:ideProjectModel | 11198 |
| :build-logic-settings:default-settings-plugins:ideProjectModel | 11197 |
| :build-logic-settings:ideProjectModel | 11196 |
| :build-logic-settings:configuration-cache-compatibility:ideProjectModel | 11190 |
| :ant-impl:ideProjectModel | 11190 |
| :build-logic:ideProjectModel | 11188 |
| :ideProjectModel | 11188 |
| :build-logic-commons:ideProjectModel | 11184 |
| :configuration-cache:ideProjectModel | 537 |
| :docs:ideProjectModel | 475 |
| :dependency-management:ideProjectModel | 454 |
| :build-cache-http:ideProjectModel | 428 |
| :plugins-groovy:ideProjectModel | 368 |
| :tooling-native:ideProjectModel | 335 |
| :ide-plugins:ideProjectModel | 326 |
| :build-init:ideProjectModel | 320 |
| :core:ideProjectModel | 296 |
| :enterprise-plugin-performance:ideProjectModel | 296 |
| :kotlin-dsl-integ-tests:ideProjectModel | 293 |
| :plugins-model-native:ideProjectModel | 293 |
| :software-diagnostics:ideProjectModel | 289 |
| :model-core:ideProjectModel | 281 |
| :language-java:ideProjectModel | 278 |
| :maven:ideProjectModel | 274 |
| :code-quality:ideProjectModel | 272 |
| :language-groovy:ideProjectModel | 269 |
| :kotlin-dsl-plugins:ideProjectModel | 266 |
| :classpath:ideProjectModel | 262 |
| :platform-jvm:ideProjectModel | 253 |
| :build-cache:ideProjectModel | 244 |
| :platform-base:ideProjectModel | 244 |
| :plugin-development:ideProjectModel | 244 |
| :tooling-api:ideProjectModel | 244 |
| :soak:ideProjectModel | 241 |
| :ide:ideProjectModel | 235 |
| :performance:ideProjectModel | 231 |
| :signing:ideProjectModel | 231 |
| :file-operations:ideProjectModel | 228 |
