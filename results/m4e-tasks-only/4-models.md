Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4e-tasks-only/4-trace-log.txt`

Overall span: 20874 ms, 609938 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 4701 | 4701 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 246 | 246 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 38 | 2 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 4947 ms, project: 38 ms

## Configure project (287 projects, sum 21211 ms)

| Project | ms |
|---|---|
| :architecture-test | 530 |
| :base-compiler-worker | 462 |
| :ant | 426 |
| :ant-api | 363 |
| :antlr | 340 |
| :docs | 331 |
| : | 321 |
| :api-metadata | 319 |
| :ant-worker | 303 |
| :ant-impl | 286 |
| :base-asm | 277 |
| :core | 234 |
| :declarative-dsl-core | 165 |
| :tooling-native | 159 |
| :configuration-problems-base | 156 |
| :kotlin-dsl | 152 |
| :toolchains-jvm | 151 |
| :configuration-cache | 148 |
| :tooling-api | 147 |
| :problems | 144 |
| :precondition-tester | 143 |
| :toolchains-jvm-shared | 143 |
| :time | 142 |
| :problems-rendering | 141 |
| :tooling-native-model-impls | 138 |
| :problems-reporting | 131 |
| :tooling-api-builders | 131 |
| :tooling-api-provider | 131 |
| :java-compiler-plugin | 128 |
| :isolated-ant-builder | 126 |
| :ivy | 126 |
| :process-memory-services | 126 |
| :problems-api | 125 |
| :problems-impl | 124 |
| :composite-builds | 123 |
| :ear | 123 |
| :jacoco-workers | 119 |
| :javadoc | 118 |
| :code-quality-workers | 115 |
| :enterprise-workers | 113 |

## Tasks (650 executed, sum 1370 ms)

| Task | ms |
|---|---|
| :build-logic:uber-plugins:compileKotlin | 36 |
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 35 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 32 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 32 |
| :build-logic:integration-testing-kotlin:compileKotlin | 25 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 24 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 22 |
| :build-logic:integration-testing:compilePluginsBlocks | 22 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 21 |
| :build-logic-settings:build-environment:generatePrecompiledScriptPluginAccessors | 20 |
| :build-logic:dependency-modules:generatePrecompiledScriptPluginAccessors | 19 |
| :build-logic:lifecycle:compilePluginsBlocks | 17 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 16 |
| :build-logic:integration-testing:compileKotlin | 16 |
| :build-logic:buildquality:compileKotlin | 16 |
| :build-logic:documentation:compileGroovy | 15 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 14 |
| :build-logic-settings:build-environment:compileKotlin | 14 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 13 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 13 |
| :build-logic:profiling:compileKotlin | 13 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 13 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 13 |
| :build-logic:packaging:compileKotlin | 13 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 13 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 11 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:jvm:compileKotlin | 11 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:kotlin-dsl:compileKotlin | 11 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 10 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic:integration-testing-kotlin:generateExternalPluginSpecBuilders | 10 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 9 |
| :build-logic:root-build:compileKotlin | 9 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 8 |
| :build-logic:idea:compileKotlin | 8 |
| :build-logic:dependency-modules:compileKotlin | 8 |
| :build-logic:build-update-utils:compileKotlin | 8 |
