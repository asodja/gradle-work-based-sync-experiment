Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4-tasks-only/1-trace-log.txt`

Overall span: 28609 ms, 618176 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 16027 | 16027 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 3632 | 3632 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 105 | 4 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 19659 ms, project: 105 ms

## Configure project (287 projects, sum 16343 ms)

| Project | ms |
|---|---|
| : | 1230 |
| :ant | 936 |
| :build-logic-settings:architecture-docs | 738 |
| :build-logic | 506 |
| :docs | 400 |
| :build-logic:binary-compatibility | 330 |
| :build-logic-commons:basics | 301 |
| :bean-serialization-services | 284 |
| :architecture-test | 272 |
| :declarative-dsl-core | 232 |
| :kotlin-dsl | 187 |
| :wrapper-main | 141 |
| :build-cache | 132 |
| :declarative-dsl-evaluator | 131 |
| :precondition-tester | 126 |
| :performance | 122 |
| :build-logic-settings | 121 |
| :kotlin-dsl-integ-tests | 119 |
| :distributions-basics | 113 |
| :distributions-full | 113 |
| :base-services | 112 |
| :process-services-base | 105 |
| :internal-build-reports | 96 |
| :tooling-api | 89 |
| :kotlin-dsl-plugins | 88 |
| :public-api | 86 |
| :enterprise-plugin-performance | 83 |
| :api-metadata | 75 |
| :encryption-services | 72 |
| :configuration-cache | 71 |
| :build-configuration | 68 |
| :client-services | 63 |
| :build-logic-settings:build-environment | 62 |
| :graph-serialization | 62 |
| :core | 58 |
| :java-compiler-worker | 58 |
| :base-diagnostics | 57 |
| :kotlin-dsl-provider-plugins | 56 |
| :ant-api | 55 |
| :ant-worker | 54 |

## Tasks (650 executed, sum 60135 ms)

| Task | ms |
|---|---|
| :docs:ideProjectModel | 1085 |
| :configuration-cache:ideProjectModel | 744 |
| :ant-impl:ideProjectModel | 700 |
| :antlr:ideProjectModel | 696 |
| :plugins-application:ideProjectModel | 676 |
| :tooling-api:ideProjectModel | 599 |
| :base-diagnostics:ideProjectModel | 596 |
| :core:ideProjectModel | 590 |
| :enterprise-plugin-performance:ideProjectModel | 590 |
| :bean-serialization-services:ideProjectModel | 553 |
| :tooling-native:ideProjectModel | 534 |
| :build-logic:cleanup:ideProjectModel | 478 |
| :build-logic:documentation:ideProjectModel | 478 |
| :build-logic:idea:ideProjectModel | 471 |
| :build-logic:binary-compatibility:ideProjectModel | 471 |
| :ant-api:ideProjectModel | 468 |
| :build-logic:dependency-modules:ideProjectModel | 467 |
| :dependency-management:ideProjectModel | 466 |
| :plugins-java:ideProjectModel | 461 |
| :architecture-test:ideProjectModel | 459 |
| :api-metadata:ideProjectModel | 455 |
| :ant:ideProjectModel | 451 |
| :ant-worker:ideProjectModel | 443 |
| :kotlin-dsl-tooling-builders:ideProjectModel | 434 |
| :execution:ideProjectModel | 420 |
| :performance:ideProjectModel | 400 |
| :build-logic:integration-testing-kotlin:ideProjectModel | 394 |
| :ivy:ideProjectModel | 388 |
| :internal-performance-testing:ideProjectModel | 386 |
| :language-java:ideProjectModel | 376 |
| :build-logic:buildquality:ideProjectModel | 368 |
| :classpath:ideProjectModel | 368 |
| :build-logic:jvm:ideProjectModel | 367 |
| :toolchains-jvm:ideProjectModel | 365 |
| :code-quality:ideProjectModel | 361 |
| :build-cache-core:ideProjectModel | 360 |
| :base-services:ideProjectModel | 357 |
| :maven:ideProjectModel | 344 |
| :declarative-dsl-provider:ideProjectModel | 343 |
| :plugins-groovy:ideProjectModel | 342 |
