Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4-builders/4-trace-log.txt`

Overall span: 15184 ms, 662318 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 25247 | 1037 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 4836 | 4836 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 4543 | 244 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 3710 | 340 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 3436 | 197 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 2870 | 2843 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 341 | 21 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 246 | 34 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 233 | 13 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 229 | 229 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 140 | 119 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 88 | 25 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 54 | 5 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 41 | 12 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 37 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 36 | 5 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 29 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 26 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 21 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 20 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 16 | 2 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 10 | 7 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 10 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 5 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 5 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 5 | 2 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 3 | 1 | 0 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 3 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 0 | 0 | 287 |

Totals by scope: build: 140 ms, default: 5065 ms, project: 41115 ms

## Configure project (287 projects, sum 24468 ms)

| Project | ms |
|---|---|
| :docs | 597 |
| :base-compiler-worker | 528 |
| :ant | 475 |
| :ant-worker | 416 |
| :core | 393 |
| : | 384 |
| :ant-api | 368 |
| :architecture-test | 318 |
| :antlr | 315 |
| :declarative-dsl-core | 296 |
| :build-logic | 285 |
| :base-asm | 278 |
| :api-metadata | 265 |
| :ant-impl | 256 |
| :stdlib-serialization-codecs | 178 |
| :performance | 165 |
| :guava-serialization-codecs | 162 |
| :ide | 147 |
| :installation-beacon | 147 |
| :testing-jvm-infrastructure | 147 |
| :time | 141 |
| :input-tracking | 137 |
| :build-init-specs | 136 |
| :build-option | 132 |
| :build-init-specs-api | 130 |
| :dependency-management-serialization-codecs | 130 |
| :precondition-tester | 130 |
| :build-operations-trace | 127 |
| :configuration-cache | 127 |
| :ide-model-impls | 124 |
| :declarative-dsl-evaluator | 120 |
| :declarative-dsl-tooling-builders | 120 |
| :process-services | 119 |
| :testing-native | 119 |
| :build-discovery-reporting | 118 |
| :build-process-services | 118 |
| :execution-e2e-tests | 118 |
| :scala-compiler-worker | 116 |
| :flow-services | 115 |
| :kotlin-dsl | 114 |

## Tasks (364 executed, sum 1007 ms)

| Task | ms |
|---|---|
| :build-logic:uber-plugins:compileKotlin | 35 |
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 34 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 24 |
| :build-logic:buildquality:compileKotlin | 23 |
| :build-logic:integration-testing-kotlin:compileKotlin | 21 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 20 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 19 |
| :build-logic:integration-testing:compileKotlin | 19 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 19 |
| :build-logic:packaging:compileKotlin | 18 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 16 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 15 |
| :build-logic:kotlin-dsl:compileKotlin | 15 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 15 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 14 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 14 |
| :build-logic-commons:gradle-plugin:compileKotlin | 14 |
| :build-logic:profiling:compileKotlin | 14 |
| :build-logic:dependency-modules:generatePrecompiledScriptPluginAccessors | 13 |
| :build-logic:documentation:compileGroovy | 13 |
| :build-logic:dependency-modules:compileKotlin | 12 |
| :build-logic:lifecycle:compileKotlin | 12 |
| :build-logic:jvm:compileKotlin | 12 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 11 |
| :build-logic:lifecycle:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:root-build:compileKotlin | 10 |
| :build-logic:buildquality:compilePluginsBlocks | 10 |
| :build-logic:packaging:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic-settings:version-catalogs:compileKotlin | 9 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 9 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 9 |
| :build-logic:kotlin-dsl:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:packaging:compilePluginsBlocks | 9 |
| :build-logic:uber-plugins:compilePluginsBlocks | 9 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 8 |
| :build-logic:idea:compileKotlin | 8 |
| :build-logic:integration-testing:compilePluginsBlocks | 8 |
| :build-logic:build-update-utils:compileKotlin | 8 |
