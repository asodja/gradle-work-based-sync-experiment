Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4-builders/1-trace-log.txt`

Overall span: 44839 ms, 670131 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 34172 | 707 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 19107 | 19107 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 11957 | 11833 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 11239 | 866 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 5558 | 525 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 4506 | 354 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 3740 | 3740 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 530 | 490 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 310 | 42 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 309 | 36 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 216 | 3 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 108 | 16 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 107 | 7 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 59 | 5 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 52 | 11 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 46 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 45 | 15 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 38 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 37 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 36 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 35 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 35 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 22 | 1 | 0 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 17 | 11 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 15 | 13 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 15 | 3 | 0 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 13 | 3 | 0 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 9 | 2 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 7 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 6 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 6 | 4 | 0 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 1 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 531 ms, default: 22847 ms, project: 69020 ms

## Configure project (287 projects, sum 17897 ms)

| Project | ms |
|---|---|
| : | 1296 |
| :ant | 971 |
| :build-logic-settings:architecture-docs | 746 |
| :build-logic | 479 |
| :docs | 415 |
| :bean-serialization-services | 350 |
| :build-logic:binary-compatibility | 342 |
| :build-logic-commons:basics | 316 |
| :architecture-test | 293 |
| :declarative-dsl-core | 251 |
| :kotlin-dsl | 200 |
| :wrapper-main | 165 |
| :declarative-dsl-evaluator | 148 |
| :precondition-tester | 139 |
| :kotlin-dsl-integ-tests | 138 |
| :base-services | 137 |
| :distributions-full | 136 |
| :build-logic-settings | 135 |
| :distributions-basics | 130 |
| :build-cache | 125 |
| :performance | 122 |
| :process-services-base | 119 |
| :internal-build-reports | 115 |
| :public-api | 107 |
| :tooling-api | 102 |
| :base-diagnostics | 98 |
| :kotlin-dsl-plugins | 91 |
| :api-metadata | 90 |
| :graph-isolation | 80 |
| :build-cache-local | 76 |
| :enterprise-plugin-performance | 72 |
| :configuration-cache | 70 |
| :gradle-cli-main | 70 |
| :base-asm | 64 |
| :declarative-dsl-tooling-models | 64 |
| :build-cache-spi | 63 |
| :plugins-test-report-aggregation | 62 |
| :tooling-native | 59 |
| :base-services-groovy | 58 |
| :collections | 58 |

## Tasks (364 executed, sum 3246 ms)

| Task | ms |
|---|---|
| :build-logic:uber-plugins:compileKotlin | 164 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 123 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 77 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 76 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 75 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 73 |
| :build-logic-commons:gradle-plugin:compileKotlin | 57 |
| :build-logic-commons:publishing:compileKotlin | 48 |
| :build-logic:buildquality:compileKotlin | 47 |
| :build-logic:integration-testing:compileKotlin | 46 |
| :build-logic:packaging:compileKotlin | 45 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 43 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 43 |
| :build-logic-commons:gradle-plugin:generatePrecompiledScriptPluginAccessors | 40 |
| :build-logic:cleanup:generatePrecompiledScriptPluginAccessors | 38 |
| :build-logic:documentation:compileGroovy | 38 |
| :build-logic:idea:compilePluginsBlocks | 37 |
| :build-logic:kotlin-dsl:generatePrecompiledScriptPluginAccessors | 37 |
| :build-logic-settings:version-catalogs:compileKotlin | 36 |
| :build-logic:lifecycle:compileKotlin | 36 |
| :build-logic:binary-compatibility:compileKotlin | 34 |
| :build-logic:dependency-modules:compileKotlin | 33 |
| :build-logic:jvm:compileKotlin | 33 |
| :build-logic:performance-testing:compileGroovy | 32 |
| :build-logic-settings:build-environment:compileKotlin | 31 |
| :build-logic:profiling:compileKotlin | 31 |
| :build-logic:buildquality:compilePluginsBlocks | 31 |
| :build-logic-commons:build-platform-plugin:compilePluginsBlocks | 29 |
| :build-logic-commons:publishing:compilePluginsBlocks | 29 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 28 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 28 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 27 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 27 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 26 |
| :build-logic:packaging:generatePrecompiledScriptPluginAccessors | 26 |
| :build-logic:lifecycle:generatePrecompiledScriptPluginAccessors | 25 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 25 |
| :build-logic:idea:compileKotlin | 25 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 24 |
| :build-logic-commons:basics:compileKotlin | 24 |
