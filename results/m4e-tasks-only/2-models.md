Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4e-tasks-only/2-trace-log.txt`

Overall span: 19320 ms, 608930 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 9119 | 9119 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 297 | 297 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 21 | 2 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 9416 ms, project: 21 ms

## Configure project (287 projects, sum 8055 ms)

| Project | ms |
|---|---|
| : | 374 |
| :ant | 208 |
| :docs | 138 |
| :java-compiler-worker | 132 |
| :build-logic | 81 |
| :plugins-version-catalog | 81 |
| :test-kit | 76 |
| :java-platform | 71 |
| :performance | 68 |
| :enterprise | 66 |
| :declarative-dsl-core | 59 |
| :javadoc | 51 |
| :build-operations | 48 |
| :cli | 48 |
| :kotlin-dsl-integ-tests | 48 |
| :kotlin-dsl | 47 |
| :precondition-tester | 43 |
| :process-services-base | 43 |
| :kotlin-dsl-plugins | 39 |
| :configuration-cache | 38 |
| :kotlin-dsl-tooling-builders | 37 |
| :jvm-compiler-worker | 35 |
| :problems | 34 |
| :build-init-specs-api | 33 |
| :declarative-dsl-tooling-builders | 33 |
| :graph-serialization | 33 |
| :kotlin-dsl-tooling-model-impls | 33 |
| :maven | 33 |
| :bean-serialization-services | 32 |
| :jvm-services | 32 |
| :kotlin-dsl-provider-plugins | 32 |
| :language-jvm | 32 |
| :wrapper-main | 32 |
| :build-option | 31 |
| :declarative-dsl-evaluator | 31 |
| :project-features | 31 |
| :build-cache-spi | 30 |
| :core-serialization-codecs | 30 |
| :declarative-dsl-tooling-models | 30 |
| :enterprise-workers | 30 |

## Tasks (650 executed, sum 76028 ms)

| Task | ms |
|---|---|
| :ant-impl:ideProjectModel | 3316 |
| :antlr:ideProjectModel | 3188 |
| :ideProjectModel | 3036 |
| :build-logic:ideProjectModel | 3027 |
| :build-logic-commons:ideProjectModel | 3025 |
| :build-logic:binary-compatibility:ideProjectModel | 3022 |
| :build-logic-settings:ideProjectModel | 3021 |
| :ant:ideProjectModel | 3018 |
| :ant-api:ideProjectModel | 3014 |
| :ant-worker:ideProjectModel | 3000 |
| :api-metadata:ideProjectModel | 2953 |
| :docs:ideProjectModel | 800 |
| :plugins-java:ideProjectModel | 602 |
| :configuration-cache:ideProjectModel | 558 |
| :kotlin-dsl-tooling-builders:ideProjectModel | 532 |
| :language-java:ideProjectModel | 499 |
| :core:ideProjectModel | 488 |
| :dependency-management:ideProjectModel | 487 |
| :integ-test:ideProjectModel | 443 |
| :enterprise-plugin-performance:ideProjectModel | 432 |
| :tooling-native:ideProjectModel | 426 |
| :kotlin-dsl-integ-tests:ideProjectModel | 421 |
| :model-core:ideProjectModel | 387 |
| :toolchains-jvm:ideProjectModel | 373 |
| :declarative-dsl-provider:ideProjectModel | 354 |
| :language-native:ideProjectModel | 336 |
| :build-cache-http:ideProjectModel | 333 |
| :platform-base:ideProjectModel | 331 |
| :base-diagnostics:ideProjectModel | 326 |
| :core-serialization-codecs:ideProjectModel | 320 |
| :maven:ideProjectModel | 320 |
| :kotlin-dsl-plugins:ideProjectModel | 319 |
| :tooling-api:ideProjectModel | 317 |
| :internal-integ-testing:ideProjectModel | 309 |
| :model-groovy:ideProjectModel | 302 |
| :platform-jvm:ideProjectModel | 297 |
| :file-collections:ideProjectModel | 288 |
| :classpath:ideProjectModel | 286 |
| :kotlin-dsl:ideProjectModel | 285 |
| :ide-plugins:ideProjectModel | 279 |
