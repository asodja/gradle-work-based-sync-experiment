Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4b-tasks-only/4-trace-log.txt`

Overall span: 15245 ms, 609240 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 5270 | 5270 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 612 | 612 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 30 | 6 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: default: 5882 ms, project: 30 ms

## Configure project (287 projects, sum 31324 ms)

| Project | ms |
|---|---|
| :docs | 714 |
| :api-metadata | 676 |
| :base-compiler-worker | 595 |
| :antlr | 553 |
| :architecture-test | 516 |
| :base-asm | 465 |
| : | 455 |
| :ant-worker | 445 |
| :core | 409 |
| :ant-impl | 378 |
| :ant-api | 357 |
| :ant | 357 |
| :declarative-dsl-core | 334 |
| :performance | 257 |
| :functional | 255 |
| :flow-services | 241 |
| :kotlin-dsl | 236 |
| :graph-isolation | 223 |
| :base-services | 215 |
| :gradle-cli | 211 |
| :core-serialization-codecs | 196 |
| :groovy-compiler-worker | 192 |
| :encryption-services | 180 |
| :tooling-api | 175 |
| :graph-serialization | 174 |
| :build-logic | 166 |
| :distributions-native | 163 |
| :publish | 163 |
| :soak | 163 |
| :distributions-full | 161 |
| :project-features-demos | 158 |
| :integ-test | 157 |
| :request-handler-worker | 154 |
| :core-kotlin-extensions | 153 |
| :plugins-java-library | 153 |
| :report-rendering | 153 |
| :enterprise-operations | 152 |
| :toolchains-jvm | 152 |
| :stdlib-java-extensions | 151 |
| :reporting | 147 |

## Tasks (650 executed, sum 3350 ms)

| Task | ms |
|---|---|
| :build-logic:cleanup:compileKotlin | 58 |
| :build-logic:idea:compilePluginsBlocks | 50 |
| :build-logic:dependency-modules:compileKotlin | 47 |
| :build-logic:lifecycle:compileKotlin | 44 |
| :build-logic:idea:generateExternalPluginSpecBuilders | 39 |
| :build-logic:performance-testing:compileGroovy | 37 |
| :build-logic:uber-plugins:compileKotlin | 36 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 31 |
| :docs:ideProjectModel | 30 |
| :build-logic:integration-testing-kotlin:compileKotlin | 29 |
| :dependency-management:ideProjectModel | 27 |
| :build-logic:integration-testing:compileKotlin | 24 |
| :build-logic:binary-compatibility:compileKotlin | 24 |
| :declarative-dsl-tooling-models:ideProjectModel | 24 |
| :declarative-dsl-tooling-builders:ideProjectModel | 24 |
| :javadoc:ideProjectModel | 23 |
| :build-logic:buildquality:compileKotlin | 22 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 21 |
| :dependency-management-serialization-codecs:ideProjectModel | 21 |
| :ide-native:ideProjectModel | 21 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 20 |
| :build-logic:kotlin-dsl:compileKotlin | 20 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 20 |
| :internal-distribution-testing:ideProjectModel | 19 |
| :java-compiler-worker:ideProjectModel | 19 |
| :java-platform:ideProjectModel | 19 |
| :build-logic:dependency-modules:generatePrecompiledScriptPluginAccessors | 18 |
| :build-logic:cleanup:compileJava | 18 |
| :build-logic:kotlin-dsl:generatePrecompiledScriptPluginAccessors | 18 |
| :internal-performance-testing:ideProjectModel | 18 |
| :build-logic:documentation:compileGroovy | 17 |
| :platform-base:ideProjectModel | 17 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 16 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 16 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 16 |
| :build-logic:build-update-utils:compileKotlin | 16 |
| :build-configuration:ideProjectModel | 16 |
| :io:ideProjectModel | 16 |
| :toolchains-jvm-shared:ideProjectModel | 16 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 15 |
