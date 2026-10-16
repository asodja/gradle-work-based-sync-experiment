Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4e-tasks-only/3-trace-log.txt`

Overall span: 37211 ms, 620186 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 9872 | 9872 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 320 | 320 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 55 | 5 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 10192 ms, project: 55 ms

## Configure project (287 projects, sum 33427 ms)

| Project | ms |
|---|---|
| :api-metadata | 732 |
| :kotlin-dsl | 699 |
| :architecture-test | 660 |
| :declarative-dsl-core | 645 |
| :kotlin-dsl-integ-tests | 613 |
| :docs | 569 |
| :ant-worker | 557 |
| :base-asm | 532 |
| : | 467 |
| :declarative-dsl-evaluator | 460 |
| :ant-api | 431 |
| :distributions-full | 420 |
| :ant | 408 |
| :ant-impl | 401 |
| :antlr | 401 |
| :base-compiler-worker | 389 |
| :kotlin-dsl-plugins | 299 |
| :tooling-api | 269 |
| :public-api | 256 |
| :bean-serialization-services | 230 |
| :internal-build-reports | 226 |
| :distributions-basics | 224 |
| :declarative-dsl-tooling-models | 216 |
| :wrapper-main | 207 |
| :dependency-management | 195 |
| :declarative-dsl-provider | 194 |
| :distributions-core | 188 |
| :enterprise-plugin-performance | 186 |
| :configuration-cache-base | 181 |
| :core-serialization-codecs | 178 |
| :performance | 170 |
| :build-cache | 164 |
| :declarative-dsl-tooling-builders | 163 |
| :distributions-jvm | 160 |
| :daemon-services | 155 |
| :code-quality | 152 |
| :toolchains-jvm | 152 |
| :flow-services | 151 |
| :java-api-extractor | 150 |
| :composite-builds | 149 |

## Tasks (650 executed, sum 1415 ms)

| Task | ms |
|---|---|
| :build-logic:uber-plugins:compileKotlin | 37 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 33 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 22 |
| :build-logic:documentation:compileGroovy | 21 |
| :build-logic:integration-testing:compileKotlin | 21 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 18 |
| :build-logic:buildquality:compileKotlin | 17 |
| :build-logic:binary-compatibility:compileKotlin | 15 |
| :build-logic:packaging:compileKotlin | 14 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 13 |
| :build-logic-commons:gradle-plugin:compileKotlin | 12 |
| :build-logic:profiling:compileKotlin | 12 |
| :build-logic:kotlin-dsl:compileKotlin | 12 |
| :build-logic:integration-testing-kotlin:compileKotlin | 12 |
| :build-logic:jvm:compileKotlin | 11 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 10 |
| :build-logic:dependency-modules:compileKotlin | 10 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 9 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 9 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 9 |
| :build-logic-settings:version-catalogs:compileKotlin | 9 |
| :build-logic:idea:compilePluginsBlocks | 9 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 9 |
| :build-logic:integration-testing:compilePluginsBlocks | 9 |
| :build-logic:performance-testing:compileGroovy | 9 |
| :build-logic:packaging:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:uber-plugins:compilePluginsBlocks | 9 |
| :classpath:ideProjectModel | 9 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 8 |
| :build-logic:cleanup:compilePluginsBlocks | 8 |
| :build-logic:cleanup:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:lifecycle:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:idea:compileKotlin | 8 |
| :build-logic:lifecycle:compileKotlin | 8 |
| :build-logic:root-build:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:root-build:compileKotlin | 8 |
| :build-logic-commons:publishing:compileKotlin | 8 |
