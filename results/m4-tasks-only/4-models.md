Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4-tasks-only/4-trace-log.txt`

Overall span: 11092 ms, 608499 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 4184 | 4184 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 250 | 250 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 37 | 2 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 4434 ms, project: 37 ms

## Configure project (287 projects, sum 19185 ms)

| Project | ms |
|---|---|
| :api-metadata | 455 |
| :ant-api | 433 |
| :base-asm | 384 |
| :architecture-test | 366 |
| :ant | 340 |
| : | 323 |
| :ant-worker | 313 |
| :docs | 305 |
| :antlr | 302 |
| :base-compiler-worker | 285 |
| :ant-impl | 280 |
| :core | 247 |
| :declarative-dsl-core | 212 |
| :resources-s3 | 141 |
| :performance | 140 |
| :graph-serialization | 139 |
| :model-groovy | 139 |
| :logging-api | 137 |
| :language-native | 136 |
| :maven | 136 |
| :logging | 135 |
| :model-core | 134 |
| :resources | 134 |
| :graph-isolation | 133 |
| :resources-sftp | 133 |
| :resources-gcs | 129 |
| :groovy-compiler-worker | 128 |
| :file-watching | 125 |
| :functional | 125 |
| :gradle-cli-main | 122 |
| :kotlin-dsl | 121 |
| :gradle-cli | 120 |
| :launcher | 120 |
| :samples | 115 |
| :configuration-cache | 111 |
| :core-kotlin-extensions | 107 |
| :messaging | 106 |
| :resources-http | 106 |
| :core-flow-services-api | 104 |
| :project-features-demos | 103 |

## Tasks (650 executed, sum 1473 ms)

| Task | ms |
|---|---|
| :build-logic:uber-plugins:compileKotlin | 46 |
| :build-logic:integration-testing-kotlin:compileKotlin | 30 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 26 |
| :build-logic:build-update-utils:compileKotlin | 25 |
| :build-logic:idea:generateExternalPluginSpecBuilders | 24 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 21 |
| :build-profile:ideProjectModel | 19 |
| :build-logic:integration-testing:compileKotlin | 18 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 18 |
| :collections:ideProjectModel | 18 |
| :client-services:ideProjectModel | 17 |
| :code-quality:ideProjectModel | 17 |
| :code-quality-workers:ideProjectModel | 17 |
| :composite-builds:ideProjectModel | 17 |
| :concurrent:ideProjectModel | 17 |
| :configuration-cache:ideProjectModel | 17 |
| :build-logic:documentation:compileGroovy | 16 |
| :build-logic:buildquality:compileKotlin | 15 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 12 |
| :build-logic-commons:gradle-plugin:compileKotlin | 12 |
| :build-logic:profiling:compileKotlin | 12 |
| :build-logic:packaging:compileKotlin | 12 |
| :build-logic-settings:version-catalogs:compileKotlin | 11 |
| :build-logic:dependency-modules:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:lifecycle:compilePluginsBlocks | 11 |
| :build-logic:cleanup:compileKotlin | 11 |
| :build-logic:jvm:compileKotlin | 11 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 11 |
| :distributions-integ-tests:ideProjectModel | 11 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 10 |
| :build-logic-commons:publishing:compileKotlin | 10 |
| :build-logic:kotlin-dsl:compileKotlin | 10 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 9 |
| :build-logic-settings:build-environment:compileKotlin | 9 |
| :build-logic:cleanup:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:dependency-modules:compileKotlin | 9 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:root-build:compileKotlin | 9 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 9 |
