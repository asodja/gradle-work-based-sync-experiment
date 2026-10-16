Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/heap-tasks-only/3-trace-log.txt`

Overall span: 17008 ms, 619849 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 7045 | 7045 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 384 | 384 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 64 | 2 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 7429 ms, project: 64 ms

## Configure project (287 projects, sum 33229 ms)

| Project | ms |
|---|---|
| :declarative-dsl-core | 711 |
| :antlr | 676 |
| :docs | 637 |
| :api-metadata | 608 |
| :base-asm | 597 |
| :declarative-dsl-evaluator | 591 |
| :architecture-test | 565 |
| :ant-api | 510 |
| : | 463 |
| :kotlin-dsl | 427 |
| :ant-worker | 422 |
| :distributions-full | 422 |
| :ant | 409 |
| :ant-impl | 387 |
| :base-compiler-worker | 386 |
| :kotlin-dsl-integ-tests | 373 |
| :distributions-basics | 317 |
| :distributions-core | 316 |
| :distributions-jvm | 308 |
| :performance | 307 |
| :distributions-native | 295 |
| :tooling-api | 247 |
| :enterprise-plugin-performance | 242 |
| :distributions-publishing | 240 |
| :declarative-dsl-provider | 236 |
| :declarative-dsl-tooling-builders | 233 |
| :dependency-management-serialization-codecs | 231 |
| :internal-build-reports | 223 |
| :declarative-dsl-tooling-models | 210 |
| :daemon-services | 204 |
| :dependency-management-resources | 191 |
| :encryption-services | 188 |
| :dependency-management | 184 |
| :wrapper-main | 175 |
| :platform-jvm | 171 |
| :public-api | 171 |
| :guava-serialization-codecs | 167 |
| :flow-services | 161 |
| :file-collections | 160 |
| :project-features | 160 |

## Tasks (650 executed, sum 2113 ms)

| Task | ms |
|---|---|
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 99 |
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 74 |
| :build-logic:lifecycle:compilePluginsBlocks | 50 |
| :build-logic:idea:compileKotlin | 48 |
| :build-logic:dependency-modules:compileKotlin | 44 |
| :build-logic:uber-plugins:compileKotlin | 38 |
| :build-logic:integration-testing-kotlin:compileKotlin | 34 |
| :build-logic:integration-testing:compileKotlin | 22 |
| :build-logic:integration-testing-kotlin:generateExternalPluginSpecBuilders | 22 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 20 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 19 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 19 |
| :build-logic:binary-compatibility:compileKotlin | 19 |
| :build-logic:performance-testing:compileGroovy | 17 |
| :build-logic:buildquality:compileKotlin | 16 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 15 |
| :build-logic:build-update-utils:generateExternalPluginSpecBuilders | 15 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 15 |
| :build-logic:packaging:compileKotlin | 15 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 15 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 14 |
| :build-logic-commons:gradle-plugin:compileKotlin | 14 |
| :build-logic:idea:compilePluginsBlocks | 14 |
| :build-logic:performance-testing:compileKotlin | 14 |
| :build-logic:lifecycle:compileKotlin | 13 |
| :build-logic:jvm:compileKotlin | 13 |
| :build-logic:documentation:compileGroovy | 13 |
| :build-logic:profiling:compileKotlin | 13 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 12 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 12 |
| :build-logic:cleanup:compilePluginsBlocks | 12 |
| :build-logic-commons:publishing:generateExternalPluginSpecBuilders | 12 |
| :build-logic:kotlin-dsl:compileKotlin | 12 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 11 |
| :build-logic-commons:basics:compilePluginsBlocks | 11 |
| :build-logic-commons:build-platform-plugin:compilePluginsBlocks | 11 |
| :build-logic:dependency-modules:compilePluginsBlocks | 11 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 10 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 10 |
