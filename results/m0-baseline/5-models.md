Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m0-baseline/5-trace-log.txt`

Overall span: 22173 ms, 672598 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 27194 | 407 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 5584 | 411 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 5358 | 5316 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 4607 | 310 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 4387 | 4387 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 3443 | 368 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 2889 | 820 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 1260 | 659 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 823 | 32 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 584 | 509 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 253 | 253 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 253 | 5 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 145 | 14 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 95 | 7 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 90 | 16 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 82 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 72 | 12 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 61 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 61 | 7 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 57 | 27 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 56 | 5 | 0 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 50 | 6 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 48 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 41 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 39 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 37 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 31 | 3 | 0 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 28 | 1 | 0 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 17 | 7 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 14 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 14 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 11 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 10 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 10 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 6 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 6 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 584 ms, default: 4640 ms, project: 52550 ms

## Configure project (287 projects, sum 25445 ms)

| Project | ms |
|---|---|
| :architecture-test | 597 |
| :docs | 588 |
| :api-metadata | 568 |
| :antlr | 438 |
| :kotlin-dsl | 437 |
| :declarative-dsl-core | 431 |
| :ant-worker | 404 |
| :ant-impl | 387 |
| : | 376 |
| :kotlin-dsl-integ-tests | 368 |
| :ant | 347 |
| :ant-api | 339 |
| :base-asm | 335 |
| :base-compiler-worker | 333 |
| :distributions-full | 309 |
| :declarative-dsl-evaluator | 239 |
| :public-api | 220 |
| :internal-build-reports | 219 |
| :tooling-api | 213 |
| :distributions-basics | 186 |
| :encryption-services | 176 |
| :ear | 165 |
| :distributions-core | 163 |
| :domain-object-collections | 155 |
| :bean-serialization-services | 149 |
| :docs-asciidoctor-extensions | 147 |
| :docs-asciidoctor-extensions-base | 142 |
| :distributions-publishing | 141 |
| :distributions-jvm | 134 |
| :distributions-native | 134 |
| :model-core | 134 |
| :toolchains-jvm | 134 |
| :logging-api | 132 |
| :time | 131 |
| :stdlib-java-extensions | 130 |
| :installation-beacon | 126 |
| :maven | 126 |
| :launcher | 125 |
| :soak | 125 |
| :tooling-api-provider | 125 |

## Tasks (364 executed, sum 876 ms)

| Task | ms |
|---|---|
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 31 |
| :build-logic:uber-plugins:compileKotlin | 29 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 19 |
| :build-logic:integration-testing:compileKotlin | 16 |
| :build-logic:buildquality:compileKotlin | 15 |
| :build-logic:lifecycle:generatePrecompiledScriptPluginAccessors | 13 |
| :build-logic:cleanup:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic:cleanup:compileKotlin | 12 |
| :build-logic:packaging:compileKotlin | 12 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:jvm:compileKotlin | 11 |
| :build-logic:documentation:compileGroovy | 11 |
| :build-logic:performance-testing:compileGroovy | 11 |
| :build-logic:kotlin-dsl:compileKotlin | 11 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:integration-testing-kotlin:compileKotlin | 11 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 10 |
| :build-logic:idea:generateScriptPluginAdapters | 10 |
| :build-logic:dependency-modules:compileKotlin | 10 |
| :build-logic:profiling:compileKotlin | 10 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 9 |
| :build-logic:cleanup:compilePluginsBlocks | 9 |
| :build-logic:lifecycle:compileKotlin | 9 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 9 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 8 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 8 |
| :build-logic-commons:basics:compilePluginsBlocks | 8 |
| :build-logic:integration-testing:processResources | 8 |
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 8 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:idea:compileKotlin | 8 |
| :build-logic:integration-testing:compilePluginsBlocks | 8 |
| :build-logic:profiling:compilePluginsBlocks | 8 |
| :build-logic:root-build:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic:root-build:compileKotlin | 8 |
| :build-logic:binary-compatibility:compileKotlin | 8 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 8 |
