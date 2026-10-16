Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/heapx-tasks-only/3-trace-log.txt`

Overall span: 15741 ms, 620132 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 6621 | 6621 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 352 | 352 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 107 | 6 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 6973 ms, project: 107 ms

## Configure project (287 projects, sum 32550 ms)

| Project | ms |
|---|---|
| :ant | 612 |
| :api-metadata | 610 |
| :ant-worker | 606 |
| : | 577 |
| :antlr | 572 |
| :architecture-test | 542 |
| :base-asm | 505 |
| :base-compiler-worker | 476 |
| :kotlin-dsl | 467 |
| :kotlin-dsl-integ-tests | 465 |
| :declarative-dsl-core | 461 |
| :docs | 461 |
| :ant-impl | 441 |
| :ant-api | 438 |
| :tooling-api | 332 |
| :declarative-dsl-evaluator | 329 |
| :public-api | 323 |
| :enterprise-plugin-performance | 263 |
| :distributions-full | 259 |
| :bean-serialization-services | 241 |
| :configuration-cache | 236 |
| :performance | 221 |
| :core | 215 |
| :wrapper-main | 214 |
| :concurrent | 208 |
| :collections | 205 |
| :configuration-problems-base | 201 |
| :distributions-basics | 201 |
| :distributions-core | 201 |
| :core-api | 189 |
| :language-java | 189 |
| :graph-serialization | 173 |
| :build-cache | 170 |
| :logging-api | 168 |
| :graph-isolation | 166 |
| :configuration-cache-base | 165 |
| :distributions-jvm | 165 |
| :language-jvm | 165 |
| :launcher | 163 |
| :toolchains-jvm | 161 |

## Tasks (650 executed, sum 2322 ms)

| Task | ms |
|---|---|
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 90 |
| :build-logic:uber-plugins:compileKotlin | 40 |
| :build-logic:integration-testing:compileKotlin | 32 |
| :build-logic:idea:compilePluginsBlocks | 31 |
| :build-logic:idea:compileKotlin | 29 |
| :build-logic:performance-testing:compileGroovy | 28 |
| :build-logic:performance-testing:compileKotlin | 27 |
| :build-logic:documentation:compileGroovy | 25 |
| :build-logic:kotlin-dsl:compileKotlin | 25 |
| :build-logic:cleanup:compileKotlin | 23 |
| :build-logic-commons:publishing:compilePluginsBlocks | 21 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 20 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 20 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 19 |
| :build-logic:integration-testing:compilePluginsBlocks | 19 |
| :build-logic-settings:version-catalogs:compileKotlin | 18 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 18 |
| :build-logic:dependency-modules:generatePrecompiledScriptPluginAccessors | 18 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 18 |
| :build-logic:lifecycle:compileKotlin | 17 |
| :build-logic:build-update-utils:generateExternalPluginSpecBuilders | 17 |
| :build-logic-commons:publishing:generateExternalPluginSpecBuilders | 17 |
| :build-logic:binary-compatibility:compileKotlin | 17 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 16 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 16 |
| :build-logic:buildquality:compileKotlin | 16 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 15 |
| :build-logic:lifecycle:compilePluginsBlocks | 15 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 15 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 15 |
| :build-logic:packaging:compileKotlin | 15 |
| :build-logic-settings:build-environment:compileKotlin | 14 |
| :build-logic:dependency-modules:compilePluginsBlocks | 14 |
| :build-logic:jvm:compileKotlin | 14 |
| :build-logic-commons:gradle-plugin:compileKotlin | 13 |
| :build-logic:profiling:compileKotlin | 13 |
| :build-logic:buildquality:generateExternalPluginSpecBuilders | 13 |
| :build-logic:integration-testing-kotlin:compileKotlin | 13 |
| :daemon-protocol:ideProjectModel | 13 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 12 |
