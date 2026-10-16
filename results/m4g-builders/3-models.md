Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4g-builders/3-trace-log.txt`

Overall span: 14457 ms, 672691 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 26144 | 544 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 5855 | 5855 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 5741 | 361 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 5239 | 253 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 3603 | 328 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 507 | 486 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 448 | 411 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 283 | 283 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 263 | 15 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 210 | 26 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 206 | 15 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 101 | 12 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 99 | 10 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 64 | 54 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 54 | 11 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 45 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 34 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 29 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 28 | 4 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 22 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 11 | 3 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 10 | 4 | 0 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 8 | 1 | 0 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 8 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 7 | 5 | 0 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 6 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 6 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 5 | 2 | 0 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 2 | 1 | 0 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 1 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 508 ms, default: 6138 ms, project: 42526 ms

## Configure project (287 projects, sum 30246 ms)

| Project | ms |
|---|---|
| :api-metadata | 759 |
| :ant-worker | 615 |
| :architecture-test | 580 |
| :ant | 536 |
| :kotlin-dsl | 534 |
| :declarative-dsl-core | 522 |
| :ant-api | 506 |
| :kotlin-dsl-integ-tests | 476 |
| :ant-impl | 466 |
| :base-asm | 426 |
| : | 415 |
| :docs | 393 |
| :declarative-dsl-evaluator | 384 |
| :distributions-full | 369 |
| :base-compiler-worker | 366 |
| :antlr | 363 |
| :distributions-basics | 359 |
| :distributions-core | 316 |
| :bean-serialization-services | 279 |
| :distributions-jvm | 275 |
| :distributions-native | 275 |
| :distributions-publishing | 239 |
| :kotlin-dsl-plugins | 234 |
| :performance | 234 |
| :enterprise-plugin-performance | 214 |
| :internal-build-reports | 200 |
| :public-api | 193 |
| :build-cache | 186 |
| :tooling-api | 176 |
| :model-core | 170 |
| :build-cache-http | 167 |
| :build-cache-example-client | 160 |
| :configuration-cache | 159 |
| :build-cache-base | 157 |
| :project-features | 155 |
| :core | 150 |
| :language-groovy | 145 |
| :model-reflect | 144 |
| :jvm-services | 140 |
| :normalization | 137 |

## Tasks (364 executed, sum 1054 ms)

| Task | ms |
|---|---|
| :build-logic:idea:generateExternalPluginSpecBuilders | 47 |
| :build-logic:profiling:compilePluginsBlocks | 44 |
| :build-logic:uber-plugins:compileKotlin | 33 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 21 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 19 |
| :build-logic:integration-testing:compileKotlin | 18 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 17 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 16 |
| :build-logic:binary-compatibility:compileKotlin | 16 |
| :build-logic-commons:publishing:compilePluginsBlocks | 16 |
| :build-logic:buildquality:compileKotlin | 16 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 15 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 14 |
| :build-logic:packaging:compileKotlin | 14 |
| :build-logic:documentation:compileGroovy | 13 |
| :build-logic:profiling:compileKotlin | 13 |
| :build-logic-commons:gradle-plugin:compileKotlin | 12 |
| :build-logic:integration-testing-kotlin:compileKotlin | 12 |
| :build-logic-settings:build-environment:compileKotlin | 11 |
| :build-logic:lifecycle:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 11 |
| :build-logic:kotlin-dsl:compileKotlin | 11 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 10 |
| :build-logic-settings:version-catalogs:compileKotlin | 10 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 10 |
| :build-logic:jvm:compileKotlin | 10 |
| :build-logic:root-build:compileKotlin | 10 |
| :build-logic:buildquality:generateExternalPluginSpecBuilders | 10 |
| :build-logic:packaging:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic:lifecycle:compileKotlin | 9 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 9 |
| :build-logic:integration-testing:compilePluginsBlocks | 9 |
| :build-logic:buildquality:compilePluginsBlocks | 9 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:uber-plugins:compilePluginsBlocks | 9 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 8 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 8 |
| :build-logic-commons:build-platform-plugin:compilePluginsBlocks | 8 |
| :build-logic:integration-testing:processResources | 8 |
| :build-logic:documentation:processResources | 8 |
