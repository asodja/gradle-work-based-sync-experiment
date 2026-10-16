Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4d-tasks-only/3-trace-log.txt`

Overall span: 29851 ms, 620090 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 8706 | 8706 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 263 | 263 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 53 | 2 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 8969 ms, project: 53 ms

## Configure project (287 projects, sum 26352 ms)

| Project | ms |
|---|---|
| :architecture-test | 710 |
| :docs | 623 |
| :ant-worker | 548 |
| :ant | 504 |
| :antlr | 459 |
| :kotlin-dsl-integ-tests | 425 |
| :ant-impl | 395 |
| :base-asm | 394 |
| :declarative-dsl-core | 384 |
| : | 383 |
| :ant-api | 383 |
| :base-compiler-worker | 380 |
| :api-metadata | 355 |
| :kotlin-dsl | 306 |
| :public-api | 279 |
| :distributions-core | 242 |
| :distributions-basics | 232 |
| :enterprise-plugin-performance | 232 |
| :distributions-full | 231 |
| :performance | 229 |
| :distributions-jvm | 191 |
| :declarative-dsl-evaluator | 189 |
| :base-services | 186 |
| :tooling-api | 186 |
| :distributions-native | 173 |
| :project-features-api | 152 |
| :internal-build-reports | 149 |
| :stdlib-java-extensions | 145 |
| :soak | 140 |
| :build-cache | 138 |
| :resources | 138 |
| :bean-serialization-services | 137 |
| :guava-serialization-codecs | 136 |
| :plugin-development | 135 |
| :core-serialization-codecs | 131 |
| :groovy-loader | 131 |
| :java-api-extractor | 130 |
| :stdlib-kotlin-extensions | 130 |
| :graph-isolation | 129 |
| :publish | 129 |

## Tasks (650 executed, sum 1252 ms)

| Task | ms |
|---|---|
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 30 |
| :build-logic:uber-plugins:compileKotlin | 29 |
| :build-logic:integration-testing-kotlin:compileKotlin | 25 |
| :core-serialization-codecs:ideProjectModel | 19 |
| :declarative-dsl-api:ideProjectModel | 19 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 18 |
| :daemon-main:ideProjectModel | 18 |
| :daemon-protocol:ideProjectModel | 18 |
| :daemon-server:ideProjectModel | 18 |
| :code-quality:ideProjectModel | 18 |
| :build-logic:integration-testing:compileKotlin | 17 |
| :credentials-impl:ideProjectModel | 17 |
| :build-logic:buildquality:compileKotlin | 16 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 14 |
| :build-logic:packaging:compileKotlin | 13 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 12 |
| :build-logic:kotlin-dsl:compileKotlin | 12 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 11 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 11 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:jvm:compileKotlin | 11 |
| :build-logic:documentation:compileGroovy | 11 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 11 |
| :build-logic:profiling:compileKotlin | 10 |
| :build-logic:binary-compatibility:compileKotlin | 10 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic-commons:publishing:compileKotlin | 9 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 9 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 8 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 8 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 8 |
| :build-logic:idea:compilePluginsBlocks | 8 |
| :build-logic:lifecycle:compileKotlin | 8 |
| :build-logic:root-build:compileKotlin | 8 |
| :build-logic:buildquality:compilePluginsBlocks | 8 |
| :build-logic:packaging:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:uber-plugins:compilePluginsBlocks | 8 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 7 |
