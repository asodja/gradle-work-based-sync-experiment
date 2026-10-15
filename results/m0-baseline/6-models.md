Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m0-baseline/6-trace-log.txt`

Overall span: 21780 ms, 671997 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 27107 | 662 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 8324 | 977 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 6436 | 490 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 5894 | 5894 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 3565 | 319 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 3173 | 3133 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 2041 | 26 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 1293 | 133 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 948 | 369 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 842 | 773 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 340 | 340 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 260 | 13 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 118 | 8 | 0 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 97 | 9 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 93 | 7 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 57 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 54 | 9 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 54 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 53 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 51 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 49 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 44 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 39 | 3 | 0 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 35 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 34 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 33 | 1 | 0 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 28 | 3 | 0 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 25 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 24 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 22 | 13 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 14 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 13 | 1 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 13 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 12 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 11 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 8 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 6 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 842 ms, default: 6234 ms, project: 54164 ms

## Configure project (287 projects, sum 30511 ms)

| Project | ms |
|---|---|
| :architecture-test | 615 |
| :api-metadata | 608 |
| :core | 598 |
| :docs | 557 |
| :ant-worker | 555 |
| :ant | 540 |
| :kotlin-dsl | 536 |
| :ant-impl | 472 |
| :kotlin-dsl-integ-tests | 441 |
| : | 438 |
| :base-asm | 436 |
| :declarative-dsl-core | 416 |
| :antlr | 408 |
| :base-compiler-worker | 408 |
| :ant-api | 401 |
| :performance | 304 |
| :public-api | 282 |
| :distributions-full | 279 |
| :declarative-dsl-evaluator | 244 |
| :distributions-basics | 216 |
| :distributions-core | 206 |
| :enterprise-plugin-performance | 206 |
| :precondition-tester | 191 |
| :distributions-jvm | 189 |
| :tooling-api | 186 |
| :stdlib-kotlin-extensions | 171 |
| :build-logic | 169 |
| :plugins-jvm-test-fixtures | 169 |
| :configuration-cache-base | 168 |
| :stdlib-serialization-codecs | 168 |
| :soak | 166 |
| :bean-serialization-services | 165 |
| :javadoc | 164 |
| :enterprise-operations | 161 |
| :java-compiler-worker | 160 |
| :plugins-groovy | 160 |
| :kotlin-dsl-plugins | 159 |
| :software-diagnostics | 158 |
| :internal-build-reports | 157 |
| :execution | 156 |

## Tasks (364 executed, sum 1224 ms)

| Task | ms |
|---|---|
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 34 |
| :build-logic:uber-plugins:compileKotlin | 30 |
| :build-logic-commons:module-identity:compileKotlin | 22 |
| :build-logic:binary-compatibility:compileKotlin | 22 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 21 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 21 |
| :build-logic:kotlin-dsl:compileKotlin | 20 |
| :build-logic:integration-testing:compileKotlin | 19 |
| :build-logic-commons:publishing:generateExternalPluginSpecBuilders | 19 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 19 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 18 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 17 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 17 |
| :build-logic:buildquality:compileKotlin | 17 |
| :build-logic:cleanup:compilePluginsBlocks | 16 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 15 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 15 |
| :build-logic:idea:compileKotlin | 15 |
| :build-logic:integration-testing:compilePluginsBlocks | 15 |
| :build-logic-commons:gradle-plugin:compileKotlin | 14 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 14 |
| :build-logic:performance-testing:compileKotlin | 14 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 13 |
| :build-logic:packaging:compileKotlin | 13 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 12 |
| :build-logic-settings:build-environment:compileKotlin | 12 |
| :build-logic-settings:version-catalogs:compileKotlin | 12 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 12 |
| :build-logic:documentation:compileGroovy | 12 |
| :build-logic:profiling:compileKotlin | 12 |
| :build-logic:kotlin-dsl:generateExternalPluginSpecBuilders | 12 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 11 |
| :build-logic-commons:basics:compilePluginsBlocks | 11 |
| :build-logic:idea:compilePluginsBlocks | 11 |
| :build-logic:dependency-modules:compileKotlin | 11 |
| :build-logic:jvm:compileKotlin | 11 |
| :build-logic:buildquality:processResources | 11 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 11 |
| :build-logic-commons:publishing:compilePluginsBlocks | 11 |
| :build-logic:buildquality:generateExternalPluginSpecBuilders | 11 |
