Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/heapx-builders/3-trace-log.txt`

Overall span: 16740 ms, 672673 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 24838 | 471 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 8478 | 8478 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 5991 | 413 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 4245 | 266 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 2502 | 332 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 488 | 456 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 418 | 418 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 357 | 85 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 246 | 3 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 227 | 4 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 209 | 5 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 187 | 167 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 95 | 6 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 58 | 11 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 48 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 37 | 6 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 36 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 36 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 26 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 19 | 3 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 12 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 8 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 7 | 1 | 0 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 6 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 5 | 2 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 5 | 1 | 0 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 4 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 2 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 187 ms, default: 8896 ms, project: 39649 ms

## Configure project (287 projects, sum 36648 ms)

| Project | ms |
|---|---|
| :api-metadata | 874 |
| :declarative-dsl-core | 756 |
| :docs | 754 |
| :base-compiler-worker | 701 |
| :kotlin-dsl | 675 |
| :ant | 666 |
| :architecture-test | 606 |
| :base-asm | 593 |
| :antlr | 590 |
| :kotlin-dsl-integ-tests | 535 |
| :ant-impl | 531 |
| : | 528 |
| :declarative-dsl-evaluator | 503 |
| :distributions-full | 496 |
| :ant-api | 495 |
| :ant-worker | 471 |
| :distributions-basics | 366 |
| :distributions-core | 315 |
| :performance | 313 |
| :tooling-api | 285 |
| :bean-serialization-services | 262 |
| :distributions-native | 259 |
| :distributions-jvm | 254 |
| :kotlin-dsl-plugins | 254 |
| :enterprise-plugin-performance | 207 |
| :kotlin-dsl-tooling-builders | 206 |
| :graph-serialization | 201 |
| :groovy-loader | 195 |
| :jvm-services | 183 |
| :java-compiler-worker | 179 |
| :flow-services | 178 |
| :build-cache-packaging | 173 |
| :build-cache | 169 |
| :classloaders | 169 |
| :scala-compiler-worker | 169 |
| :distributions-publishing | 163 |
| :kotlin-dsl-provider-plugins | 162 |
| :serialization | 161 |
| :internal-build-reports | 160 |
| :plugin-development | 159 |

## Tasks (364 executed, sum 1472 ms)

| Task | ms |
|---|---|
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 72 |
| :build-logic:uber-plugins:compileKotlin | 36 |
| :build-logic:integration-testing-kotlin:compileKotlin | 30 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 24 |
| :build-logic:documentation:compileGroovy | 24 |
| :build-logic:lifecycle:compilePluginsBlocks | 23 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 23 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 21 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 20 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 20 |
| :build-logic:integration-testing:compileKotlin | 20 |
| :build-logic:buildquality:compileKotlin | 20 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 19 |
| :build-logic-commons:publishing:generateExternalPluginSpecBuilders | 19 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 19 |
| :build-logic:idea:compilePluginsBlocks | 18 |
| :build-logic:cleanup:compilePluginsBlocks | 18 |
| :build-logic:idea:compileKotlin | 18 |
| :build-logic:binary-compatibility:compileKotlin | 17 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 16 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 15 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 15 |
| :build-logic-commons:gradle-plugin:compileKotlin | 15 |
| :build-logic:lifecycle:compileKotlin | 15 |
| :build-logic:cleanup:compileJava | 15 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 15 |
| :build-logic:profiling:compileKotlin | 15 |
| :build-logic:kotlin-dsl:compileKotlin | 15 |
| :build-logic:packaging:compileKotlin | 15 |
| :build-logic-settings:build-environment:compileKotlin | 14 |
| :build-logic-commons:build-platform-plugin:compilePluginsBlocks | 14 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 14 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 13 |
| :build-logic:jvm:compileKotlin | 13 |
| :build-logic:buildquality:compilePluginsBlocks | 13 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 12 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 12 |
| :build-logic-settings:version-catalogs:compileKotlin | 12 |
| :build-logic:cleanup:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic:integration-testing:compilePluginsBlocks | 12 |
