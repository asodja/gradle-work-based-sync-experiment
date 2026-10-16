Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4c-tasks-only/4-trace-log.txt`

Overall span: 11098 ms, 608222 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 3485 | 3485 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 409 | 409 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 47 | 4 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 3894 ms, project: 47 ms

## Configure project (287 projects, sum 23538 ms)

| Project | ms |
|---|---|
| :antlr | 566 |
| :base-asm | 514 |
| :api-metadata | 512 |
| :ant-worker | 462 |
| :docs | 442 |
| :ant-impl | 380 |
| : | 347 |
| :ant-api | 340 |
| :base-compiler-worker | 308 |
| :architecture-test | 296 |
| :ant | 265 |
| :core | 258 |
| :declarative-dsl-core | 227 |
| :performance | 218 |
| :kotlin-dsl | 200 |
| :build-logic-commons:build-platform-plugin | 182 |
| :build-logic-commons:basics | 181 |
| :build-logic-commons:module-identity | 180 |
| :build-logic-commons:publishing | 180 |
| :declarative-dsl-evaluator | 173 |
| :kotlin-dsl-provider-plugins | 137 |
| :snapshots | 136 |
| :flow-services | 135 |
| :project-features-api | 135 |
| :soak | 133 |
| :platform-base | 125 |
| :core-serialization-codecs | 123 |
| :project-features-demos | 123 |
| :build-logic | 122 |
| :daemon-server | 121 |
| :file-watching | 121 |
| :launcher | 121 |
| :configuration-cache | 118 |
| :isolated-action-services | 117 |
| :files | 116 |
| :publish | 116 |
| :file-temp | 114 |
| :configuration-problems-base | 113 |
| :project-features | 112 |
| :stdlib-java-extensions | 112 |

## Tasks (650 executed, sum 1383 ms)

| Task | ms |
|---|---|
| :build-logic:uber-plugins:compileKotlin | 32 |
| :build-logic:documentation:compileGroovy | 31 |
| :build-logic:integration-testing:compileKotlin | 31 |
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 28 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 21 |
| :build-logic:buildquality:compileKotlin | 21 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 19 |
| :build-logic:packaging:compileKotlin | 18 |
| :architecture-test:ideProjectModel | 16 |
| :build-logic-settings:ideProjectModel | 15 |
| :java-platform:ideProjectModel | 15 |
| :kotlin-dsl:ideProjectModel | 15 |
| :javadoc:ideProjectModel | 15 |
| :build-logic:buildquality:generateExternalPluginSpecBuilders | 14 |
| :build-logic-commons:basics:ideProjectModel | 14 |
| :ant-worker:ideProjectModel | 14 |
| :antlr:ideProjectModel | 14 |
| :api-metadata:ideProjectModel | 14 |
| :dependency-management-serialization-codecs:ideProjectModel | 14 |
| :kotlin-dsl-provider-plugins:ideProjectModel | 13 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 12 |
| :build-logic-commons:gradle-plugin:compileKotlin | 12 |
| :build-logic:dependency-modules:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic:kotlin-dsl:compileKotlin | 12 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic:jvm:compileKotlin | 11 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:profiling:compileKotlin | 11 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:integration-testing-kotlin:compileKotlin | 11 |
| :kotlin-dsl-integ-tests:ideProjectModel | 11 |
| :normalization:ideProjectModel | 11 |
| :build-logic:idea:compileKotlin | 10 |
| :build-logic:buildquality:compilePluginsBlocks | 10 |
| :build-logic:packaging:generatePrecompiledScriptPluginAccessors | 10 |
| :jvm-services:ideProjectModel | 10 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 9 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 9 |
| :build-logic:lifecycle:compileKotlin | 9 |
| :build-logic:root-build:compileKotlin | 9 |
