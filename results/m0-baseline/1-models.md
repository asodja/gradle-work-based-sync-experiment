Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m0-baseline/1-trace-log.txt`

Overall span: 35063 ms, 670468 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 29757 | 513 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 15603 | 15603 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 10777 | 744 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 6175 | 6042 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 5846 | 570 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 4234 | 308 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 3359 | 3359 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 780 | 445 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 501 | 463 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 297 | 39 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 250 | 24 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 226 | 3 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 116 | 16 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 56 | 5 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 50 | 11 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 42 | 10 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 38 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 38 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 32 | 3 | 0 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 31 | 6 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 29 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 25 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 22 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 17 | 15 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 15 | 3 | 0 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 9 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 8 | 4 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 7 | 1 | 0 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 7 | 2 | 0 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 6 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 6 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 1 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 0 | 0 | 287 |

Totals by scope: build: 502 ms, default: 18962 ms, project: 58944 ms

## Configure project (287 projects, sum 16078 ms)

| Project | ms |
|---|---|
| : | 1205 |
| :ant | 903 |
| :build-logic-settings:architecture-docs | 685 |
| :build-logic | 494 |
| :docs | 385 |
| :build-logic:binary-compatibility | 312 |
| :bean-serialization-services | 285 |
| :architecture-test | 262 |
| :build-logic-commons:basics | 252 |
| :declarative-dsl-core | 217 |
| :kotlin-dsl | 183 |
| :declarative-dsl-evaluator | 144 |
| :distributions-full | 141 |
| :wrapper-main | 141 |
| :build-cache | 133 |
| :kotlin-dsl-integ-tests | 133 |
| :build-logic-settings | 130 |
| :distributions-basics | 129 |
| :performance | 129 |
| :precondition-tester | 115 |
| :internal-build-reports | 107 |
| :kotlin-dsl-plugins | 105 |
| :base-services | 102 |
| :process-services-base | 101 |
| :tooling-api | 88 |
| :public-api | 80 |
| :api-metadata | 74 |
| :enterprise-plugin-performance | 70 |
| :ant-api | 65 |
| :configuration-problems-base | 62 |
| :core-serialization-codecs | 60 |
| :configuration-cache | 59 |
| :build-cache-base | 57 |
| :plugin-development | 57 |
| :build-cache-core | 54 |
| :build-init-specs | 52 |
| :build-state | 52 |
| :base-compiler-worker | 51 |
| :testing-native | 51 |
| :toolchains-jvm | 51 |

## Tasks (364 executed, sum 3120 ms)

| Task | ms |
|---|---|
| :build-logic:uber-plugins:compileKotlin | 163 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 109 |
| :build-logic:packaging:compileKotlin | 80 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 71 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 68 |
| :build-logic:integration-testing:compileKotlin | 66 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 65 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 63 |
| :build-logic:documentation:compileGroovy | 62 |
| :build-logic-commons:gradle-plugin:compileKotlin | 57 |
| :build-logic:buildquality:compileKotlin | 53 |
| :build-logic:jvm:compileKotlin | 49 |
| :build-logic:packaging:compilePluginsBlocks | 41 |
| :build-logic-commons:gradle-plugin:generatePrecompiledScriptPluginAccessors | 37 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 37 |
| :build-logic-commons:build-platform-plugin:compilePluginsBlocks | 35 |
| :build-logic:cleanup:compilePluginsBlocks | 35 |
| :build-logic:dependency-modules:compilePluginsBlocks | 34 |
| :build-logic-commons:basics:compilePluginsBlocks | 33 |
| :build-logic-settings:build-environment:compileKotlin | 30 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 30 |
| :build-logic:lifecycle:compilePluginsBlocks | 30 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 28 |
| :build-logic:profiling:compileKotlin | 28 |
| :build-logic:binary-compatibility:compileKotlin | 28 |
| :build-logic-commons:publishing:compileKotlin | 28 |
| :build-logic:idea:compileKotlin | 27 |
| :build-logic:idea:generateExternalPluginSpecBuilders | 26 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 26 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 25 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 25 |
| :build-logic-commons:module-identity:compilePluginsBlocks | 25 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 25 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 24 |
| :build-logic-settings:version-catalogs:compileKotlin | 24 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 24 |
| :build-logic:cleanup:compileKotlin | 24 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 23 |
| :build-logic:integration-testing-kotlin:compileKotlin | 23 |
| :build-logic-commons:basics:compileKotlin | 22 |
