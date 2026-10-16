Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4-tasks/4-trace-log.txt`

Overall span: 17069 ms, 621406 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 5123 | 5089 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 3853 | 3853 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 318 | 47 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 228 | 228 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 93 | 71 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 29 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 28 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 23 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 21 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 19 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 18 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 15 | 2 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 5 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 2 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 1 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 1 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 93 ms, default: 4081 ms, project: 5636 ms

## Configure project (287 projects, sum 20565 ms)

| Project | ms |
|---|---|
| :ant | 469 |
| :antlr | 454 |
| :docs | 453 |
| :base-asm | 437 |
| :ant-worker | 353 |
| :architecture-test | 327 |
| :core | 304 |
| : | 296 |
| :declarative-dsl-core | 290 |
| :ant-impl | 288 |
| :ant-api | 275 |
| :api-metadata | 259 |
| :base-compiler-worker | 248 |
| :distributions-full | 202 |
| :distributions-integ-tests | 147 |
| :tooling-native | 147 |
| :tooling-api | 142 |
| :kotlin-dsl | 141 |
| :process-services | 141 |
| :docs-asciidoctor-extensions | 134 |
| :publish | 133 |
| :tooling-api-builders | 131 |
| :kotlin-dsl-integ-tests | 130 |
| :process-services-api | 130 |
| :project-features-demos | 129 |
| :kotlin-dsl-tooling-builders | 127 |
| :kotlin-dsl-tooling-model-impls | 127 |
| :project-features | 127 |
| :worker-main | 124 |
| :performance | 120 |
| :project-features-api | 118 |
| :tooling-native-model-impls | 118 |
| :war | 116 |
| :jvm-services | 115 |
| :kotlin-dsl-tooling-models | 115 |
| :versioned-cache | 115 |
| :declarative-dsl-tooling-builders | 113 |
| :credentials-impl | 110 |
| :kotlin-dsl-plugins | 109 |
| :kotlin-dsl-provider-plugins | 107 |

## Tasks (651 executed, sum 1128 ms)

| Task | ms |
|---|---|
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 33 |
| :build-logic:uber-plugins:compileKotlin | 30 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 19 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 19 |
| :build-logic:buildquality:compileKotlin | 18 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 18 |
| :build-logic:integration-testing-kotlin:compileKotlin | 18 |
| :build-logic:integration-testing:compileKotlin | 17 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 17 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 16 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 15 |
| :build-logic:documentation:compileGroovy | 13 |
| :build-logic-commons:gradle-plugin:compileKotlin | 12 |
| :build-logic:profiling:compileKotlin | 12 |
| :build-logic:packaging:compileKotlin | 12 |
| :build-logic:lifecycle:compilePluginsBlocks | 11 |
| :build-logic:dependency-modules:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 10 |
| :build-logic:jvm:compileKotlin | 10 |
| :build-logic:binary-compatibility:compileKotlin | 10 |
| :build-logic:kotlin-dsl:generateExternalPluginSpecBuilders | 10 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic:kotlin-dsl:compileKotlin | 10 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 9 |
| :build-logic:lifecycle:compileKotlin | 9 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:buildquality:generateExternalPluginSpecBuilders | 9 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 8 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 8 |
| :build-logic:dependency-modules:compilePluginsBlocks | 8 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:idea:compileKotlin | 8 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 8 |
| :build-logic:build-update-utils:generateExternalPluginSpecBuilders | 8 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:root-build:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:root-build:compileKotlin | 8 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 7 |
| :build-logic:idea:compilePluginsBlocks | 7 |
| :build-logic:integration-testing:compilePluginsBlocks | 7 |
