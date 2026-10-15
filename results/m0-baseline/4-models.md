Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m0-baseline/4-trace-log.txt`

Overall span: 15784 ms, 661960 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 24495 | 520 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 4776 | 452 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 4306 | 4306 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 4301 | 266 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 3973 | 3944 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 3573 | 362 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 801 | 503 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 243 | 243 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 217 | 18 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 215 | 17 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 143 | 114 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 64 | 15 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 36 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 35 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 28 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 28 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 24 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 20 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 17 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 14 | 2 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 14 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 11 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 8 | 6 | 0 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 6 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 6 | 1 | 0 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 4 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 3 | 1 | 0 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 3 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 0 | 0 | 287 |

Totals by scope: build: 143 ms, default: 4549 ms, project: 42751 ms

## Configure project (287 projects, sum 25144 ms)

| Project | ms |
|---|---|
| :core | 740 |
| :architecture-test | 643 |
| :ant-impl | 574 |
| :docs | 525 |
| :base-asm | 455 |
| :ant-api | 402 |
| : | 367 |
| :api-metadata | 332 |
| :base-compiler-worker | 310 |
| :antlr | 301 |
| :ant-worker | 278 |
| :ant | 269 |
| :declarative-dsl-core | 207 |
| :performance | 198 |
| :build-logic:integration-testing-kotlin | 196 |
| :build-logic:dependency-modules | 195 |
| :build-logic:documentation | 193 |
| :build-logic:integration-testing | 192 |
| :kotlin-dsl | 190 |
| :build-logic:jvm | 184 |
| :build-logic:kotlin-dsl-shared-runtime | 180 |
| :graph-isolation | 179 |
| :build-logic:kotlin-dsl | 178 |
| :build-logic:lifecycle | 177 |
| :build-configuration | 154 |
| :graph-serialization | 148 |
| :service-registry-impl | 147 |
| :groovy-loader | 145 |
| :guava-serialization-codecs | 140 |
| :build-init-specs | 138 |
| :ivy | 137 |
| :groovydoc-worker | 134 |
| :build-cache-local | 132 |
| :groovy-compiler-worker | 132 |
| :build-logic | 128 |
| :kotlin-dsl-integ-tests | 127 |
| :java-compiler-plugin | 125 |
| :smoke-test | 124 |
| :javadoc | 119 |
| :snapshots | 118 |

## Tasks (364 executed, sum 988 ms)

| Task | ms |
|---|---|
| :build-logic-commons:basics:compilePluginsBlocks | 37 |
| :build-logic:uber-plugins:compileKotlin | 31 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 26 |
| :build-logic:buildquality:compileKotlin | 26 |
| :build-logic:integration-testing-kotlin:processResources | 26 |
| :build-logic-commons:build-platform-plugin:generatePrecompiledScriptPluginAccessors | 23 |
| :build-logic:packaging:compileKotlin | 21 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 20 |
| :build-logic:integration-testing-kotlin:compileKotlin | 18 |
| :build-logic:integration-testing:compileKotlin | 17 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 14 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 13 |
| :build-logic:documentation:compileGroovy | 12 |
| :build-logic:profiling:compileKotlin | 12 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 11 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:jvm:compileKotlin | 11 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:kotlin-dsl:compileKotlin | 11 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:packaging:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 10 |
| :build-logic-commons:build-platform-plugin:compileKotlin | 10 |
| :build-logic:idea:compileKotlin | 10 |
| :build-logic:cleanup:compilePluginsBlocks | 9 |
| :build-logic:cleanup:compileKotlin | 9 |
| :build-logic:lifecycle:compileKotlin | 9 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 9 |
| :build-logic:root-build:compileKotlin | 9 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 9 |
| :build-logic:uber-plugins:compilePluginsBlocks | 9 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 8 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 8 |
| :build-logic:idea:compilePluginsBlocks | 8 |
| :build-logic:root-build:generatePrecompiledScriptPluginAccessors | 8 |
| :build-logic-commons:publishing:generateExternalPluginSpecBuilders | 8 |
| :build-logic:binary-compatibility:compileKotlin | 8 |
| :build-logic-commons:publishing:compilePluginsBlocks | 8 |
