Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4b-tasks-only/3-trace-log.txt`

Overall span: 16198 ms, 618939 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 6554 | 6554 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 506 | 506 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 44 | 9 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 7060 ms, project: 44 ms

## Configure project (287 projects, sum 38066 ms)

| Project | ms |
|---|---|
| :architecture-test | 971 |
| :declarative-dsl-core | 833 |
| :antlr | 775 |
| :ant-api | 685 |
| :docs | 604 |
| :ant | 599 |
| :ant-impl | 596 |
| :api-metadata | 592 |
| :base-asm | 590 |
| :ant-worker | 589 |
| :base-compiler-worker | 585 |
| : | 568 |
| :kotlin-dsl | 534 |
| :internal-build-reports | 411 |
| :enterprise-plugin-performance | 349 |
| :declarative-dsl-evaluator | 348 |
| :public-api | 346 |
| :kotlin-dsl-integ-tests | 334 |
| :distributions-basics | 330 |
| :performance | 323 |
| :distributions-core | 315 |
| :distributions-full | 313 |
| :distributions-jvm | 300 |
| :build-cache | 276 |
| :bean-serialization-services | 262 |
| :flow-services | 250 |
| :tooling-api | 247 |
| :file-temp | 244 |
| :file-watching | 234 |
| :distributions-native | 232 |
| :wrapper-main | 232 |
| :test-kit | 213 |
| :build-logic | 212 |
| :testing-jvm | 212 |
| :testing-base-infrastructure | 207 |
| :stdlib-serialization-codecs | 203 |
| :testing-jvm-infrastructure | 201 |
| :build-cache-base | 199 |
| :file-collections | 199 |
| :daemon-main | 198 |

## Tasks (650 executed, sum 2289 ms)

| Task | ms |
|---|---|
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 58 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 58 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 58 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 56 |
| :build-logic-commons:gradle-plugin:compilePluginsBlocks | 53 |
| :build-logic:uber-plugins:compileKotlin | 46 |
| :build-logic-settings:build-environment:ideProjectModel | 42 |
| :build-cache-local:ideProjectModel | 42 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 40 |
| :build-logic:binary-compatibility:compileKotlin | 40 |
| :build-logic-settings:configuration-cache-compatibility:ideProjectModel | 40 |
| :build-logic-settings:default-settings-plugins:ideProjectModel | 40 |
| :build-logic-settings:version-catalogs:ideProjectModel | 40 |
| :build-logic-settings:architecture-docs:ideProjectModel | 39 |
| :build-cache-spi:ideProjectModel | 39 |
| :build-logic:integration-testing-kotlin:compileKotlin | 37 |
| :build-cache-packaging:ideProjectModel | 37 |
| :build-logic:performance-testing:compileGroovy | 33 |
| :build-logic:documentation:compileGroovy | 28 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 24 |
| :build-logic:integration-testing:compilePluginsBlocks | 24 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 23 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 22 |
| :build-logic:buildquality:compileKotlin | 20 |
| :build-logic-commons:publishing:compileKotlin | 19 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 19 |
| :build-logic:idea:compilePluginsBlocks | 17 |
| :build-logic:lifecycle:compileKotlin | 16 |
| :build-logic-commons:publishing:generateExternalPluginSpecBuilders | 16 |
| :build-logic-commons:publishing:compilePluginsBlocks | 16 |
| :build-logic:kotlin-dsl:compileKotlin | 16 |
| :build-logic:packaging:compileKotlin | 16 |
| :build-logic:lifecycle:generatePrecompiledScriptPluginAccessors | 15 |
| :build-logic:jvm:compileKotlin | 15 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 15 |
| :build-logic:integration-testing:compileKotlin | 15 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 14 |
| :build-logic:kotlin-dsl:generatePrecompiledScriptPluginAccessors | 14 |
| :build-logic-settings:version-catalogs:compileKotlin | 12 |
| :build-logic-commons:gradle-plugin:compileKotlin | 12 |
