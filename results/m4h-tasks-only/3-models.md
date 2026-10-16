Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4h-tasks-only/3-trace-log.txt`

Overall span: 14221 ms, 620400 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 5707 | 5707 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 282 | 282 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 69 | 6 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 5989 ms, project: 69 ms

## Configure project (287 projects, sum 27298 ms)

| Project | ms |
|---|---|
| :architecture-test | 564 |
| :kotlin-dsl | 463 |
| :base-compiler-worker | 452 |
| :base-asm | 436 |
| :declarative-dsl-core | 402 |
| :docs | 385 |
| :kotlin-dsl-integ-tests | 368 |
| :ant-impl | 361 |
| : | 356 |
| :ant | 326 |
| :ant-api | 305 |
| :ant-worker | 305 |
| :antlr | 305 |
| :api-metadata | 295 |
| :enterprise-plugin-performance | 241 |
| :declarative-dsl-evaluator | 239 |
| :performance | 237 |
| :public-api | 234 |
| :internal-build-reports | 229 |
| :distributions-full | 226 |
| :tooling-api | 218 |
| :distributions-core | 195 |
| :distributions-basics | 184 |
| :bean-serialization-services | 165 |
| :configuration-cache | 161 |
| :kotlin-dsl-plugins | 161 |
| :distributions-jvm | 160 |
| :testing-jvm-infrastructure | 151 |
| :distributions-native | 150 |
| :configuration-cache-base | 149 |
| :core-kotlin-extensions | 144 |
| :testing-native | 144 |
| :core-api | 142 |
| :security | 141 |
| :stdlib-serialization-codecs | 139 |
| :distributions-publishing | 137 |
| :ide-native | 136 |
| :integ-test | 135 |
| :toolchains-jvm | 135 |
| :test-kit | 133 |

## Tasks (650 executed, sum 1027 ms)

| Task | ms |
|---|---|
| :build-logic:uber-plugins:compileKotlin | 29 |
| :build-logic:idea:generateExternalPluginSpecBuilders | 24 |
| :build-logic:integration-testing-kotlin:compileKotlin | 24 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 18 |
| :build-logic:dependency-modules:generatePrecompiledScriptPluginAccessors | 17 |
| :build-logic:integration-testing:compileKotlin | 16 |
| :build-logic:documentation:compileGroovy | 15 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 14 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 13 |
| :build-logic:buildquality:compileKotlin | 13 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 13 |
| :build-logic-commons:gradle-plugin:compileKotlin | 12 |
| :build-logic:packaging:compileKotlin | 12 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic:profiling:compileKotlin | 11 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 10 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 9 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 9 |
| :build-logic:jvm:compileKotlin | 9 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 9 |
| :build-logic:build-update-utils:generateExternalPluginSpecBuilders | 9 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 9 |
| :build-logic:kotlin-dsl:compileKotlin | 9 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 8 |
| :build-logic-commons:build-platform-plugin:compilePluginsBlocks | 8 |
| :build-logic:cleanup:compileJava | 8 |
| :build-logic:root-build:compileKotlin | 8 |
| :build-logic:binary-compatibility:compileKotlin | 8 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 8 |
| :build-logic-commons:publishing:compileKotlin | 8 |
| :build-logic:uber-plugins:compilePluginsBlocks | 8 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 7 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 7 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 7 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 7 |
| :build-logic-commons:basics:compilePluginsBlocks | 7 |
| :build-logic-commons:basics:compileKotlin | 7 |
| :build-logic:dependency-modules:compilePluginsBlocks | 7 |
