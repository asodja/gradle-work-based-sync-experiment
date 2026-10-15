Trace: `/tmp/claude-1000/-home-agent-projects-gradle-work-based-sync-experiment/03bfd696-8798-4573-b4b6-36c58133e9f7/scratchpad/logs/gg-first-trace-log.txt`

Overall span: 1569492 ms, 668312 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.jetbrains.plugins.gradle.model.GradleSourceSetDependencyModel | project | 287 | 7249785 | 622532 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinGradleModel | project | 287 | 867827 | 84080 | 0 |
| org.gradle.tooling.model.kotlin.dsl.KotlinDslScriptsModel | project | 4 | 344624 | 339495 | 0 |
| org.jetbrains.plugins.gradle.model.GradleSourceSetModel | project | 287 | 161959 | 26328 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 112430 | 112430 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.warmUp.GradleTaskWarmUpRequest | project | 287 | 12284 | 1097 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 10881 | 10881 | 0 |
| org.gradle.tooling.model.idea.IdeaProject | build | 4 | 3029 | 3007 | 0 |
| com.intellij.openapi.externalSystem.model.project.dependencies.ProjectDependencies | project | 287 | 929 | 647 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 876 | 529 | 0 |
| org.jetbrains.plugins.gradle.model.VersionCatalogsModel | project | 287 | 379 | 353 | 0 |
| org.jetbrains.plugins.gradle.model.AnnotationProcessingModel | project | 287 | 286 | 15 | 0 |
| org.jetbrains.plugins.gradle.model.GradleTaskModel | project | 287 | 242 | 22 | 0 |
| org.jetbrains.plugins.gradle.model.tests.ExternalTestsModel | project | 287 | 82 | 16 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.sourceSetArtifactIndex.GradleSourceSetArtifactBuildRequest | project | 287 | 74 | 5 | 0 |
| org.jetbrains.plugins.gradle.model.GradleExtensions | project | 287 | 70 | 12 | 0 |
| org.jetbrains.plugins.gradle.model.ExternalProject | project | 287 | 49 | 4 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 42 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 41 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 40 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 40 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 39 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.kapt.KaptGradleModel | project | 287 | 39 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 36 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 34 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.IntelliJProjectSettings | project | 287 | 29 | 26 | 0 |
| org.jetbrains.plugins.gradle.javaModel.JavaGradleManifestModel | project | 287 | 20 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.RepositoryModels | project | 287 | 19 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.dependencyDownloadPolicyModel.GradleDependencyDownloadPolicy | project | 287 | 12 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 11 | 1 | 287 |
| org.jetbrains.plugins.gradle.model.IntelliJSettings | project | 287 | 10 | 5 | 0 |
| com.intellij.gradle.toolingExtension.model.repositoryModel.ProjectRepositoriesModel | project | 287 | 9 | 1 | 0 |
| org.jetbrains.plugins.gradle.model.DependencyAccessorsModel | project | 287 | 7 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 4 | 1 | 0 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 1 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 3030 ms, default: 123311 ms, project: 8639911 ms

## Configure project (287 projects, sum 369105 ms)

| Project | ms |
|---|---|
| : | 57147 |
| :base-asm | 27494 |
| :ant-impl | 27451 |
| :antlr | 27407 |
| :ant-worker | 27355 |
| :base-compiler-worker | 27310 |
| :architecture-test | 27269 |
| :ant | 27251 |
| :api-metadata | 27234 |
| :ant-api | 27227 |
| :build-logic | 11483 |
| :distributions-core | 7546 |
| :distributions-basics | 6803 |
| :build-logic-settings:version-catalogs | 6302 |
| :scoped-persistent-cache | 5664 |
| :kotlin-dsl | 4789 |
| :wrapper-main | 4681 |
| :declarative-dsl-evaluator | 2006 |
| :build-logic-settings:default-settings-plugins | 1143 |
| :daemon-services | 963 |
| :declarative-dsl-core | 341 |
| :docs | 275 |
| :build-init-specs-api | 203 |
| :build-init-specs | 197 |
| :flow-services | 196 |
| :build-operations | 186 |
| :classloaders | 186 |
| :build-process-services | 185 |
| :performance | 185 |
| :classpath | 169 |
| :build-option | 168 |
| :daemon-protocol | 168 |
| :build-state | 166 |
| :daemon-messaging | 166 |
| :daemon-server-worker | 160 |
| :build-profile | 155 |
| :core | 152 |
| :daemon-server | 142 |
| :configuration-cache | 141 |
| :platform-jvm | 141 |
| :declarative-dsl-api | 134 |
| :scaladoc-worker | 117 |
| :declarative-dsl-tooling-builders | 106 |
| :core-serialization-codecs | 102 |
| :declarative-dsl-tooling-models | 99 |
| :declarative-dsl-provider | 97 |
| :build-init | 95 |
| :build-cache-local | 94 |
| :kotlin-dsl-integ-tests | 94 |
| :build-discovery-impl | 92 |
| :core-flow-services-api | 90 |
| :composite-builds | 89 |
| :configuration-problems-base | 89 |
| :core-kotlin-extensions | 89 |
| :build-cache-example-client | 88 |
| :cli | 88 |
| :credentials-impl | 88 |
| :dependency-management | 85 |
| :configuration-cache-base | 84 |
| :bean-serialization-services | 83 |

