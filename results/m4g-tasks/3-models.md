Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4g-tasks/3-trace-log.txt`

Overall span: 28869 ms, 627096 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 12127 | 12127 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 8327 | 8327 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 325 | 13 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 42 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 37 | 5 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 24 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 21 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 16 | 2 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 5 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 0 ms, default: 20454 ms, project: 582 ms

## Configure project (287 projects, sum 39169 ms)

| Project | ms |
|---|---|
| :build-logic-settings | 11875 |
| :kotlin-dsl | 590 |
| :api-metadata | 562 |
| :base-compiler-worker | 511 |
| :docs | 504 |
| :architecture-test | 475 |
| :declarative-dsl-core | 458 |
| :antlr | 457 |
| :ant | 417 |
| : | 393 |
| :kotlin-dsl-integ-tests | 391 |
| :ant-api | 381 |
| :declarative-dsl-evaluator | 342 |
| :distributions-full | 339 |
| :ant-impl | 316 |
| :ant-worker | 312 |
| :base-asm | 310 |
| :performance | 301 |
| :internal-build-reports | 286 |
| :distributions-basics | 241 |
| :distributions-core | 227 |
| :tooling-api | 212 |
| :distributions-jvm | 208 |
| :daemon-logging | 200 |
| :bean-serialization-services | 195 |
| :daemon-main | 194 |
| :distributions-native | 190 |
| :core-serialization-codecs | 188 |
| :core-kotlin-extensions | 181 |
| :wrapper-main | 180 |
| :credentials-impl | 168 |
| :distributions-publishing | 164 |
| :core-api | 162 |
| :internal-testing | 158 |
| :kotlin-dsl-plugins | 158 |
| :normalization-api | 158 |
| :credentials | 149 |
| :daemon-messaging | 149 |
| :build-cache | 136 |
| :platform-base | 136 |

## Tasks (651 executed, sum 1218 ms)

| Task | ms |
|---|---|
| :build-logic:integration-testing:compileKotlin | 40 |
| :build-logic:documentation:compileGroovy | 31 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 30 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 30 |
| :build-logic:uber-plugins:compileKotlin | 30 |
| :build-logic:integration-testing-kotlin:compileKotlin | 26 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 24 |
| :build-logic:buildquality:compileKotlin | 15 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 15 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 15 |
| :build-logic:profiling:compileKotlin | 12 |
| :build-logic:packaging:compileKotlin | 12 |
| :build-logic:cleanup:compilePluginsBlocks | 11 |
| :build-logic:jvm:compileKotlin | 11 |
| :build-logic:integration-testing-kotlin:generateExternalPluginSpecBuilders | 11 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 10 |
| :build-logic-commons:gradle-plugin:compileKotlin | 10 |
| :build-logic:idea:compilePluginsBlocks | 10 |
| :build-logic:kotlin-dsl:compileKotlin | 10 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 9 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:dependency-modules:compilePluginsBlocks | 9 |
| :build-logic:idea:compileKotlin | 9 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 9 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 9 |
| :build-logic:buildquality:compilePluginsBlocks | 9 |
| :build-logic:uber-plugins:compilePluginsBlocks | 9 |
| :build-logic:lifecycle:compilePluginsBlocks | 8 |
| :build-logic:integration-testing:compilePluginsBlocks | 8 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:root-build:compileKotlin | 8 |
| :build-logic:binary-compatibility:compileKotlin | 8 |
| :build-logic-commons:publishing:compileKotlin | 8 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 8 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 7 |
| :build-logic-commons:basics:compilePluginsBlocks | 7 |
| :build-logic:lifecycle:generatePrecompiledScriptPluginAccessors | 7 |
| :build-logic:build-update-utils:compilePluginsBlocks | 7 |
