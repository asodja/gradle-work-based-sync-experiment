Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4h-tasks-only/4-trace-log.txt`

Overall span: 15520 ms, 609997 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 7657 | 7657 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 222 | 222 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 18 | 2 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 7879 ms, project: 18 ms

## Configure project (287 projects, sum 25284 ms)

| Project | ms |
|---|---|
| :base-compiler-worker | 502 |
| :api-metadata | 482 |
| :docs | 474 |
| :ant | 461 |
| :ant-impl | 426 |
| :core | 411 |
| :ivy | 375 |
| :isolated-action-services | 354 |
| :jacoco-workers | 354 |
| :ant-worker | 349 |
| :jacoco | 337 |
| :java-api-extractor | 337 |
| :java-compiler-plugin | 328 |
| :antlr | 318 |
| :java-platform | 316 |
| : | 309 |
| :java-compiler-worker | 307 |
| :declarative-dsl-core | 290 |
| :ant-api | 283 |
| :base-asm | 267 |
| :architecture-test | 244 |
| :performance | 181 |
| :toolchains-jvm | 171 |
| :core-serialization-codecs | 156 |
| :gradle-cli-main | 150 |
| :daemon-services | 149 |
| :files | 139 |
| :distributions-full | 138 |
| :build-cache-core | 135 |
| :enterprise-logging | 135 |
| :tooling-native-model-impls | 133 |
| :daemon-server | 132 |
| :normalization | 129 |
| :unit-test-fixtures | 129 |
| :core-api | 128 |
| :flow-services | 128 |
| :tooling-native | 128 |
| :persistent-cache | 122 |
| :request-handler-worker | 121 |
| :composite-builds | 120 |

## Tasks (650 executed, sum 1476 ms)

| Task | ms |
|---|---|
| :build-logic:idea:generateExternalPluginSpecBuilders | 54 |
| :build-logic:integration-testing-kotlin:compileKotlin | 38 |
| :build-logic:uber-plugins:compileKotlin | 32 |
| :build-logic-settings:build-environment:checkKotlinCompilerVersion | 26 |
| :build-logic-settings:default-settings-plugins:checkKotlinCompilerVersion | 25 |
| :build-logic-settings:architecture-docs:checkKotlinCompilerVersion | 25 |
| :build-logic-settings:configuration-cache-compatibility:checkKotlinCompilerVersion | 25 |
| :build-logic:binary-compatibility:compileKotlin | 25 |
| :build-logic:lifecycle:generatePrecompiledScriptPluginAccessors | 24 |
| :build-logic:kotlin-dsl:generateExternalPluginSpecBuilders | 24 |
| :build-logic:performance-testing:compileKotlin | 24 |
| :build-logic-commons:publishing:generatePrecompiledScriptPluginAccessors | 22 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 21 |
| :build-logic:cleanup:compilePluginsBlocks | 20 |
| :build-logic:lifecycle:generateExternalPluginSpecBuilders | 19 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 17 |
| :build-logic:integration-testing:compileKotlin | 15 |
| :build-logic:performance-testing:compileGroovy | 15 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 14 |
| :build-logic:documentation:compileGroovy | 14 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 14 |
| :build-logic:buildquality:compileKotlin | 14 |
| :build-logic:dependency-modules:compileKotlin | 13 |
| :build-logic:packaging:compileKotlin | 12 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 11 |
| :build-logic:profiling:compileKotlin | 11 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 10 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 10 |
| :build-logic-commons:gradle-plugin:compileKotlin | 10 |
| :build-logic:jvm:compileKotlin | 10 |
| :build-logic:kotlin-dsl:compileKotlin | 10 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 9 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 9 |
| :build-logic:cleanup:compileJava | 9 |
| :build-logic:lifecycle:compileKotlin | 9 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 8 |
| :build-logic:jvm:compilePluginsBlocks | 8 |
| :build-logic:idea:compilePluginsBlocks | 8 |