## Tasks (364 executed, sum 64579 ms)

| Task | ms |
|---|---|
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 23232 |
| :build-logic:performance-testing:compileGroovy | 13262 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 11326 |
| :build-logic-commons:publishing:generateExternalPluginSpecBuilders | 5406 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 2798 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 2182 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 1970 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 1433 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 1071 |
| :build-logic-settings:architecture-docs:generateExternalPluginSpecBuilders | 781 |
| :build-logic:uber-plugins:compileKotlin | 40 |
| :build-logic:documentation:compileGroovy | 37 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 22 |
| :build-logic:buildquality:compileKotlin | 20 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 20 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 18 |
| :build-logic-commons:gradle-plugin:compileKotlin | 18 |
| :build-logic:jvm:compileKotlin | 18 |
| :build-logic:integration-testing:compileKotlin | 18 |
| :build-logic:dependency-modules:compileKotlin | 16 |
| :build-logic:idea:compileKotlin | 16 |
| :build-logic:packaging:compileKotlin | 16 |
| :build-logic:lifecycle:compileKotlin | 15 |
| :build-logic:integration-testing:compilePluginsBlocks | 15 |
| :build-logic-commons:publishing:compilePluginsBlocks | 15 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 14 |
| :build-logic-commons:publishing:compileKotlin | 14 |
| :build-logic:buildquality:compilePluginsBlocks | 14 |
| :build-logic-commons:build-platform-plugin:compileKotlin | 13 |
| :build-logic:idea:generateExternalPluginSpecBuilders | 13 |
| :build-logic:idea:compilePluginsBlocks | 13 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 13 |
| :build-logic:profiling:compileKotlin | 13 |
| :build-logic-settings:architecture-docs:extractPrecompiledScriptPluginPlugins | 12 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 12 |
| :build-logic-settings:architecture-docs:compilePluginsBlocks | 12 |
| :build-logic-commons:basics:compilePluginsBlocks | 12 |
| :build-logic:dependency-modules:compilePluginsBlocks | 12 |
| :build-logic:lifecycle:compilePluginsBlocks | 12 |
| :build-logic:root-build:compileKotlin | 12 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 12 |
| :build-logic-commons:build-platform-plugin:compilePluginsBlocks | 11 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 11 |
| :build-logic:cleanup:compileKotlin | 11 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:uber-plugins:compilePluginsBlocks | 11 |
| :build-logic:integration-testing-kotlin:compileKotlin | 11 |
| :build-logic-settings:build-environment:compileKotlin | 10 |
| :build-logic-settings:architecture-docs:compileKotlin | 10 |
| :build-logic-commons:basics:compileKotlin | 10 |
| :build-logic-commons:gradle-plugin:compilePluginsBlocks | 10 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 10 |
| :build-logic:binary-compatibility:compileKotlin | 10 |
| :build-logic:kotlin-dsl:compileKotlin | 10 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 9 |
| :build-logic-settings:version-catalogs:compileKotlin | 9 |
| :build-logic:build-update-utils:compileKotlin | 9 |
| :build-logic:profiling:compilePluginsBlocks | 9 |
| :build-logic:packaging:compilePluginsBlocks | 9 |
| :build-logic:packaging:generatePrecompiledScriptPluginAccessors | 9 |
