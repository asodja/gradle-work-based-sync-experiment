Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4d-tasks/4-trace-log.txt`

Overall span: 17921 ms, 616590 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 4804 | 4804 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 309 | 14 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 237 | 237 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 51 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 34 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 33 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 29 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 29 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 22 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 22 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 16 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 0 ms, default: 5041 ms, project: 567 ms

## Configure project (287 projects, sum 19889 ms)

| Project | ms |
|---|---|
| :base-asm | 413 |
| :ant | 390 |
| :antlr | 378 |
| :architecture-test | 373 |
| :core | 334 |
| : | 331 |
| :docs | 290 |
| :ant-impl | 285 |
| :ant-worker | 263 |
| :ant-api | 249 |
| :base-compiler-worker | 228 |
| :declarative-dsl-core | 224 |
| :api-metadata | 222 |
| :problems | 150 |
| :time | 148 |
| :dependency-management-serialization-codecs | 145 |
| :declarative-dsl-tooling-builders | 141 |
| :dependency-management | 139 |
| :kotlin-dsl-tooling-model-impls | 139 |
| :tooling-api | 136 |
| :declarative-dsl-tooling-models | 135 |
| :dependency-management-resources | 135 |
| :kotlin-dsl-tooling-builders | 132 |
| :tooling-api-builders | 129 |
| :testing-native | 127 |
| :graph-serialization | 125 |
| :toolchains-jvm | 121 |
| :problems-reporting | 118 |
| :performance | 117 |
| :build-discovery | 116 |
| :language-jvm | 116 |
| :ide-native | 115 |
| :declarative-dsl-provider | 114 |
| :credentials | 112 |
| :launcher | 112 |
| :process-services-base | 112 |
| :credentials-api | 111 |
| :groovydoc-worker | 110 |
| :process-memory-services | 107 |
| :hashing | 104 |

## Tasks (651 executed, sum 1362 ms)

| Task | ms |
|---|---|
| :build-logic:integration-testing-kotlin:compileKotlin | 81 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 33 |
| :build-logic:uber-plugins:compileKotlin | 33 |
| :build-logic:build-update-utils:compileKotlin | 31 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 29 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 20 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 19 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 17 |
| :build-logic:integration-testing:compileKotlin | 15 |
| :build-logic:buildquality:compileKotlin | 15 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 14 |
| :build-logic:lifecycle:generatePrecompiledScriptPluginAccessors | 14 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 13 |
| :build-logic:packaging:compileKotlin | 13 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 12 |
| :build-logic-commons:gradle-plugin:compileKotlin | 12 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:documentation:compileGroovy | 11 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 10 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 10 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 10 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 10 |
| :build-logic-settings:build-environment:compileKotlin | 10 |
| :build-logic:cleanup:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic:jvm:compileKotlin | 10 |
| :build-logic:profiling:compileKotlin | 10 |
| :build-logic:performance-testing:compileGroovy | 10 |
| :build-logic:kotlin-dsl:compileKotlin | 10 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 9 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 8 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 8 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 8 |
| :build-logic:idea:compileKotlin | 8 |
| :build-logic:root-build:compileKotlin | 8 |
| :build-logic:buildquality:compilePluginsBlocks | 8 |
| :build-logic:packaging:generatePrecompiledScriptPluginAccessors | 8 |
| :stdlib-kotlin-extensions:ideProjectModel | 8 |
| :test-kit:ideProjectModel | 8 |
| :resources-sftp:ideProjectModel | 8 |
