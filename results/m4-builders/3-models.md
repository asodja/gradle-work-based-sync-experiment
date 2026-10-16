Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4-builders/3-trace-log.txt`

Overall span: 18204 ms, 671839 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 23680 | 534 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 8834 | 802 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 5479 | 5479 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 5223 | 301 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 5117 | 5065 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 3678 | 354 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 340 | 32 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 273 | 273 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 238 | 22 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 226 | 20 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 113 | 95 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 113 | 8 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 63 | 7 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 63 | 6 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 55 | 11 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 38 | 10 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 28 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 26 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 24 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 20 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 17 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 16 | 10 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 14 | 9 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 12 | 2 | 0 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 8 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 7 | 4 | 0 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 6 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 5 | 1 | 0 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 5 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 2 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 1 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 113 ms, default: 5752 ms, project: 47930 ms

## Configure project (287 projects, sum 25702 ms)

| Project | ms |
|---|---|
| :base-compiler-worker | 543 |
| :antlr | 528 |
| :ant-api | 463 |
| :ant-worker | 451 |
| :declarative-dsl-core | 449 |
| :ant | 399 |
| :api-metadata | 399 |
| :kotlin-dsl | 389 |
| :architecture-test | 376 |
| : | 371 |
| :base-asm | 368 |
| :ant-impl | 365 |
| :docs | 331 |
| :distributions-full | 329 |
| :declarative-dsl-evaluator | 328 |
| :kotlin-dsl-integ-tests | 323 |
| :internal-build-reports | 279 |
| :distributions-basics | 242 |
| :performance | 241 |
| :distributions-core | 239 |
| :distributions-jvm | 217 |
| :distributions-native | 183 |
| :serialization | 171 |
| :scala | 167 |
| :distributions-publishing | 164 |
| :scoped-persistent-cache | 163 |
| :scaladoc-worker | 162 |
| :tooling-api | 160 |
| :service-lookup | 159 |
| :scala-compiler-worker | 150 |
| :instrumentation-declarations | 148 |
| :enterprise-plugin-performance | 146 |
| :ide-native | 146 |
| :bean-serialization-services | 145 |
| :security | 145 |
| :wrapper-main | 141 |
| :native | 138 |
| :declarative-dsl-tooling-models | 136 |
| :public-api | 136 |
| :build-logic | 128 |

## Tasks (364 executed, sum 979 ms)

| Task | ms |
|---|---|
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 48 |
| :build-logic:uber-plugins:compileKotlin | 34 |
| :build-logic-commons:gradle-plugin:compilePluginsBlocks | 26 |
| :build-logic:integration-testing-kotlin:compileKotlin | 25 |
| :build-logic:idea:generateExternalPluginSpecBuilders | 24 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 21 |
| :build-logic:integration-testing:compileKotlin | 18 |
| :build-logic:buildquality:compileKotlin | 18 |
| :build-logic:packaging:compileKotlin | 15 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 14 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 14 |
| :build-logic:documentation:compileGroovy | 14 |
| :build-logic:profiling:compileKotlin | 13 |
| :build-logic:performance-testing:compileGroovy | 13 |
| :build-logic-commons:gradle-plugin:compileKotlin | 12 |
| :build-logic:cleanup:compilePluginsBlocks | 12 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 11 |
| :build-logic:build-update-utils:compileKotlin | 11 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 11 |
| :build-logic:kotlin-dsl:compileKotlin | 11 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 10 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 10 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 10 |
| :build-logic:dependency-modules:compileKotlin | 10 |
| :build-logic:jvm:compileKotlin | 10 |
| :build-logic:build-update-utils:generateExternalPluginSpecBuilders | 10 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic:root-build:compileKotlin | 10 |
| :build-logic:uber-plugins:compilePluginsBlocks | 10 |
| :build-logic:integration-testing-kotlin:generateExternalPluginSpecBuilders | 10 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 9 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 9 |
| :build-logic:dependency-modules:compilePluginsBlocks | 9 |
| :build-logic:lifecycle:compilePluginsBlocks | 9 |
| :build-logic:lifecycle:compileKotlin | 9 |
| :build-logic:idea:compileKotlin | 9 |
| :build-logic:jvm:compilePluginsBlocks | 9 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 9 |
