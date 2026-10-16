Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4-tasks-only/7-trace-log.txt`

Overall span: 11773 ms, 608836 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 4625 | 4625 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 429 | 429 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 52 | 2 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 5054 ms, project: 52 ms

## Configure project (287 projects, sum 24229 ms)

| Project | ms |
|---|---|
| :ant | 518 |
| :ant-worker | 509 |
| :docs | 509 |
| :architecture-test | 444 |
| :base-asm | 409 |
| :ant-impl | 373 |
| : | 339 |
| :maven | 333 |
| :normalization | 322 |
| :native | 319 |
| :api-metadata | 315 |
| :model-reflect | 310 |
| :model-core | 299 |
| :model-groovy | 296 |
| :declarative-dsl-core | 292 |
| :antlr | 291 |
| :normalization-api | 284 |
| :normalization-java | 275 |
| :base-compiler-worker | 267 |
| :ant-api | 264 |
| :performance | 206 |
| :kotlin-dsl | 173 |
| :toolchains-jvm | 168 |
| :tooling-native | 139 |
| :tooling-native-model-impls | 139 |
| :unit-test-fixtures | 136 |
| :distributions-full | 135 |
| :tooling-api-provider | 135 |
| :war | 132 |
| :dependency-management-serialization-codecs | 129 |
| :version-control | 128 |
| :versioned-cache | 126 |
| :kotlin-dsl-integ-tests | 121 |
| :kotlin-dsl-tooling-model-impls | 116 |
| :project-features-api | 116 |
| :project-features-demos | 114 |
| :declarative-dsl-api | 111 |
| :daemon-logging | 110 |
| :publish | 110 |
| :credentials-api | 109 |

## Tasks (650 executed, sum 1248 ms)

| Task | ms |
|---|---|
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 39 |
| :build-logic:uber-plugins:compileKotlin | 35 |
| :build-logic:packaging:compileKotlin | 31 |
| :build-logic:integration-testing:compilePluginsBlocks | 23 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 21 |
| :build-logic:build-update-utils:compilePluginsBlocks | 20 |
| :build-logic:integration-testing-kotlin:compileKotlin | 19 |
| :build-logic-commons:publishing:generateExternalPluginSpecBuilders | 18 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 18 |
| :build-logic:buildquality:compileKotlin | 17 |
| :build-logic:binary-compatibility:compileKotlin | 15 |
| :build-logic:idea:compileKotlin | 14 |
| :build-logic:integration-testing:compileKotlin | 14 |
| :build-logic:profiling:compileKotlin | 14 |
| :build-logic:lifecycle:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic:cleanup:compileJava | 12 |
| :build-logic:lifecycle:compileKotlin | 12 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 12 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:performance-testing:compileKotlin | 11 |
| :build-logic:kotlin-dsl:compileKotlin | 11 |
| :build-logic-commons:code-quality-rules:ideProjectModel | 11 |
| :build-logic-commons:gradle-plugin:ideProjectModel | 11 |
| :build-logic-commons:module-identity:ideProjectModel | 11 |
| :build-logic-commons:publishing:ideProjectModel | 11 |
| :build-logic-commons:ideProjectModel | 11 |
| :build-logic-commons:gradle-plugin:compileKotlin | 10 |
| :build-logic:build-update-utils:processResources | 10 |
| :build-logic:jvm:compileKotlin | 10 |
| :build-logic:documentation:compileGroovy | 10 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic:kotlin-dsl:generateExternalPluginSpecBuilders | 10 |
| :build-logic-commons:publishing:compileKotlin | 10 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 9 |
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 9 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:build-update-utils:generateExternalPluginSpecBuilders | 9 |
| :build-logic:root-build:compileKotlin | 9 |
| :build-logic-settings:configuration-cache-compatibility:ideProjectModel | 9 |
