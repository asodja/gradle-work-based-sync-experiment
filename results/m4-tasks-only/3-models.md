Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4-tasks-only/3-trace-log.txt`

Overall span: 12767 ms, 618966 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 5185 | 5185 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 301 | 301 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 72 | 3 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 5486 ms, project: 72 ms

## Configure project (287 projects, sum 27188 ms)

| Project | ms |
|---|---|
| :api-metadata | 556 |
| :base-asm | 555 |
| :ant-api | 544 |
| :declarative-dsl-core | 517 |
| :architecture-test | 512 |
| :declarative-dsl-evaluator | 504 |
| :ant-impl | 484 |
| :docs | 454 |
| :antlr | 452 |
| : | 399 |
| :ant | 385 |
| :ant-worker | 384 |
| :base-compiler-worker | 380 |
| :kotlin-dsl | 366 |
| :internal-build-reports | 339 |
| :distributions-full | 230 |
| :tooling-api | 217 |
| :public-api | 212 |
| :kotlin-dsl-integ-tests | 203 |
| :wrapper-main | 200 |
| :bean-serialization-services | 185 |
| :distributions-basics | 182 |
| :performance | 181 |
| :distributions-core | 171 |
| :enterprise-plugin-performance | 165 |
| :base-services | 150 |
| :distributions-jvm | 147 |
| :precondition-tester | 146 |
| :configuration-cache-base | 145 |
| :build-cache | 143 |
| :daemon-services | 141 |
| :kotlin-dsl-plugins | 138 |
| :scoped-persistent-cache | 134 |
| :daemon-server-worker | 130 |
| :project-features | 128 |
| :project-features-demos | 128 |
| :daemon-protocol | 123 |
| :software-diagnostics | 123 |
| :signing | 122 |
| :flow-services | 120 |

## Tasks (650 executed, sum 1419 ms)

| Task | ms |
|---|---|
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 37 |
| :build-logic:binary-compatibility:compileKotlin | 35 |
| :build-logic:uber-plugins:compileKotlin | 31 |
| :build-logic-commons:publishing:compileKotlin | 30 |
| :build-logic:performance-testing:compileKotlin | 27 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 24 |
| :build-logic:integration-testing-kotlin:compileKotlin | 24 |
| :build-logic:integration-testing:compileKotlin | 22 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 18 |
| :build-logic:documentation:compileGroovy | 17 |
| :build-logic:buildquality:compileKotlin | 16 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 15 |
| :build-logic:build-update-utils:compileKotlin | 14 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 14 |
| :build-logic:idea:compileKotlin | 13 |
| :build-logic:jvm:compileKotlin | 13 |
| :build-logic:performance-testing:compileGroovy | 13 |
| :build-logic:packaging:compileKotlin | 13 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 12 |
| :build-logic:dependency-modules:compileKotlin | 12 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:lifecycle:compilePluginsBlocks | 11 |
| :build-logic:lifecycle:compileKotlin | 11 |
| :build-logic:profiling:compileKotlin | 11 |
| :build-logic:kotlin-dsl:compileKotlin | 11 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 10 |
| :build-logic:dependency-modules:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 9 |
| :build-logic-commons:basics:compilePluginsBlocks | 9 |
| :build-logic:integration-testing:compilePluginsBlocks | 9 |
| :build-logic:uber-plugins:compilePluginsBlocks | 9 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 8 |
| :build-logic-settings:build-environment:compileKotlin | 8 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:jvm:compilePluginsBlocks | 8 |
| :build-logic:build-update-utils:generateExternalPluginSpecBuilders | 8 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 8 |
