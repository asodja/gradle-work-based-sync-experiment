Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4-builders/2-trace-log.txt`

Overall span: 25488 ms, 660093 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 25145 | 550 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 9800 | 9800 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 7795 | 7697 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 5029 | 472 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 3368 | 302 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 2696 | 168 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 523 | 493 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 460 | 415 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 345 | 345 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 289 | 40 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 232 | 40 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 198 | 3 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 74 | 12 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 61 | 32 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 57 | 6 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 40 | 37 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 36 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 23 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 23 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 22 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 21 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 17 | 3 | 0 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 9 | 3 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 9 | 7 | 0 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 8 | 2 | 0 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 7 | 2 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 6 | 1 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 6 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 5 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 4 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 2 | 1 | 0 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 2 | 1 | 287 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 1 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |

Totals by scope: build: 460 ms, default: 10146 ms, project: 45812 ms

## Configure project (287 projects, sum 8354 ms)

| Project | ms |
|---|---|
| : | 369 |
| :ant | 235 |
| :docs | 215 |
| :build-logic | 119 |
| :architecture-test | 76 |
| :performance | 76 |
| :declarative-dsl-core | 67 |
| :kotlin-dsl | 63 |
| :configuration-problems-base | 56 |
| :declarative-dsl-evaluator | 52 |
| :distributions-integ-tests | 51 |
| :hashing-services | 49 |
| :internal-performance-testing | 49 |
| :platform-jvm | 49 |
| :project-features | 49 |
| :ant-worker | 47 |
| :execution | 47 |
| :service-provider | 47 |
| :plugins-test-report-aggregation | 46 |
| :stdlib-serialization-codecs | 46 |
| :stdlib-java-extensions | 45 |
| :base-services | 44 |
| :build-cache-base | 44 |
| :composite-builds | 44 |
| :resources-http | 44 |
| :testing-native | 43 |
| :logging-api | 42 |
| :war | 42 |
| :build-operations | 41 |
| :kotlin-dsl-integ-tests | 40 |
| :credentials-api | 39 |
| :java-api-extractor | 39 |
| :bean-serialization-services | 38 |
| :configuration-cache | 38 |
| :precondition-tester | 38 |
| :core | 36 |
| :kotlin-dsl-plugins | 36 |
| :plugins-jvm-test-fixtures | 36 |
| :stdlib-kotlin-extensions | 34 |
| :build-cache-core | 33 |

## Tasks (364 executed, sum 1014 ms)

| Task | ms |
|---|---|
| :build-logic-settings:default-settings-plugins:compileKotlin | 53 |
| :build-logic-settings:architecture-docs:compilePluginsBlocks | 49 |
| :build-logic:uber-plugins:compileKotlin | 35 |
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 34 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 27 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 21 |
| :build-logic:documentation:compileGroovy | 19 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 18 |
| :build-logic:profiling:generateExternalPluginSpecBuilders | 18 |
| :build-logic-settings:version-catalogs:compileKotlin | 15 |
| :build-logic:buildquality:compileKotlin | 15 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 14 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 13 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 13 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 13 |
| :build-logic:build-update-utils:generateExternalPluginSpecBuilders | 13 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 12 |
| :build-logic:lifecycle:compilePluginsBlocks | 12 |
| :build-logic:integration-testing:compileKotlin | 12 |
| :build-logic:binary-compatibility:compileKotlin | 12 |
| :build-logic-settings:build-environment:compileKotlin | 11 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 11 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:packaging:compileKotlin | 11 |
| :build-logic:uber-plugins:compilePluginsBlocks | 11 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 10 |
| :build-logic:jvm:compileKotlin | 10 |
| :build-logic:profiling:compileKotlin | 10 |
| :build-logic-commons:publishing:compileKotlin | 10 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 9 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 9 |
| :build-logic:idea:compileKotlin | 9 |
| :build-logic:lifecycle:compileKotlin | 9 |
| :build-logic:root-build:compileKotlin | 9 |
| :build-logic:dependency-modules:compileKotlin | 8 |
| :build-logic:integration-testing:compilePluginsBlocks | 8 |
| :build-logic:kotlin-dsl:compileKotlin | 8 |
| :build-logic:buildquality:compilePluginsBlocks | 8 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 7 |
