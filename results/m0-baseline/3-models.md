Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m0-baseline/3-trace-log.txt`

Overall span: 20959 ms, 672772 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 29814 | 866 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 6246 | 6246 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 6130 | 532 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 5855 | 5818 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 5290 | 308 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 5193 | 328 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 444 | 444 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 327 | 23 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 314 | 26 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 213 | 4 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 191 | 157 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 129 | 11 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 62 | 12 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 61 | 7 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 34 | 6 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 33 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 29 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 25 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 21 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 20 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 14 | 3 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 13 | 10 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 8 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 7 | 1 | 0 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 5 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 5 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 5 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 4 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 4 | 1 | 0 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 191 ms, default: 6690 ms, project: 53740 ms

## Configure project (287 projects, sum 36214 ms)

| Project | ms |
|---|---|
| :kotlin-dsl | 1749 |
| :internal-build-reports | 1181 |
| :kotlin-dsl-integ-tests | 927 |
| :declarative-dsl-core | 888 |
| :distributions-full | 631 |
| :base-asm | 626 |
| :ant-worker | 625 |
| :ant | 620 |
| :architecture-test | 605 |
| :api-metadata | 602 |
| :base-compiler-worker | 598 |
| :declarative-dsl-evaluator | 590 |
| :ant-impl | 575 |
| :antlr | 572 |
| :ant-api | 571 |
| : | 562 |
| :public-api | 555 |
| :docs | 467 |
| :distributions-core | 363 |
| :distributions-basics | 359 |
| :distributions-jvm | 324 |
| :distributions-native | 308 |
| :distributions-publishing | 294 |
| :enterprise-plugin-performance | 245 |
| :tooling-api | 225 |
| :bean-serialization-services | 224 |
| :process-services-api | 203 |
| :core | 194 |
| :build-init-specs-api | 191 |
| :build-init | 190 |
| :build-operations-trace | 183 |
| :build-cache | 179 |
| :configuration-cache | 179 |
| :base-services | 178 |
| :concurrent | 174 |
| :wrapper-main | 173 |
| :kotlin-dsl-plugins | 170 |
| :configuration-cache-base | 169 |
| :build-logic | 166 |
| :configuration-problems-base | 164 |

## Tasks (364 executed, sum 1501 ms)

| Task | ms |
|---|---|
| :build-logic:idea:generateExternalPluginSpecBuilders | 50 |
| :build-logic:dependency-modules:compilePluginsBlocks | 46 |
| :build-logic:cleanup:compilePluginsBlocks | 37 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 36 |
| :build-logic:lifecycle:compilePluginsBlocks | 32 |
| :build-logic-commons:publishing:generateExternalPluginSpecBuilders | 31 |
| :build-logic:kotlin-dsl:pluginDescriptors | 28 |
| :build-logic:uber-plugins:compileKotlin | 28 |
| :build-logic:uber-plugins:extractPrecompiledScriptPluginPlugins | 27 |
| :build-logic:packaging:processResources | 26 |
| :build-logic:binary-compatibility:extractPluginRequests | 25 |
| :build-logic:binary-compatibility:compileKotlin | 23 |
| :build-logic:idea:compilePluginsBlocks | 22 |
| :build-logic-commons:publishing:compilePluginsBlocks | 22 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 21 |
| :build-logic-commons:basics:compilePluginsBlocks | 19 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 18 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 18 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 18 |
| :build-logic-commons:build-platform-plugin:compilePluginsBlocks | 18 |
| :build-logic:documentation:compileGroovy | 18 |
| :build-logic:integration-testing-kotlin:compileKotlin | 18 |
| :build-logic:integration-testing:compilePluginsBlocks | 17 |
| :build-logic:kotlin-dsl:compileKotlin | 17 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 16 |
| :build-logic-commons:publishing:compileKotlin | 16 |
| :build-logic:buildquality:compileKotlin | 15 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 15 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 14 |
| :build-logic:integration-testing:compileKotlin | 14 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 14 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 13 |
| :build-logic:dependency-modules:compileKotlin | 13 |
| :build-logic:build-update-utils:compilePluginsBlocks | 13 |
| :build-logic:performance-testing:compileKotlin | 13 |
| :build-logic:buildquality:compilePluginsBlocks | 13 |
| :build-logic:packaging:compileKotlin | 13 |
| :build-logic-settings:architecture-docs:compilePluginsBlocks | 12 |
| :build-logic:jvm:compileKotlin | 12 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 12 |
