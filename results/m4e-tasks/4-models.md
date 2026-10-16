Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4e-tasks/4-trace-log.txt`

Overall span: 24652 ms, 616869 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 5207 | 5207 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 311 | 16 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 269 | 269 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 43 | 3 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 37 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 33 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 29 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 23 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 21 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 17 | 3 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 5 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 0 ms, default: 5476 ms, project: 566 ms

## Configure project (287 projects, sum 22207 ms)

| Project | ms |
|---|---|
| :ant | 524 |
| :ant-api | 508 |
| :ant-impl | 446 |
| :docs | 431 |
| :ant-worker | 381 |
| :base-compiler-worker | 380 |
| : | 362 |
| :architecture-test | 349 |
| :antlr | 339 |
| :core | 330 |
| :api-metadata | 323 |
| :declarative-dsl-core | 314 |
| :base-asm | 289 |
| :dependency-management-serialization-codecs | 181 |
| :plugins-java | 165 |
| :declarative-dsl-tooling-builders | 155 |
| :declarative-dsl-provider | 151 |
| :plugins-jvm-test-fixtures | 150 |
| :declarative-dsl-evaluator | 148 |
| :precondition-tester | 146 |
| :plugins-version-catalog | 145 |
| :software-diagnostics | 141 |
| :encryption-services | 139 |
| :declarative-dsl-tooling-models | 137 |
| :plugins-jvm-test-suite | 135 |
| :problems | 130 |
| :soak | 127 |
| :kotlin-dsl | 126 |
| :core-kotlin-extensions | 120 |
| :dependency-management-resources | 120 |
| :service-registry-impl | 119 |
| :service-registry-builder | 118 |
| :configuration-cache | 117 |
| :problems-api | 117 |
| :distributions-full | 116 |
| :kotlin-dsl-integ-tests | 116 |
| :core-serialization-codecs | 115 |
| :java-compiler-worker | 115 |
| :plugins-model-native | 114 |
| :core-flow-services-api | 110 |

## Tasks (651 executed, sum 1419 ms)

| Task | ms |
|---|---|
| :build-logic:uber-plugins:compileKotlin | 45 |
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 42 |
| :build-logic:integration-testing-kotlin:compileKotlin | 36 |
| :build-logic:integration-testing:compileKotlin | 25 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 25 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 22 |
| :build-logic:buildquality:compileKotlin | 20 |
| :build-logic:documentation:compileGroovy | 17 |
| :build-logic:packaging:compileKotlin | 17 |
| :build-logic:jvm:compileKotlin | 16 |
| :build-logic:binary-compatibility:compileKotlin | 16 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 16 |
| :build-logic:integration-testing:generatePrecompiledScriptPluginAccessors | 15 |
| :build-logic:performance-testing:compileKotlin | 15 |
| :build-logic-commons:gradle-plugin:compileKotlin | 14 |
| :build-logic:kotlin-dsl:compileKotlin | 14 |
| :build-logic:lifecycle:compileKotlin | 13 |
| :build-logic:profiling:compileKotlin | 13 |
| :build-logic:buildquality:generatePrecompiledScriptPluginAccessors | 13 |
| :build-logic-settings:build-environment:compileKotlin | 11 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 11 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 11 |
| :build-logic:integration-testing:generateExternalPluginSpecBuilders | 11 |
| :build-logic:profiling:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:buildquality:compilePluginsBlocks | 11 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 10 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 10 |
| :build-logic:dependency-modules:compilePluginsBlocks | 10 |
| :build-logic:root-build:compileKotlin | 10 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 10 |
| :build-logic-commons:basics:generateExternalPluginSpecBuilders | 9 |
| :build-logic:lifecycle:compilePluginsBlocks | 9 |
| :build-logic:dependency-modules:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic:idea:compileKotlin | 9 |
| :build-logic:integration-testing:compilePluginsBlocks | 9 |
| :build-logic:root-build:generatePrecompiledScriptPluginAccessors | 9 |
| :build-logic-commons:publishing:compileKotlin | 9 |
| :build-logic:kotlin-dsl:compilePluginsBlocks | 9 |
| :build-logic:uber-plugins:compilePluginsBlocks | 9 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 8 |
