Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4c-tasks-only/2-trace-log.txt`

Overall span: 15302 ms, 607921 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 8971 | 8971 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 318 | 318 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 40 | 7 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 9289 ms, project: 40 ms

## Configure project (287 projects, sum 7886 ms)

| Project | ms |
|---|---|
| : | 324 |
| :ant | 226 |
| :docs | 156 |
| :build-logic | 109 |
| :performance | 95 |
| :declarative-dsl-core | 65 |
| :toolchains-jvm-shared | 58 |
| :kotlin-dsl | 57 |
| :base-services | 53 |
| :jacoco | 50 |
| :project-features-api | 50 |
| :graph-isolation | 49 |
| :build-cache-spi | 48 |
| :model-core | 48 |
| :daemon-logging | 47 |
| :worker-process-services | 47 |
| :base-asm | 46 |
| :problems | 46 |
| :bean-serialization-services | 45 |
| :resources-gcs | 44 |
| :classpath | 43 |
| :architecture-test | 42 |
| :core-flow-services-api | 42 |
| :platform-base | 42 |
| :execution-e2e-tests | 41 |
| :plugins-distribution | 41 |
| :stdlib-java-extensions | 41 |
| :language-java | 40 |
| :wrapper-main | 40 |
| :declarative-dsl-api | 39 |
| :resources-sftp | 39 |
| :workers | 39 |
| :internal-distribution-testing | 38 |
| :ide-model-impls | 37 |
| :kotlin-dsl-integ-tests | 37 |
| :security | 36 |
| :build-cache | 35 |
| :docs-asciidoctor-extensions-base | 35 |
| :tooling-api | 35 |
| :core-kotlin-extensions | 34 |

## Tasks (650 executed, sum 37606 ms)

| Task | ms |
|---|---|
| :ant-impl:ideProjectModel | 607 |
| :antlr:ideProjectModel | 607 |
| :configuration-cache:ideProjectModel | 441 |
| :docs:ideProjectModel | 439 |
| :base-diagnostics:ideProjectModel | 413 |
| :language-java:ideProjectModel | 402 |
| :plugins-java:ideProjectModel | 369 |
| :java-platform:ideProjectModel | 342 |
| :build-configuration:ideProjectModel | 328 |
| :declarative-dsl-provider:ideProjectModel | 326 |
| :maven:ideProjectModel | 317 |
| :dependency-management:ideProjectModel | 306 |
| :model-core:ideProjectModel | 306 |
| :plugins-groovy:ideProjectModel | 306 |
| :build-cache-http:ideProjectModel | 305 |
| :plugins-application:ideProjectModel | 296 |
| :enterprise-plugin-performance:ideProjectModel | 292 |
| :testing-base:ideProjectModel | 289 |
| :language-native:ideProjectModel | 283 |
| :build-init:ideProjectModel | 277 |
| :base-services:ideProjectModel | 275 |
| :ivy:ideProjectModel | 273 |
| :ant-worker:ideProjectModel | 271 |
| :ant:ideProjectModel | 262 |
| :plugins-model-native:ideProjectModel | 261 |
| :architecture-test:ideProjectModel | 257 |
| :kotlin-dsl:ideProjectModel | 256 |
| :toolchains-jvm:ideProjectModel | 256 |
| :execution:ideProjectModel | 252 |
| :performance:ideProjectModel | 252 |
| :wrapper-main:ideProjectModel | 249 |
| :build-cache:ideProjectModel | 248 |
| :api-metadata:ideProjectModel | 246 |
| :ide:ideProjectModel | 246 |
| :launcher:ideProjectModel | 240 |
| :project-features-demos:ideProjectModel | 238 |
| :classpath:ideProjectModel | 237 |
| :project-features:ideProjectModel | 237 |
| :kotlin-dsl-tooling-builders:ideProjectModel | 233 |
| :smoke-test:ideProjectModel | 231 |
