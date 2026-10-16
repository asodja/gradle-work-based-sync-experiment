Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4g-tasks/4-trace-log.txt`

Overall span: 21882 ms, 616829 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 8605 | 8605 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 4561 | 4561 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 304 | 40 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 32 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 23 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 19 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 14 | 2 | 0 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 6 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 1 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 0 ms, default: 13166 ms, project: 535 ms

## Configure project (287 projects, sum 29064 ms)

| Project | ms |
|---|---|
| :build-logic-settings | 8362 |
| :antlr | 461 |
| :ant-impl | 383 |
| :api-metadata | 362 |
| :ant | 338 |
| :core | 332 |
| :ant-worker | 328 |
| :docs | 299 |
| : | 298 |
| :base-asm | 288 |
| :architecture-test | 280 |
| :base-compiler-worker | 253 |
| :ant-api | 226 |
| :declarative-dsl-core | 173 |
| :configuration-cache | 164 |
| :configuration-problems-base | 146 |
| :stdlib-serialization-codecs | 137 |
| :signing | 136 |
| :build-configuration | 133 |
| :execution | 133 |
| :soak | 132 |
| :plugins-java | 126 |
| :stdlib-kotlin-extensions | 123 |
| :platform-native | 122 |
| :plugins-groovy | 121 |
| :smoke-test | 121 |
| :flow-services | 120 |
| :file-temp | 119 |
| :start-parameter | 118 |
| :build-discovery-impl | 117 |
| :isolated-ant-builder | 117 |
| :software-diagnostics | 117 |
| :core-serialization-codecs | 115 |
| :java-compiler-worker | 114 |
| :declarative-dsl-api | 112 |
| :java-compiler-plugin | 111 |
| :execution-e2e-tests | 110 |
| :ivy | 110 |
| :core-api | 109 |
| :plugins-jvm-test-fixtures | 109 |

## Tasks (651 executed, sum 1288 ms)

| Task | ms |
|---|---|
| :build-logic:uber-plugins:compileKotlin | 31 |
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 22 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 17 |
| :build-logic:idea:compileKotlin | 15 |
| :build-logic:integration-testing:compileKotlin | 15 |
| :build-logic:performance-testing:compileGroovy | 14 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 14 |
| :build-logic:buildquality:compileKotlin | 14 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 12 |
| :build-logic:documentation:compileGroovy | 12 |
| :build-logic:binary-compatibility:compileKotlin | 12 |
| :build-logic:packaging:compileKotlin | 12 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:jvm:compileKotlin | 11 |
| :build-logic:profiling:compileKotlin | 10 |
| :build-logic:binary-compatibility:compileJava | 10 |
| :build-logic:performance-testing:compileKotlin | 10 |
| :build-logic:kotlin-dsl:compileKotlin | 10 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 10 |
| :build-option:ideProjectModel | 10 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 9 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 9 |
| :build-logic-settings:build-environment:compileKotlin | 9 |
| :build-logic-settings:version-catalogs:compileKotlin | 9 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:build-update-utils:compileKotlin | 9 |
| :build-logic:root-build:compileKotlin | 9 |
| :build-logic:kotlin-dsl:generateExternalPluginSpecBuilders | 9 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 8 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 8 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 8 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 8 |
| :build-logic-commons:build-platform-plugin:compilePluginsBlocks | 8 |
| :build-logic:dependency-modules:compilePluginsBlocks | 8 |
| :build-logic:lifecycle:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:uber-plugins:compilePluginsBlocks | 8 |
| :build-logic:integration-testing-kotlin:compileKotlin | 8 |
| :graph-serialization:ideProjectModel | 8 |
