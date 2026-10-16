Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4f-tasks/4-trace-log.txt`

Overall span: 33742 ms, 616797 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 18388 | 18388 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 6775 | 6775 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 301 | 10 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 38 | 12 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 35 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 29 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 27 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 23 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 23 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 15 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 5 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 5 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 2 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 0 ms, default: 25163 ms, project: 545 ms

## Configure project (287 projects, sum 40210 ms)

| Project | ms |
|---|---|
| :build-logic-settings | 18090 |
| :core | 764 |
| :antlr | 447 |
| :docs | 428 |
| :api-metadata | 414 |
| :ant | 380 |
| :base-asm | 371 |
| : | 334 |
| :declarative-dsl-core | 321 |
| :ant-api | 303 |
| :architecture-test | 275 |
| :ant-impl | 273 |
| :ant-worker | 237 |
| :base-compiler-worker | 223 |
| :kotlin-dsl | 177 |
| :performance | 148 |
| :kotlin-dsl-integ-tests | 147 |
| :core-serialization-codecs | 140 |
| :kotlin-dsl-tooling-builders | 139 |
| :graph-isolation | 136 |
| :resources | 136 |
| :plugins-model-native | 135 |
| :problems-rendering | 135 |
| :declarative-dsl-evaluator | 132 |
| :kotlin-dsl-tooling-model-impls | 129 |
| :plugins-java-library | 129 |
| :jvm-compiler-worker | 126 |
| :security | 125 |
| :gradle-cli-main | 123 |
| :resources-http | 122 |
| :declarative-dsl-tooling-models | 121 |
| :java-platform | 120 |
| :precondition-tester | 118 |
| :scaladoc-worker | 118 |
| :build-profile | 117 |
| :encryption-services | 117 |
| :scoped-persistent-cache | 116 |
| :kotlin-dsl-provider-plugins | 114 |
| :daemon-logging | 113 |
| :groovy-loader | 113 |

## Tasks (651 executed, sum 1414 ms)

| Task | ms |
|---|---|
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 58 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 42 |
| :build-logic:integration-testing-kotlin:jar | 39 |
| :build-logic:dependency-modules:generatePrecompiledScriptPluginAccessors | 35 |
| :build-logic:lifecycle:compilePluginsBlocks | 35 |
| :build-logic:uber-plugins:compileKotlin | 32 |
| :build-logic:integration-testing-kotlin:compileKotlin | 25 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 21 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 19 |
| :build-logic:integration-testing:compileKotlin | 19 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 18 |
| :build-logic:documentation:compileGroovy | 16 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 16 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 15 |
| :build-logic:buildquality:compileKotlin | 15 |
| :build-logic:packaging:compileKotlin | 13 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 12 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:performance-testing:compileGroovy | 11 |
| :build-logic-settings:version-catalogs:generateExternalPluginSpecBuilders | 10 |
| :build-logic-settings:build-environment:generateExternalPluginSpecBuilders | 10 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 10 |
| :build-logic:dependency-modules:compilePluginsBlocks | 10 |
| :build-logic:jvm:compileKotlin | 10 |
| :build-logic:profiling:compileKotlin | 10 |
| :build-logic:kotlin-dsl:compileKotlin | 10 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 10 |
| :build-logic-commons:build-platform-plugin:generateExternalPluginSpecBuilders | 9 |
| :build-logic-commons:basics:compilePluginsBlocks | 9 |
| :build-logic:idea:compilePluginsBlocks | 9 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 9 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:root-build:compileKotlin | 9 |
| :build-logic:packaging:generatePrecompiledScriptPluginAccessors | 9 |
| :build-cache:ideProjectModel | 9 |
| :build-logic-settings:build-environment:compileKotlin | 8 |
| :build-logic-settings:version-catalogs:compileKotlin | 8 |
| :build-logic:idea:compileKotlin | 8 |
| :build-logic:lifecycle:compileKotlin | 8 |
| :build-logic:build-update-utils:compileKotlin | 8 |
