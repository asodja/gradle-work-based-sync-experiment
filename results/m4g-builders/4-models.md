Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4g-builders/4-trace-log.txt`

Overall span: 14406 ms, 662245 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 25946 | 733 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 6120 | 6120 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 4773 | 345 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 4766 | 334 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 2724 | 326 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 514 | 485 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 362 | 28 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 289 | 289 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 238 | 14 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 237 | 31 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 125 | 106 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 115 | 20 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 56 | 13 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 38 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 25 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 25 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 24 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 20 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 19 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 16 | 2 | 0 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 9 | 3 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 9 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 6 | 1 | 0 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 5 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 5 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 5 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 5 | 2 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 4 | 1 | 0 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 2 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 2 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 2 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 1 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 126 ms, default: 6409 ms, project: 40063 ms

## Configure project (287 projects, sum 23828 ms)

| Project | ms |
|---|---|
| :ant-api | 492 |
| :docs | 485 |
| :base-compiler-worker | 442 |
| :ant-worker | 400 |
| : | 375 |
| :ant-impl | 361 |
| :core | 306 |
| :antlr | 304 |
| :declarative-dsl-core | 280 |
| :base-asm | 278 |
| :api-metadata | 257 |
| :architecture-test | 250 |
| :ant | 243 |
| :declarative-dsl-evaluator | 168 |
| :build-profile | 165 |
| :kotlin-dsl | 165 |
| :kotlin-dsl-integ-tests | 156 |
| :kotlin-dsl-plugins | 148 |
| :cli | 147 |
| :build-state | 146 |
| :build-option | 145 |
| :classloaders | 143 |
| :kotlin-dsl-tooling-builders | 139 |
| :classpath | 137 |
| :toolchains-jvm | 135 |
| :client-services | 134 |
| :ide-model-impls | 132 |
| :tooling-api | 128 |
| :signing | 125 |
| :unit-test-fixtures | 125 |
| :process-services-base | 124 |
| :credentials | 120 |
| :problems | 120 |
| :build-logic | 116 |
| :kotlin-dsl-tooling-model-impls | 112 |
| :normalization-java | 112 |
| :smoke-ide-test | 111 |
| :software-diagnostics | 111 |
| :version-control | 111 |
| :credentials-impl | 110 |

## Tasks (364 executed, sum 850 ms)

| Task | ms |
|---|---|
| :build-logic:uber-plugins:compileKotlin | 31 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 27 |
| :build-logic:integration-testing:compileKotlin | 27 |
| :build-logic:documentation:compileGroovy | 19 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 17 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 14 |
| :build-logic:buildquality:compileKotlin | 14 |
| :build-logic:jvm:compileKotlin | 13 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 13 |
| :build-logic:integration-testing:compilePluginsBlocks | 12 |
| :build-logic:profiling:compileKotlin | 12 |
| :build-logic-commons:publishing:compileKotlin | 12 |
| :build-logic:packaging:compileKotlin | 12 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 11 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:cleanup:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:build-update-utils:generateExternalPluginSpecBuilders | 11 |
| :build-logic:binary-compatibility:compileKotlin | 11 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:build-update-utils:compileKotlin | 10 |
| :build-logic:root-build:compileKotlin | 10 |
| :build-logic:performance-testing:compileGroovy | 10 |
| :build-logic:kotlin-dsl:compileKotlin | 10 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 10 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:buildquality:compilePluginsBlocks | 9 |
| :build-logic:integration-testing-kotlin:compileKotlin | 9 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 8 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 8 |
| :build-logic:lifecycle:compileKotlin | 8 |
| :build-logic:idea:compileKotlin | 8 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:uber-plugins:compilePluginsBlocks | 8 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 7 |
| :build-logic:cleanup:compilePluginsBlocks | 7 |
| :build-logic:cleanup:compileKotlin | 7 |
| :build-logic:profiling:compilePluginsBlocks | 7 |
| :build-logic:root-build:generatePrecompiledScriptPluginAccessors | 7 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 6 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 6 |
