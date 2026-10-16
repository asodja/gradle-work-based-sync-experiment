Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4d-tasks/3-trace-log.txt`

Overall span: 26451 ms, 626731 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 7575 | 7575 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 344 | 344 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 309 | 15 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 30 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 25 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 23 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 21 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 14 | 2 | 0 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 7 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 5 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 1 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 0 | 0 | 287 |

Totals by scope: build: 0 ms, default: 7919 ms, project: 535 ms

## Configure project (287 projects, sum 31933 ms)

| Project | ms |
|---|---|
| :api-metadata | 745 |
| :architecture-test | 629 |
| :ant | 618 |
| :ant-api | 613 |
| :declarative-dsl-core | 580 |
| :base-asm | 538 |
| :declarative-dsl-evaluator | 530 |
| :docs | 497 |
| :antlr | 464 |
| :ant-impl | 463 |
| :ant-worker | 463 |
| :base-compiler-worker | 458 |
| : | 427 |
| :kotlin-dsl | 411 |
| :kotlin-dsl-integ-tests | 372 |
| :public-api | 312 |
| :tooling-api | 304 |
| :distributions-full | 288 |
| :distributions-basics | 235 |
| :distributions-core | 223 |
| :core | 220 |
| :performance | 213 |
| :daemon-server-worker | 209 |
| :distributions-jvm | 205 |
| :internal-build-reports | 201 |
| :configuration-cache | 194 |
| :concurrent | 186 |
| :configuration-cache-base | 182 |
| :daemon-services | 182 |
| :wrapper-main | 182 |
| :isolated-action-services | 181 |
| :core-serialization-codecs | 179 |
| :toolchains-jvm-shared | 178 |
| :ivy | 176 |
| :distributions-native | 174 |
| :problems-reporting | 174 |
| :isolated-ant-builder | 173 |
| :bean-serialization-services | 171 |
| :declarative-dsl-tooling-builders | 170 |
| :graph-serialization | 168 |

## Tasks (651 executed, sum 1660 ms)

| Task | ms |
|---|---|
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 44 |
| :build-logic:buildquality:compileKotlin | 34 |
| :build-logic:uber-plugins:compileKotlin | 32 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 30 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 28 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 26 |
| :build-logic:binary-compatibility:compileKotlin | 25 |
| :build-logic:kotlin-dsl:compileKotlin | 24 |
| :build-logic:integration-testing:compileKotlin | 23 |
| :build-logic:documentation:compileGroovy | 21 |
| :build-logic:packaging:generateExternalPluginSpecBuilders | 21 |
| :build-logic-commons:publishing:generateExternalPluginSpecBuilders | 20 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 20 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 19 |
| :build-logic:packaging:generatePrecompiledScriptPluginAccessors | 19 |
| :build-logic:performance-testing:compileGroovy | 17 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 15 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 15 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 14 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 13 |
| :build-logic:packaging:compileKotlin | 13 |
| :build-logic:integration-testing-kotlin:compileKotlin | 13 |
| :build-logic:profiling:compileKotlin | 12 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 11 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 11 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:cleanup:compilePluginsBlocks | 11 |
| :build-logic:jvm:compileKotlin | 11 |
| :build-logic:buildquality:generateExternalPluginSpecBuilders | 11 |
| :build-logic:buildquality:compilePluginsBlocks | 11 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 10 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 10 |
| :build-logic:idea:compilePluginsBlocks | 10 |
| :build-logic:lifecycle:compileKotlin | 10 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic-settings:build-environment:compileKotlin | 9 |
| :build-logic-settings:version-catalogs:compileKotlin | 9 |
| :build-logic:idea:compileKotlin | 9 |
| :build-logic:dependency-modules:compileKotlin | 9 |
| :build-logic:integration-testing:compilePluginsBlocks | 9 |
