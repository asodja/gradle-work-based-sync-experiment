Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4h-tasks-only/2-trace-log.txt`

Overall span: 16673 ms, 608955 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 9620 | 9620 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 261 | 261 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 67 | 4 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 9881 ms, project: 67 ms

## Configure project (287 projects, sum 6980 ms)

| Project | ms |
|---|---|
| : | 289 |
| :ant | 244 |
| :docs | 176 |
| :declarative-dsl-core | 86 |
| :build-logic | 84 |
| :test-suites-base | 79 |
| :plugins-model-native | 74 |
| :performance | 59 |
| :encryption-services | 57 |
| :build-operations | 55 |
| :jacoco-workers | 53 |
| :enterprise-workers | 42 |
| :build-cache-example-client | 37 |
| :build-init | 37 |
| :classloaders | 37 |
| :core-flow-services-api | 37 |
| :kotlin-dsl | 36 |
| :base-asm | 35 |
| :kotlin-dsl-integ-tests | 34 |
| :bean-serialization-services | 33 |
| :configuration-cache | 32 |
| :kotlin-dsl-plugins | 32 |
| :precondition-tester | 31 |
| :build-cache | 30 |
| :configuration-problems-base | 30 |
| :declarative-dsl-evaluator | 30 |
| :toolchains-jvm | 30 |
| :ant-impl | 29 |
| :architecture-test | 29 |
| :kotlin-dsl-tooling-builders | 29 |
| :ant-api | 28 |
| :api-metadata | 28 |
| :declarative-dsl-tooling-builders | 28 |
| :wrapper-main | 28 |
| :base-services | 27 |
| :configuration-cache-base | 27 |
| :core-kotlin-extensions | 27 |
| :graph-serialization | 27 |
| :kotlin-dsl-provider-plugins | 27 |
| :project-features-api | 27 |

## Tasks (650 executed, sum 40419 ms)

| Task | ms |
|---|---|
| :configuration-cache:ideProjectModel | 602 |
| :plugins-java:ideProjectModel | 573 |
| :ant-impl:ideProjectModel | 564 |
| :enterprise-plugin-performance:ideProjectModel | 560 |
| :antlr:ideProjectModel | 534 |
| :docs:ideProjectModel | 470 |
| :tooling-native:ideProjectModel | 403 |
| :java-platform:ideProjectModel | 370 |
| :base-diagnostics:ideProjectModel | 365 |
| :plugin-development:ideProjectModel | 360 |
| :plugins-jvm-test-fixtures:ideProjectModel | 320 |
| :ant:ideProjectModel | 319 |
| :integ-test:ideProjectModel | 318 |
| :core:ideProjectModel | 309 |
| :base-ide-plugins:ideProjectModel | 295 |
| :dependency-management:ideProjectModel | 293 |
| :precondition-tester:ideProjectModel | 293 |
| :language-native:ideProjectModel | 288 |
| :war:ideProjectModel | 286 |
| :kotlin-dsl-integ-tests:ideProjectModel | 285 |
| :model-groovy:ideProjectModel | 275 |
| :language-groovy:ideProjectModel | 273 |
| :tooling-api:ideProjectModel | 271 |
| :launcher:ideProjectModel | 269 |
| :file-collections:ideProjectModel | 266 |
| :api-metadata:ideProjectModel | 264 |
| :declarative-dsl-provider:ideProjectModel | 263 |
| :build-init:ideProjectModel | 260 |
| :model-core:ideProjectModel | 258 |
| :platform-base:ideProjectModel | 258 |
| :ant-worker:ideProjectModel | 256 |
| :toolchains-jvm:ideProjectModel | 254 |
| :build-cache-http:ideProjectModel | 250 |
| :performance:ideProjectModel | 248 |
| :ivy:ideProjectModel | 247 |
| :maven:ideProjectModel | 234 |
| :kotlin-dsl-provider-plugins:ideProjectModel | 230 |
| :smoke-test:ideProjectModel | 230 |
| :java-compiler-worker:ideProjectModel | 229 |
| :internal-performance-testing:ideProjectModel | 228 |
