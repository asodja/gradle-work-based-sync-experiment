Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4b-tasks-only/2-trace-log.txt`

Overall span: 22480 ms, 608488 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 12319 | 12319 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 536 | 536 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 39 | 6 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 1 | 1 | 0 |

Totals by scope: default: 12856 ms, project: 39 ms

## Configure project (287 projects, sum 10167 ms)

| Project | ms |
|---|---|
| : | 516 |
| :build-logic | 411 |
| :ant | 266 |
| :docs | 175 |
| :versioned-cache | 101 |
| :declarative-dsl-core | 95 |
| :kotlin-dsl | 81 |
| :samples | 80 |
| :performance | 79 |
| :base-services | 74 |
| :graph-serialization | 69 |
| :stdlib-serialization-codecs | 68 |
| :credentials-impl | 65 |
| :base-asm | 64 |
| :messaging | 63 |
| :enterprise-plugin-performance | 62 |
| :core-flow-services-api | 61 |
| :base-services-groovy | 60 |
| :classpath | 59 |
| :instrumentation-agent-services | 57 |
| :tooling-api | 54 |
| :architecture-test | 53 |
| :toolchains-jvm | 53 |
| :kotlin-dsl-provider-plugins | 52 |
| :plugins-java-library | 52 |
| :build-cache-spi | 51 |
| :plugins-jvm-test-suite | 50 |
| :platform-jvm | 49 |
| :war | 49 |
| :ivy | 48 |
| :declarative-dsl-tooling-models | 47 |
| :kotlin-dsl-integ-tests | 47 |
| :problems-reporting | 46 |
| :bean-serialization-services | 45 |
| :wrapper-main | 45 |
| :declarative-dsl-evaluator | 44 |
| :distributions-integ-tests | 44 |
| :version-control | 44 |
| :core-kotlin-extensions | 43 |
| :jvm-services | 43 |

## Tasks (650 executed, sum 58176 ms)

| Task | ms |
|---|---|
| :ant-impl:ideProjectModel | 858 |
| :antlr:ideProjectModel | 824 |
| :plugins-java:ideProjectModel | 697 |
| :configuration-cache:ideProjectModel | 672 |
| :enterprise-plugin-performance:ideProjectModel | 671 |
| :execution:ideProjectModel | 606 |
| :core:ideProjectModel | 581 |
| :performance:ideProjectModel | 549 |
| :base-diagnostics:ideProjectModel | 525 |
| :plugins-model-native:ideProjectModel | 515 |
| :integ-test:ideProjectModel | 484 |
| :tooling-native:ideProjectModel | 483 |
| :scala:ideProjectModel | 477 |
| :docs:ideProjectModel | 467 |
| :ant:ideProjectModel | 458 |
| :dependency-management:ideProjectModel | 441 |
| :language-java:ideProjectModel | 435 |
| :base-services:ideProjectModel | 423 |
| :ant-api:ideProjectModel | 412 |
| :ear:ideProjectModel | 407 |
| :plugin-development:ideProjectModel | 404 |
| :internal-integ-testing:ideProjectModel | 403 |
| :base-ide-plugins:ideProjectModel | 402 |
| :plugins-jvm-test-fixtures:ideProjectModel | 401 |
| :build-cache-http:ideProjectModel | 399 |
| :kotlin-dsl-tooling-builders:ideProjectModel | 396 |
| :ide-plugins:ideProjectModel | 388 |
| :smoke-test:ideProjectModel | 388 |
| :jacoco:ideProjectModel | 383 |
| :declarative-dsl-provider:ideProjectModel | 380 |
| :file-watching:ideProjectModel | 380 |
| :plugins-jvm-test-suite:ideProjectModel | 376 |
| :build-configuration:ideProjectModel | 368 |
| :plugins-groovy:ideProjectModel | 354 |
| :kotlin-dsl-integ-tests:ideProjectModel | 353 |
| :toolchains-jvm:ideProjectModel | 350 |
| :classpath:ideProjectModel | 349 |
| :internal-performance-testing:ideProjectModel | 349 |
| :code-quality:ideProjectModel | 345 |
| :platform-native:ideProjectModel | 345 |
