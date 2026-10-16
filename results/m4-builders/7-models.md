Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4-builders/7-trace-log.txt`

Overall span: 16129 ms, 662130 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 24615 | 591 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 4272 | 4241 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 4262 | 387 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 4058 | 4058 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 3858 | 233 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 3082 | 293 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 839 | 839 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 763 | 458 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 224 | 18 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 210 | 18 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 126 | 109 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 58 | 9 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 36 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 31 | 4 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 29 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 27 | 4 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 24 | 5 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 24 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 22 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 10 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 8 | 6 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 5 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 5 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 5 | 2 | 0 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 4 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 4 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 3 | 1 | 0 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 2 | 1 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 2 | 1 | 287 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 1 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 126 ms, default: 4897 ms, project: 41720 ms

## Configure project (287 projects, sum 22939 ms)

| Project | ms |
|---|---|
| :base-compiler-worker | 516 |
| :antlr | 475 |
| :ant | 439 |
| :architecture-test | 414 |
| :ant-impl | 410 |
| : | 399 |
| :ant-api | 348 |
| :api-metadata | 324 |
| :docs | 318 |
| :declarative-dsl-core | 313 |
| :base-asm | 307 |
| :ant-worker | 301 |
| :performance | 197 |
| :declarative-dsl-provider | 135 |
| :declarative-dsl-evaluator | 133 |
| :kotlin-dsl-provider-plugins | 130 |
| :smoke-ide-test | 128 |
| :kotlin-dsl | 127 |
| :logging | 127 |
| :build-logic | 126 |
| :serialization | 126 |
| :flow-services | 125 |
| :distributions-core | 124 |
| :service-lookup | 123 |
| :collections | 122 |
| :functional | 121 |
| :process-services-base | 121 |
| :signing | 121 |
| :core-kotlin-extensions | 120 |
| :logging-api | 118 |
| :launcher | 117 |
| :reflection-services | 115 |
| :dependency-management-serialization-codecs | 113 |
| :project-features-demos | 113 |
| :maven | 112 |
| :dependency-management-resources | 111 |
| :public-api-tests | 111 |
| :execution | 110 |
| :test-kit | 110 |
| :language-native | 109 |

## Tasks (364 executed, sum 1185 ms)

| Task | ms |
|---|---|
| :build-logic:integration-testing-kotlin:compileKotlin | 56 |
| :build-logic:buildquality:compileKotlin | 44 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 40 |
| :build-logic:packaging:compileKotlin | 40 |
| :build-logic:uber-plugins:compileKotlin | 35 |
| :build-logic:documentation:compileGroovy | 25 |
| :build-logic:integration-testing:compileKotlin | 24 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 22 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 21 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 21 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 20 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 20 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 19 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 19 |
| :build-logic-commons:gradle-plugin:generateExternalPluginSpecBuilders | 18 |
| :build-logic:cleanup:compileJava | 18 |
| :build-logic-commons:module-identity:jar | 16 |
| :build-logic:dependency-modules:compilePluginsBlocks | 16 |
| :build-logic:lifecycle:generatePrecompiledScriptPluginAccessors | 15 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 14 |
| :build-logic:profiling:compileKotlin | 13 |
| :build-logic:performance-testing:compileGroovy | 13 |
| :build-logic:jvm:compileKotlin | 12 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic:kotlin-dsl:compileKotlin | 12 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic-commons:publishing:compileKotlin | 11 |
| :build-logic:root-build:compileKotlin | 10 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 10 |
| :build-logic:uber-plugins:compilePluginsBlocks | 10 |
| :build-logic-settings:architecture-docs:compileKotlin | 9 |
| :build-logic:idea:compileKotlin | 9 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 9 |
| :build-logic:integration-testing:compilePluginsBlocks | 9 |
| :build-logic:binary-compatibility:compileKotlin | 9 |
| :build-logic:buildquality:compilePluginsBlocks | 9 |
| :build-logic:integration-testing-kotlin:checkKotlinCompilerVersion | 9 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 8 |
