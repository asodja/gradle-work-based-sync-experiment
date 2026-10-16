Trace: `/home/agent/projects/gradle-work-based-sync-experiment/results/m4f-tasks/3-trace-log.txt`

Overall span: 27826 ms, 626865 operations.

## Model fetches (wall time of the fetch operation, overlapping when parallel)

| Model | Scope | Count | Total ms | Max ms | Failed |
|---|---|---|---|---|---|
| org.gradle.tooling.model.gradle.GradleBuild | default | 1 | 11794 | 11794 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilDummyModel.DummyModel | default | 1 | 6948 | 6948 | 0 |
| org.jetbrains.plugins.gradle.model.GradleBuildScriptClasspathModel | project | 287 | 395 | 20 | 0 |
| com.intellij.gradle.toolingExtension.impl.ideTask.IdeModelTaskRequest | project | 287 | 36 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.assignment.AssignmentModel | project | 287 | 33 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.allopen.AllOpenModel | project | 287 | 32 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.noarg.NoArgModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.samWithReceiver.SamWithReceiverModel | project | 287 | 30 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.lombok.LombokModel | project | 287 | 24 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.parcelize.ParcelizeGradleModel | project | 287 | 22 | 1 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.PrepareKotlinIdeImportTaskModel | project | 287 | 17 | 2 | 0 |
| org.jetbrains.kotlin.idea.gradleTooling.model.annotation.AnnotationBasedPluginModel | project | 287 | 6 | 1 | 287 |
| org.jetbrains.kotlin.idea.projectModel.KotlinTarget | project | 287 | 5 | 1 | 287 |
| org.jetbrains.kotlin.tooling.core.Extras | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.gradle.idea.kpm.IdeaKpmProjectContainer | project | 287 | 4 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinMPPGradleModel | project | 287 | 3 | 1 | 0 |
| org.jetbrains.kotlin.gradle.idea.tcs.IdeaKotlinDependency | project | 287 | 1 | 1 | 287 |
| org.jetbrains.kotlin.idea.gradleTooling.KotlinDslScriptAdditionalTask | build | 4 | 0 | 0 | 0 |
| com.intellij.gradle.toolingExtension.impl.model.utilTurnOffDefaultTasksModel.TurnOffDefaultTasks | default | 1 | 0 | 0 | 0 |

Totals by scope: build: 0 ms, default: 18742 ms, project: 642 ms

## Configure project (287 projects, sum 39588 ms)

| Project | ms |
|---|---|
| :build-logic-settings | 11504 |
| :architecture-test | 652 |
| :kotlin-dsl | 635 |
| :kotlin-dsl-integ-tests | 572 |
| :ant-api | 558 |
| :antlr | 544 |
| :docs | 462 |
| :ant-impl | 451 |
| :declarative-dsl-core | 425 |
| : | 373 |
| :base-compiler-worker | 368 |
| :ant | 366 |
| :base-asm | 363 |
| :ant-worker | 361 |
| :api-metadata | 335 |
| :declarative-dsl-evaluator | 332 |
| :distributions-full | 267 |
| :performance | 246 |
| :internal-build-reports | 219 |
| :public-api | 207 |
| :build-cache | 196 |
| :distributions-core | 196 |
| :enterprise-plugin-performance | 194 |
| :distributions-basics | 192 |
| :concurrent | 178 |
| :isolated-action-services | 172 |
| :kotlin-dsl-plugins | 168 |
| :model-groovy | 165 |
| :configuration-cache-base | 157 |
| :native | 155 |
| :configuration-cache | 153 |
| :client-services | 151 |
| :tooling-api | 151 |
| :distributions-jvm | 146 |
| :precondition-tester | 144 |
| :core | 141 |
| :classpath | 136 |
| :classloaders | 135 |
| :plugins-test-report-aggregation | 135 |
| :internal-instrumentation-api | 134 |

## Tasks (651 executed, sum 1676 ms)

| Task | ms |
|---|---|
| :build-logic:performance-testing:compileGroovy | 36 |
| :build-logic:integration-testing-kotlin:generateExternalPluginSpecBuilders | 36 |
| :build-logic:uber-plugins:compileKotlin | 31 |
| :build-logic-settings:version-catalogs:compilePluginsBlocks | 30 |
| :build-logic:kotlin-dsl:generateExternalPluginSpecBuilders | 29 |
| :build-logic:dependency-modules:generateExternalPluginSpecBuilders | 28 |
| :build-logic-settings:configuration-cache-compatibility:compilePluginsBlocks | 27 |
| :build-logic-settings:build-environment:compilePluginsBlocks | 27 |
| :build-logic:binary-compatibility:compileJava | 27 |
| :build-logic-settings:default-settings-plugins:compilePluginsBlocks | 25 |
| :build-logic-commons:publishing:generatePrecompiledScriptPluginAccessors | 25 |
| :build-logic:kotlin-dsl-shared-runtime:generatePomProperties | 24 |
| :build-logic:integration-testing-kotlin:compileKotlin | 22 |
| :build-logic-commons:module-identity:compilePluginsBlocks | 21 |
| :build-logic:integration-testing:compileKotlin | 20 |
| :build-logic:cleanup:generatePrecompiledScriptPluginAccessors | 18 |
| :build-logic:uber-plugins:generatePrecompiledScriptPluginAccessors | 18 |
| :build-logic:integration-testing-kotlin:compilePluginsBlocks | 18 |
| :build-logic-settings:configuration-cache-compatibility:compileKotlin | 17 |
| :build-logic:documentation:compileGroovy | 17 |
| :build-logic:buildquality:compileKotlin | 15 |
| :build-logic-settings:architecture-docs:generateExternalPluginSpecBuilders | 13 |
| :build-logic:packaging:compileKotlin | 13 |
| :build-logic-settings:default-settings-plugins:compileKotlin | 12 |
| :build-logic:buildquality:compilePluginsBlocks | 12 |
| :build-logic:integration-testing-kotlin:generatePrecompiledScriptPluginAccessors | 12 |
| :declarative-dsl-tooling-builders:ideProjectModel | 12 |
| :declarative-dsl-tooling-models:ideProjectModel | 12 |
| :build-logic-commons:gradle-plugin:compileKotlin | 11 |
| :build-logic:lifecycle:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:idea:generatePrecompiledScriptPluginAccessors | 11 |
| :build-logic:jvm:compileKotlin | 11 |
| :build-logic:build-update-utils:generateExternalPluginSpecBuilders | 11 |
| :declarative-dsl-provider:ideProjectModel | 11 |
| :dependency-management-resources:ideProjectModel | 11 |
| :build-logic-settings:default-settings-plugins:generateExternalPluginSpecBuilders | 10 |
| :build-logic:profiling:compileKotlin | 10 |
| :build-logic:kotlin-dsl:compileKotlin | 10 |
| :build-logic-commons:build-platform-plugin:compilePluginsBlocks | 9 |
| :build-logic:lifecycle:compileKotlin | 9 |
