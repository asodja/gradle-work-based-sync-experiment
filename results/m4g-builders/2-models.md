Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4g-builders/2-trace-log.txt`

Overall span: 24340 ms, 659602 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 21890 | 458 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 15431 | 15431 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 5359 | 536 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 2748 | 306 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 2637 | 161 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 1498 | 1401 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 459 | 418 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 374 | 374 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 299 | 34 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 226 | 31 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 213 | 45 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 89 | 16 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 47 | 2 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 46 | 8 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 28 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 25 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 25 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 23 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 22 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 20 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 19 | 4 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 8 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 6 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 6 | 2 | 0 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 5 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 4 | 1 | 287 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 3 | 1 | 287 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 2 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 2 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 2 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 459 ms, default: 15805 ms, project: 35355 ms

## Configure project (287 projects, sum 8258 ms)

| Project | ms |
|---|---|
| : | 358 |
| :ant | 234 |
| :docs | 187 |
| :build-logic | 126 |
| :declarative-dsl-core | 83 |
| :architecture-test | 79 |
| :performance | 73 |
| :start-parameter | 59 |
| :plugins-application | 58 |
| :base-services | 55 |
| :tooling-api-builders | 53 |
| :samples | 52 |
| :collections | 50 |
| :java-api-extractor | 49 |
| :ide-native | 48 |
| :logging | 48 |
| :process-services | 48 |
| :toolchains-jvm | 45 |
| :ant-worker | 43 |
| :configuration-problems-base | 43 |
| :kotlin-dsl | 42 |
| :distributions-core | 41 |
| :precondition-tester | 40 |
| :distributions-full | 39 |
| :enterprise-plugin-performance | 39 |
| :build-init-specs-api | 38 |
| :credentials | 38 |
| :kotlin-dsl-integ-tests | 38 |
| :plugins-groovy | 38 |
| :configuration-cache | 37 |
| :declarative-dsl-evaluator | 37 |
| :plugins-version-catalog | 36 |
| :build-cache-base | 35 |
| :enterprise-workers | 35 |
| :kotlin-dsl-plugins | 35 |
| :bean-serialization-services | 34 |
| :credentials-api | 34 |
| :plugins-java | 34 |
| :stdlib-serialization-codecs | 34 |
| :base-compiler-worker | 33 |

## Tasks (364 executed, sum 1071 ms)

| Task | ms |
|---|---|
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 49 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 46 |
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 31 |
| :build-logic:integration-testing:compileKotlin | 28 |
| :build-logic:uber-plugins:compileKotlin | 26 |
| :build-logic:documentation:compileGroovy | 24 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 21 |
| :build-logic:dependency-modules:generatePrecompiledScriptPluginAccessors | 17 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 16 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 15 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 15 |
| :build-logic:idea:generateExternalPluginSpecBuilders | 13 |
| :build-logic:integration-testing:compilePluginsBlocks | 13 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 13 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 13 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 12 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 12 |
| :build-logic:lifecycle:compilePluginsBlocks | 12 |
| :build-logic:jvm:compileKotlin | 12 |
| :build-logic:buildquality:compileKotlin | 12 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 11 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 11 |
| :build-logic-settings:build-environment:compileKotlin | 11 |
| :build-logic:performance-testing:compileGroovy | 11 |
| :build-logic-commons:publishing:generateExternalPluginSpecBuilders | 11 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 10 |
| :build-logic-commons:gradle-plugin:compileKotlin | 10 |
| :build-logic:dependency-modules:compilePluginsBlocks | 10 |
| :build-logic:binary-compatibility:compileJava | 10 |
| :build-logic:packaging:compileKotlin | 10 |
| :build-logic:root-build:processResources | 9 |
| :build-logic:lifecycle:generateExternalPluginSpecBuilders | 9 |
| :build-logic:build-update-utils:processResources | 9 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:idea:compileKotlin | 9 |
| :build-logic:lifecycle:compileKotlin | 9 |
| :build-logic:jvm:compilePluginsBlocks | 9 |
| :build-logic:build-update-utils:compileKotlin | 9 |
| :build-logic:kotlin-dsl:compileKotlin | 9 |
| :build-logic:uber-plugins:compilePluginsBlocks | 9 |
