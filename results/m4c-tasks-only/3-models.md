Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4c-tasks-only/3-trace-log.txt`

Overall span: 11060 ms, 619290 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 3829 | 3829 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 325 | 325 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 49 | 6 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 4154 ms, project: 49 ms

## Configure project (287 projects, sum 26291 ms)

| Project | ms |
|---|---|
| :kotlin-dsl | 594 |
| :architecture-test | 584 |
| :ant-impl | 473 |
| :kotlin-dsl-integ-tests | 469 |
| :api-metadata | 457 |
| :base-compiler-worker | 426 |
| :docs | 407 |
| :declarative-dsl-core | 393 |
| :ant | 381 |
| : | 354 |
| :base-asm | 348 |
| :internal-build-reports | 323 |
| :antlr | 320 |
| :ant-api | 320 |
| :ant-worker | 319 |
| :declarative-dsl-evaluator | 278 |
| :distributions-full | 272 |
| :performance | 268 |
| :tooling-api | 238 |
| :distributions-basics | 206 |
| :distributions-core | 205 |
| :build-cache | 204 |
| :distributions-jvm | 165 |
| :distributions-native | 164 |
| :build-cache-spi | 157 |
| :distributions-publishing | 157 |
| :graph-serialization | 148 |
| :kotlin-dsl-plugins | 144 |
| :kotlin-dsl-tooling-model-impls | 141 |
| :core-serialization-codecs | 140 |
| :wrapper-main | 139 |
| :bean-serialization-services | 138 |
| :credentials-api | 137 |
| :core | 135 |
| :credentials-impl | 135 |
| :war | 132 |
| :tooling-api-builders | 131 |
| :unit-test-fixtures | 129 |
| :worker-main | 129 |
| :credentials | 127 |

## Tasks (650 executed, sum 1360 ms)

| Task | ms |
|---|---|
| :build-logic:uber-plugins:compileKotlin | 32 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 29 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 20 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 18 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 18 |
| :build-logic:documentation:compileGroovy | 17 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 16 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 16 |
| :build-logic:lifecycle:compilePluginsBlocks | 15 |
| :build-logic:integration-testing:compileKotlin | 15 |
| :build-logic:buildquality:compileKotlin | 15 |
| :build-logic:idea:compilePluginsBlocks | 14 |
| :build-logic:packaging:compileKotlin | 14 |
| :build-logic:cleanup:compilePluginsBlocks | 12 |
| :build-logic:cleanup:compileJava | 12 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 12 |
| :build-logic:profiling:compileKotlin | 12 |
| :build-logic-settings:version-catalogs:compileKotlin | 11 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 11 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:binary-compatibility:compileKotlin | 11 |
| :build-logic:kotlin-dsl:compileKotlin | 11 |
| :build-logic:integration-testing-kotlin:compileKotlin | 11 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic:lifecycle:compileKotlin | 10 |
| :build-logic:jvm:compileKotlin | 10 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic:performance-testing:compileKotlin | 10 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 10 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 9 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 9 |
| :build-logic:idea:compileKotlin | 9 |
| :build-logic-commons:publishing:generateExternalPluginSpecBuilders | 9 |
| :build-logic:buildquality:compilePluginsBlocks | 9 |
| :build-logic:uber-plugins:compilePluginsBlocks | 9 |
| :build-logic:cleanup:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:jvm:compilePluginsBlocks | 8 |
| :build-logic:build-update-utils:generateExternalPluginSpecBuilders | 8 |
