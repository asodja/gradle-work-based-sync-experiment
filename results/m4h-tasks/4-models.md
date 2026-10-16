Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4h-tasks/4-trace-log.txt`

Overall span: 14543 ms, 616774 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 5109 | 5109 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 356 | 28 | 0 |
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 284 | 284 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 43 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 31 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 27 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 24 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 21 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 12 | 2 | 0 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 3 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 2 | 1 | 0 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 2 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 1 | 1 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 1 ms, default: 5393 ms, project: 595 ms

## Configure project (287 projects, sum 20665 ms)

| Project | ms |
|---|---|
| : | 455 |
| :base-compiler-worker | 393 |
| :architecture-test | 366 |
| :docs | 341 |
| :antlr | 330 |
| :ant-worker | 326 |
| :base-asm | 288 |
| :ant | 268 |
| :ant-api | 253 |
| :ant-impl | 236 |
| :performance | 236 |
| :declarative-dsl-core | 232 |
| :api-metadata | 225 |
| :core | 219 |
| :stdlib-serialization-codecs | 196 |
| :stdlib-kotlin-extensions | 192 |
| :snapshots | 188 |
| :software-diagnostics | 170 |
| :test-kit | 169 |
| :start-parameter | 168 |
| :testing-base | 161 |
| :plugins-jvm-test-fixtures | 159 |
| :plugins-java | 149 |
| :plugins-model-native | 144 |
| :internal-instrumentation-api | 142 |
| :internal-integ-testing | 136 |
| :distributions-full | 135 |
| :plugins-test-report-aggregation | 134 |
| :test-suites-base | 133 |
| :internal-distribution-testing | 131 |
| :instrumentation-agent-services | 129 |
| :plugins-jvm-test-suite | 121 |
| :plugins-java-library | 120 |
| :internal-instrumentation-processor | 114 |
| :process-services-api | 114 |
| :internal-testing | 111 |
| :plugins-version-catalog | 111 |
| :toolchains-jvm | 111 |
| :scala-compiler-worker | 110 |
| :internal-performance-testing | 105 |

## Tasks (651 executed, sum 2081 ms)

| Task | ms |
|---|---|
| :build-logic:cleanup:generateExternalPluginSpecBuilders | 153 |
| :build-logic:build-update-utils:generateExternalPluginSpecBuilders | 51 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 36 |
| :declarative-dsl-api:ideProjectModel | 34 |
| :daemon-services:ideProjectModel | 33 |
| :declarative-dsl-core:ideProjectModel | 33 |
| :build-logic:uber-plugins:compileKotlin | 32 |
| :declarative-dsl-evaluator:ideProjectModel | 32 |
| :declarative-dsl-tooling-builders:ideProjectModel | 31 |
| :dependency-management:ideProjectModel | 31 |
| :declarative-dsl-provider:ideProjectModel | 30 |
| :declarative-dsl-tooling-models:ideProjectModel | 30 |
| :build-logic:dependency-modules:generatePrecompiledScriptPluginAccessors | 21 |
| :build-logic:idea:compileKotlin | 21 |
| :build-logic:lifecycle:compilePluginsBlocks | 20 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 19 |
| :build-logic:jvm:compileKotlin | 18 |
| :build-logic:cleanup:compileJava | 18 |
| :build-logic:documentation:compileGroovy | 17 |
| :build-logic:integration-testing-kotlin:compileKotlin | 16 |
| :build-logic:idea:compilePluginsBlocks | 15 |
| :build-logic:buildquality:compileKotlin | 14 |
| :build-logic:integration-testing:compileKotlin | 13 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 12 |
| :build-logic:documentation:processResources | 12 |
| :build-logic:packaging:compileKotlin | 12 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:profiling:compileKotlin | 11 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 11 |
| :stdlib-java-extensions:ideProjectModel | 11 |
| :build-logic-settings:configuration-cache-compatibility:generateExternalPluginSpecBuilders | 10 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 10 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 10 |
| :build-logic:dependency-modules:compilePluginsBlocks | 10 |
| :build-logic:jvm:compilePluginsBlocks | 10 |
| :build-logic:cleanup:compileKotlin | 10 |
| :build-logic:kotlin-dsl-shared-runtime:compileKotlin | 10 |
| :build-logic:binary-compatibility:compileKotlin | 10 |
| :build-logic:kotlin-dsl:compileKotlin | 10 |
| :stdlib-serialization-codecs:ideProjectModel | 10 |
