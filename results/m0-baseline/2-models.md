Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m0-baseline/2-trace-log.txt`

Overall span: 18817 ms, 659860 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 20080 | 597 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 7856 | 7856 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 4898 | 4803 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 4281 | 238 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 3291 | 299 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 1709 | 170 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 387 | 352 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 304 | 304 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 300 | 38 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 227 | 33 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 189 | 4 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 47 | 11 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 42 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 28 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 28 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 24 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 24 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 21 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 18 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 12 | 2 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 12 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 9 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 8 | 6 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 5 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 5 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 4 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 4 | 1 | 287 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 3 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 3 | 1 | 0 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 2 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 2 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 1 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 387 ms, default: 8160 ms, project: 35344 ms

## Configure project (287 projects, sum 6849 ms)

| Project | ms |
|---|---|
| : | 275 |
| :ant | 241 |
| :docs | 136 |
| :project-features-api | 85 |
| :build-logic | 84 |
| :versioned-cache | 80 |
| :kotlin-dsl-tooling-builders | 79 |
| :declarative-dsl-evaluator | 66 |
| :performance | 66 |
| :build-option | 61 |
| :file-watching | 60 |
| :declarative-dsl-core | 59 |
| :dependency-management-resources | 47 |
| :kotlin-dsl | 38 |
| :architecture-test | 37 |
| :wrapper-main | 37 |
| :declarative-dsl-tooling-builders | 35 |
| :base-services | 32 |
| :declarative-dsl-provider | 32 |
| :dependency-management-serialization-codecs | 32 |
| :kotlin-dsl-integ-tests | 32 |
| :bean-serialization-services | 31 |
| :configuration-cache | 31 |
| :kotlin-dsl-plugins | 31 |
| :declarative-dsl-tooling-models | 30 |
| :precondition-tester | 30 |
| :toolchains-jvm | 30 |
| :stdlib-kotlin-extensions | 29 |
| :problems | 28 |
| :war | 28 |
| :build-cache | 27 |
| :tooling-api | 27 |
| :core-kotlin-extensions | 26 |
| :graph-isolation | 26 |
| :isolated-action-services | 26 |
| :kotlin-dsl-tooling-model-impls | 26 |
| :project-features-demos | 26 |
| :soak | 26 |
| :stdlib-serialization-codecs | 26 |
| :configuration-cache-base | 25 |

## Tasks (364 executed, sum 912 ms)

| Task | ms |
|---|---|
| :build-logic:kotlin-dsl:generateExternalPluginSpecBuilders | 47 |
| :build-logic-commons:publishing:compileKotlin | 47 |
| :build-logic:performance-testing:compileKotlin | 45 |
| :build-logic:binary-compatibility:compileGroovy | 43 |
| :build-logic:uber-plugins:compileKotlin | 26 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 22 |
| :build-logic:integration-testing:compileKotlin | 16 |
| :build-logic:documentation:compileGroovy | 16 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 15 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 12 |
| :build-logic:buildquality:compileKotlin | 12 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 11 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 11 |
| :build-logic:packaging:compileKotlin | 11 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 10 |
| :build-logic:jvm:compileKotlin | 10 |
| :build-logic:profiling:compileKotlin | 10 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 9 |
| :build-logic-settings:version-catalogs:compileKotlin | 9 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 9 |
| :build-logic-commons:gradle-plugin:compileKotlin | 9 |
| :build-logic:jvm:extractPrecompiledScriptPluginPlugins | 9 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 9 |
| :build-logic:kotlin-dsl:compileKotlin | 9 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 8 |
| :build-logic:idea:compilePluginsBlocks | 8 |
| :build-logic:cleanup:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:dependency-modules:compileKotlin | 8 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 8 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 7 |
| :build-logic-commons:build-platform-plugin:compilePluginsBlocks | 7 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 7 |
| :build-logic:cleanup:compileKotlin | 7 |
| :build-logic:lifecycle:compileKotlin | 7 |
| :build-logic:integration-testing:compilePluginsBlocks | 7 |
| :build-logic:build-update-utils:compileKotlin | 7 |
| :build-logic:buildquality:compilePluginsBlocks | 7 |
| :build-logic:uber-plugins:compilePluginsBlocks | 7 |
| :build-logic:integration-testing-kotlin:compileKotlin | 7 |
| :build-logic-settings:architecture-docs:generateExternalPluginSpecBuilders | 6 |
