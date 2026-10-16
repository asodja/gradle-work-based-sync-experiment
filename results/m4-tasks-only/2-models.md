Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4-tasks-only/2-trace-log.txt`

Overall span: 15044 ms, 608166 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 8588 | 8588 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 489 | 489 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 57 | 2 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 9077 ms, project: 57 ms

## Configure project (287 projects, sum 7827 ms)

| Project | ms |
|---|---|
| : | 327 |
| :ant | 269 |
| :build-logic-commons:basics | 178 |
| :docs | 166 |
| :build-logic | 107 |
| :performance | 98 |
| :declarative-dsl-core | 76 |
| :soak | 59 |
| :tooling-api | 54 |
| :declarative-dsl-evaluator | 52 |
| :internal-instrumentation-api | 50 |
| :architecture-test | 49 |
| :kotlin-dsl-provider-plugins | 49 |
| :functional | 47 |
| :resources-gcs | 47 |
| :problems-api | 46 |
| :configuration-cache | 45 |
| :declarative-dsl-provider | 44 |
| :core-serialization-codecs | 42 |
| :base-services | 41 |
| :guava-serialization-codecs | 41 |
| :build-cache-packaging | 40 |
| :kotlin-dsl | 40 |
| :build-option | 39 |
| :daemon-services | 39 |
| :declarative-dsl-tooling-models | 39 |
| :collections | 38 |
| :plugins-java | 38 |
| :graph-serialization | 37 |
| :kotlin-dsl-integ-tests | 36 |
| :cli | 35 |
| :declarative-dsl-tooling-builders | 35 |
| :bean-serialization-services | 34 |
| :graph-isolation | 34 |
| :kotlin-dsl-plugins | 34 |
| :encryption-services | 33 |
| :testing-native | 33 |
| :toolchains-jvm | 33 |
| :base-diagnostics | 32 |
| :ant-api | 31 |

## Tasks (650 executed, sum 37462 ms)

| Task | ms |
|---|---|
| :antlr:ideProjectModel | 610 |
| :ant-impl:ideProjectModel | 493 |
| :docs:ideProjectModel | 441 |
| :configuration-cache:ideProjectModel | 425 |
| :dependency-management:ideProjectModel | 416 |
| :plugins-java:ideProjectModel | 409 |
| :kotlin-dsl-tooling-builders:ideProjectModel | 379 |
| :build-configuration:ideProjectModel | 369 |
| :base-services:ideProjectModel | 349 |
| :ide:ideProjectModel | 349 |
| :java-platform:ideProjectModel | 337 |
| :base-diagnostics:ideProjectModel | 323 |
| :enterprise-plugin-performance:ideProjectModel | 322 |
| :execution:ideProjectModel | 322 |
| :language-java:ideProjectModel | 309 |
| :core:ideProjectModel | 292 |
| :file-watching:ideProjectModel | 287 |
| :language-native:ideProjectModel | 281 |
| :internal-integ-testing:ideProjectModel | 271 |
| :base-ide-plugins:ideProjectModel | 270 |
| :build-init:ideProjectModel | 268 |
| :plugin-development:ideProjectModel | 265 |
| :tooling-api:ideProjectModel | 259 |
| :declarative-dsl-provider:ideProjectModel | 253 |
| :software-diagnostics:ideProjectModel | 244 |
| :ant-api:ideProjectModel | 235 |
| :ivy:ideProjectModel | 234 |
| :platform-native:ideProjectModel | 233 |
| :plugins-groovy:ideProjectModel | 226 |
| :testing-base:ideProjectModel | 226 |
| :maven:ideProjectModel | 223 |
| :integ-test:ideProjectModel | 221 |
| :performance:ideProjectModel | 216 |
| :code-quality:ideProjectModel | 214 |
| :version-control:ideProjectModel | 210 |
| :architecture-test:ideProjectModel | 209 |
| :platform-jvm:ideProjectModel | 208 |
| :daemon-server:ideProjectModel | 206 |
| :api-metadata:ideProjectModel | 204 |
| :testing-jvm:ideProjectModel | 203 |
