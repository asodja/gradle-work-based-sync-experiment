Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/heap-builders/3-trace-log.txt`

Overall span: 15484 ms, 672248 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 28540 | 676 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 6938 | 6938 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 5848 | 383 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 4848 | 309 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 3516 | 317 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 432 | 432 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 424 | 364 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 356 | 30 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 242 | 17 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 235 | 18 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 146 | 131 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 125 | 10 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 53 | 10 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 48 | 16 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 47 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 37 | 4 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 37 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 35 | 8 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 33 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 28 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 28 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 24 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 23 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 12 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 11 | 2 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 10 | 7 | 0 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 6 | 1 | 0 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 6 | 1 | 0 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 5 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 5 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 5 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 5 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 2 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 1 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 146 ms, default: 7370 ms, project: 44634 ms

## Configure project (287 projects, sum 31088 ms)

| Project | ms |
|---|---|
| :base-compiler-worker | 619 |
| :base-asm | 596 |
| :docs | 596 |
| :architecture-test | 568 |
| :ant | 563 |
| : | 512 |
| :ant-worker | 507 |
| :kotlin-dsl | 488 |
| :ant-api | 479 |
| :declarative-dsl-core | 463 |
| :ant-impl | 399 |
| :api-metadata | 390 |
| :antlr | 378 |
| :kotlin-dsl-integ-tests | 360 |
| :declarative-dsl-evaluator | 357 |
| :tooling-api | 356 |
| :distributions-full | 337 |
| :internal-build-reports | 308 |
| :public-api | 307 |
| :wrapper-main | 257 |
| :performance | 214 |
| :build-cache | 200 |
| :configuration-cache | 192 |
| :bean-serialization-services | 190 |
| :distributions-core | 184 |
| :toolchains-jvm | 184 |
| :build-cache-base | 180 |
| :enterprise-plugin-performance | 179 |
| :distributions-basics | 173 |
| :build-logic | 167 |
| :worker-shared | 167 |
| :workers | 163 |
| :ide-plugins | 160 |
| :project-features-demos | 160 |
| :core | 157 |
| :guava-serialization-codecs | 156 |
| :ide-native | 153 |
| :configuration-cache-base | 151 |
| :worker-main | 151 |
| :installation-beacon | 148 |

## Tasks (364 executed, sum 1527 ms)

| Task | ms |
|---|---|
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 60 |
| :build-logic:uber-plugins:compileKotlin | 33 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 30 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 29 |
| :build-logic:dependency-modules:compilePluginsBlocks | 24 |
| :build-logic:integration-testing:compileKotlin | 24 |
| :build-logic:integration-testing-kotlin:compileKotlin | 24 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 22 |
| :build-logic:jvm:compileKotlin | 21 |
| :build-logic:binary-compatibility:compileKotlin | 21 |
| :build-logic-commons:publishing:compilePluginsBlocks | 21 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 20 |
| :build-logic:idea:compilePluginsBlocks | 19 |
| :build-logic:lifecycle:generatePrecompiledScriptPluginAccessors | 18 |
| :build-logic:documentation:compileGroovy | 18 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 17 |
| :build-logic-settings:build-environment:compileKotlin | 17 |
| :build-logic:dependency-modules:generatePrecompiledScriptPluginAccessors | 17 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 17 |
| :build-logic:performance-testing:compileKotlin | 17 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 16 |
| :build-logic-commons:basics:compilePluginsBlocks | 16 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 16 |
| :build-logic:buildquality:compileKotlin | 16 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 16 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 15 |
| :build-logic-commons:build-platform-plugin:compilePluginsBlocks | 15 |
| :build-logic:idea:compileKotlin | 15 |
| :build-logic:binary-compatibility:compileJava | 15 |
| :build-logic:kotlin-dsl:compileKotlin | 15 |
| :build-logic-commons:gradle-plugin:compileKotlin | 14 |
| :build-logic:lifecycle:compileKotlin | 14 |
| :build-logic:performance-testing:compileGroovy | 14 |
| :build-logic-commons:publishing:generateExternalPluginSpecBuilders | 14 |
| :build-logic:packaging:compileKotlin | 14 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 14 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 13 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 13 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 13 |
| :build-logic:jvm:compilePluginsBlocks | 13 |
