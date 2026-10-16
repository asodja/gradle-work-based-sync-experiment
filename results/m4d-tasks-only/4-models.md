Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4d-tasks-only/4-trace-log.txt`

Overall span: 21592 ms, 609830 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 5252 | 5252 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 225 | 225 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 47 | 3 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 5477 ms, project: 47 ms

## Configure project (287 projects, sum 19668 ms)

| Project | ms |
|---|---|
| :antlr | 476 |
| :api-metadata | 474 |
| :base-compiler-worker | 441 |
| :base-asm | 416 |
| :ant | 373 |
| :architecture-test | 363 |
| :ant-api | 343 |
| :docs | 330 |
| :ant-impl | 316 |
| :ant-worker | 309 |
| : | 289 |
| :core | 248 |
| :performance | 194 |
| :maven | 165 |
| :scoped-persistent-cache | 158 |
| :model-reflect | 154 |
| :service-registry-builder | 150 |
| :service-lookup | 147 |
| :normalization-api | 145 |
| :guava-serialization-codecs | 140 |
| :security | 140 |
| :graph-isolation | 136 |
| :service-registry-impl | 136 |
| :serialization | 134 |
| :hashing | 133 |
| :normalization | 132 |
| :service-provider | 132 |
| :signing | 128 |
| :groovy-compiler-worker | 126 |
| :graph-serialization | 125 |
| :normalization-java | 121 |
| :model-core | 118 |
| :core-kotlin-extensions | 117 |
| :daemon-logging | 117 |
| :native | 117 |
| :declarative-dsl-core | 113 |
| :hashing-services | 112 |
| :daemon-messaging | 102 |
| :ide | 102 |
| :declarative-dsl-evaluator | 98 |

## Tasks (650 executed, sum 1344 ms)

| Task | ms |
|---|---|
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 49 |
| :build-logic:uber-plugins:compileKotlin | 34 |
| :build-logic:cleanup:compileKotlin | 30 |
| :build-logic:jvm:compilePluginsBlocks | 27 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 26 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 26 |
| :build-logic:buildquality:compileKotlin | 26 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 24 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 23 |
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 23 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 21 |
| :build-logic:kotlin-dsl:compileKotlin | 17 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 16 |
| :build-logic:performance-testing:compileKotlin | 15 |
| :build-logic:packaging:compilePluginsBlocks | 14 |
| :build-logic:integration-testing:compileKotlin | 13 |
| :build-logic-settings:version-catalogs:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic:packaging:compileKotlin | 12 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 12 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:documentation:compileGroovy | 11 |
| :build-logic:profiling:compileKotlin | 11 |
| :build-logic:performance-testing:extractPluginRequests | 11 |
| :build-logic:binary-compatibility:compileJava | 11 |
| :build-logic-settings:architecture-docs:generateExternalPluginSpecBuilders | 10 |
| :build-logic:jvm:compileKotlin | 10 |
| :build-logic:performance-testing:compileGroovy | 10 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 9 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 9 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:integration-testing-kotlin:compileKotlin | 9 |
| :build-logic:build-update-utils:generateExternalPluginSpecBuilders | 8 |
| :build-logic:root-build:compileKotlin | 8 |
| :build-logic:binary-compatibility:compileKotlin | 8 |
| :build-logic:kotlin-dsl:generateExternalPluginSpecBuilders | 8 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 8 |
| :build-logic:packaging:generatePrecompiledScriptPluginAccessors | 8 |
